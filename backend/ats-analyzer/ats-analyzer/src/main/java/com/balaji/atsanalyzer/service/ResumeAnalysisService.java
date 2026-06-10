package com.balaji.atsanalyzer.service;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ResumeAnalysisService {

    private final Tika tika = new Tika();

    public Map<String, Object> analyze(MultipartFile file) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("fileName", file.getOriginalFilename());
        result.put("size", file.getSize());

        try (InputStream is = file.getInputStream()) {
            String text = tika.parseToString(is);

            int wordCount = countWords(text);
            List<String> emails = extractEmails(text);
            List<String> phones = extractPhones(text);
            List<String> skills = detectSkills(text);

            int atsScore = calculateATSScore(
                    wordCount,
                    skills,
                    emails,
                    phones
            );

            result.put("textSnippet",
                    text.length() > 500
                            ? text.substring(0, 500) + "..."
                            : text);

            result.put("wordCount", wordCount);
            result.put("emails", emails);
            result.put("phones", phones);
            result.put("skills", skills);
            result.put("atsScore", atsScore);

        } catch (Exception e) {
            result.put("error", e.getMessage());
        }

        return result;
    }

    public Map<String, Object> match(MultipartFile resumeFile, MultipartFile jobDescFile) {
        Map<String, Object> result = new LinkedHashMap<>();

        try (InputStream ris = resumeFile.getInputStream();
             InputStream jis = jobDescFile.getInputStream()) {

            String resumeText = tika.parseToString(ris);
            String jobText = tika.parseToString(jis);

            String[] skillsArr = {
                    "java", "spring", "spring boot",
                    "python", "javascript", "react",
                    "aws", "docker", "kubernetes",
                    "sql", "mysql", "nosql",
                    "mongodb", "c#", "c++", "git"
            };

            Set<String> jobSkills = new LinkedHashSet<>();
            Set<String> resumeSkills = new LinkedHashSet<>();

            String lowerJob = jobText.toLowerCase();
            String lowerResume = resumeText.toLowerCase();

            for (String s : skillsArr) {
                if (lowerJob.contains(s))
                    jobSkills.add(s);

                if (lowerResume.contains(s))
                    resumeSkills.add(s);
            }

            Set<String> matchedSkills = new LinkedHashSet<>(resumeSkills);
            matchedSkills.retainAll(jobSkills);

            Set<String> missingSkills = new LinkedHashSet<>(jobSkills);
            missingSkills.removeAll(resumeSkills);

            Set<String> jobTokens = tokenizeForMatching(jobText);
            Set<String> resumeTokens = tokenizeForMatching(resumeText);

            Set<String> matchedKeywords = new LinkedHashSet<>(resumeTokens);
            matchedKeywords.retainAll(jobTokens);

            int score = 0;

            if (!jobSkills.isEmpty()) {
                score = (int) Math.round(
                        10.0 * matchedSkills.size() / jobSkills.size()
                );
            } else if (!jobTokens.isEmpty()) {
                double ratio =
                        (double) matchedKeywords.size() / jobTokens.size();

                score = (int) Math.round(
                        Math.min(1.0, ratio) * 10.0
                );
            }

            result.put("scoreOutOf10",
                    Math.max(0, Math.min(10, score)));

            result.put("matchedSkills",
                    new ArrayList<>(matchedSkills));

            result.put("missingSkills",
                    new ArrayList<>(missingSkills));

            result.put("matchedKeywords",
                    new ArrayList<>(matchedKeywords));

            result.put("jobSkillsDetected",
                    new ArrayList<>(jobSkills));

            result.put("resumeSkillsDetected",
                    new ArrayList<>(resumeSkills));

            result.put("resumeAnalysis",
                    analyze(resumeFile));

        } catch (Exception e) {
            result.put("error", e.getMessage());
        }

        return result;
    }

        public Map<String, Object> match(MultipartFile resumeFile, String jobText) {
        Map<String, Object> result = new LinkedHashMap<>();

        try (InputStream ris = resumeFile.getInputStream()) {

            String resumeText = tika.parseToString(ris);
            String jobTextStr = jobText == null ? "" : jobText;

            String[] skillsArr = {
                "java", "spring", "spring boot",
                "python", "javascript", "react",
                "aws", "docker", "kubernetes",
                "sql", "mysql", "nosql",
                "mongodb", "c#", "c++", "git"
            };

            Set<String> jobSkills = new LinkedHashSet<>();
            Set<String> resumeSkills = new LinkedHashSet<>();

            String lowerJob = jobTextStr.toLowerCase();
            String lowerResume = resumeText.toLowerCase();

            for (String s : skillsArr) {
            if (lowerJob.contains(s))
                jobSkills.add(s);

            if (lowerResume.contains(s))
                resumeSkills.add(s);
            }

            Set<String> matchedSkills = new LinkedHashSet<>(resumeSkills);
            matchedSkills.retainAll(jobSkills);

            Set<String> missingSkills = new LinkedHashSet<>(jobSkills);
            missingSkills.removeAll(resumeSkills);

            Set<String> jobTokens = tokenizeForMatching(jobTextStr);
            Set<String> resumeTokens = tokenizeForMatching(resumeText);

            Set<String> matchedKeywords = new LinkedHashSet<>(resumeTokens);
            matchedKeywords.retainAll(jobTokens);

            int score = 0;

            if (!jobSkills.isEmpty()) {
            score = (int) Math.round(
                10.0 * matchedSkills.size() / jobSkills.size()
            );
            } else if (!jobTokens.isEmpty()) {
            double ratio =
                (double) matchedKeywords.size() / jobTokens.size();

            score = (int) Math.round(
                Math.min(1.0, ratio) * 10.0
            );
            }

            result.put("scoreOutOf10",
                Math.max(0, Math.min(10, score)));

            result.put("matchedSkills",
                new ArrayList<>(matchedSkills));

            result.put("missingSkills",
                new ArrayList<>(missingSkills));

            result.put("matchedKeywords",
                new ArrayList<>(matchedKeywords));

            result.put("jobSkillsDetected",
                new ArrayList<>(jobSkills));

            result.put("resumeSkillsDetected",
                new ArrayList<>(resumeSkills));

            result.put("resumeAnalysis",
                analyze(resumeFile));

        } catch (Exception e) {
            result.put("error", e.getMessage());
        }

        return result;
        }

    private Set<String> tokenizeForMatching(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptySet();
        }

        String cleaned =
                text.toLowerCase()
                        .replaceAll("[^a-z0-9+#\\s]", " ");

        String[] parts = cleaned.split("\\s+");

        Set<String> tokens = new LinkedHashSet<>();

        List<String> stopWords = Arrays.asList(
                "the", "and", "a", "an",
                "to", "for", "with",
                "in", "on", "of",
                "is", "are", "be",
                "by", "as", "that", "this"
        );

        for (String p : parts) {
            if (p.length() < 3)
                continue;

            if (stopWords.contains(p))
                continue;

            tokens.add(p);
        }

        return tokens;
    }

    private int countWords(String text) {
        if (text == null || text.isEmpty())
            return 0;

        return text.trim().split("\\s+").length;
    }

    private List<String> extractEmails(String text) {
        List<String> emails = new ArrayList<>();

        Pattern p = Pattern.compile(
                "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}"
        );

        Matcher m = p.matcher(text);

        while (m.find()) {
            emails.add(m.group());
        }

        return emails;
    }

    private List<String> extractPhones(String text) {
        List<String> phones = new ArrayList<>();

        Pattern p = Pattern.compile(
                "(\\+?\\d{1,3}[-.\\s]?)?(?:\\(?\\d{2,4}\\)?[-.\\s]?)?\\d{3,4}[-.\\s]?\\d{3,4}"
        );

        Matcher m = p.matcher(text);

        while (m.find()) {
            phones.add(m.group().trim());
        }

        return phones;
    }

    private List<String> detectSkills(String text) {
        String lower = text.toLowerCase();

        String[] skills = {
                "java", "spring", "spring boot",
                "python", "javascript", "react",
                "aws", "docker", "kubernetes",
                "sql", "mysql", "nosql",
                "mongodb", "c#", "c++", "git"
        };

        List<String> found = new ArrayList<>();

        for (String s : skills) {
            if (lower.contains(s)) {
                found.add(s);
            }
        }

        return found;
    }

    private int calculateATSScore(
            int wordCount,
            List<String> skills,
            List<String> emails,
            List<String> phones) {

        int score = 0;

        if (wordCount > 200)
            score += 20;

        if (skills.size() >= 5)
            score += 40;

        if (!emails.isEmpty())
            score += 20;

        if (!phones.isEmpty())
            score += 20;

        return Math.min(score, 100);
    }
}