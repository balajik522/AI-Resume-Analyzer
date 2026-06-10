package com.balaji.atsanalyzer.controller;

import com.balaji.atsanalyzer.service.ResumeAnalysisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.lang.reflect.Field;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ResumeControllerTest {

    private ResumeAnalysisService analysisService;
    private ResumeController controller;

    @BeforeEach
    void setUp() throws Exception {
        analysisService = mock(ResumeAnalysisService.class);
        controller = new ResumeController();
        Field f = ResumeController.class.getDeclaredField("analysisService");
        f.setAccessible(true);
        f.set(controller, analysisService);
    }

    @Test
    void uploadResume_emptyFile_returnsBadRequest() throws Exception {
        MockMultipartFile empty = new MockMultipartFile("file", "", "text/plain", new byte[0]);
        ResponseEntity<?> resp = controller.uploadResume(empty);
        assertThat(resp.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    void uploadResume_validFile_returnsOk() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "resume.txt", "text/plain", "content".getBytes());
        when(analysisService.analyze(file)).thenReturn(Map.of("ok", true));

        ResponseEntity<?> resp = controller.uploadResume(file);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody()).isInstanceOf(Map.class);
    }
}
