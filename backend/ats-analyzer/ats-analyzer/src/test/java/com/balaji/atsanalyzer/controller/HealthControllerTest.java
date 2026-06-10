package com.balaji.atsanalyzer.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HealthControllerTest {

    private final HealthController controller = new HealthController();

    @Test
    void health_returnsExpectedString() {
        String h = controller.health();
        assertThat(h).isEqualTo("ATS Backend is Running");
    }
}
