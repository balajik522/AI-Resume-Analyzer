package com.balaji.atsanalyzer.service;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ResumeAnalysisServiceTest {

    private final ResumeAnalysisService service = new ResumeAnalysisService();

    @Test
    void analyze_returnsExpectedFields() throws Exception {
        String content = "John Doe\nEmail: john.doe@example.com\nPhone: +1-555-123-4567\nSkills: Java, Spring Boot\n";
        MockMultipartFile file = new MockMultipartFile("file", "resume.txt", "text/plain", content.getBytes());
        Map<String, Object> res = service.analyze(file);

        assertThat(res).containsKeys("fileName", "size", "textSnippet", "wordCount", "emails", "phones", "skills");
        assertThat(res.get("fileName")).isEqualTo("resume.txt");
        assertThat((Integer) res.get("wordCount")).isGreaterThan(0);

        @SuppressWarnings("unchecked")
        List<String> emails = (List<String>) res.get("emails");
        assertThat(emails).contains("john.doe@example.com");

        @SuppressWarnings("unchecked")
        List<String> phones = (List<String>) res.get("phones");
        assertThat(phones).isNotEmpty();

        @SuppressWarnings("unchecked")
        List<String> skills = (List<String>) res.get("skills");
        assertThat(skills).contains("java", "spring boot");
    }
}
