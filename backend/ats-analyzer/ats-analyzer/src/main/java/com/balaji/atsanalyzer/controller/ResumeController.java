package com.balaji.atsanalyzer.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

import com.balaji.atsanalyzer.service.ResumeAnalysisService;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {
    @Autowired
    private ResumeAnalysisService analysisService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(
            @RequestParam("file") MultipartFile file) throws IOException {

        Map<String, Object> response = new HashMap<>();

        if (file == null || file.isEmpty()) {
            response.put("error", "File is missing or empty");
            return ResponseEntity.badRequest().body(response);
        }

        byte[] bytes = file.getBytes();

        response.put("fileName", file.getOriginalFilename());
        response.put("size", bytes.length);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/match")
    public ResponseEntity<?> matchResume(
            @RequestParam(value = "resume", required = false) MultipartFile resume,
            @RequestParam(value = "job", required = false) MultipartFile job,
            @RequestParam(value = "jobText", required = false) String jobText) throws IOException {

        Map<String, Object> response = new HashMap<>();

        if (resume == null || resume.isEmpty()) {
            response.put("error", "Resume file is required");
            return ResponseEntity.badRequest().body(response);
        }

        Map<String, Object> result;

        if (job != null && !job.isEmpty()) {
            result = analysisService.match(resume, job);
        } else if (jobText != null && !jobText.isBlank()) {
            result = analysisService.match(resume, jobText);
        } else {
            response.put("error", "Either job file or jobText must be provided");
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(result);
    }
}