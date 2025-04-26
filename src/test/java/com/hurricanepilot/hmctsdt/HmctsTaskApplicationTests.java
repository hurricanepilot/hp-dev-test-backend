package com.hurricanepilot.hmctsdt;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;

class HmctsTaskApplicationTests {

    @Test
    void ensureSpringApplicationIsBootstrapped() {
        // we need to ensure this mock gets closed as testing for coverage
        // in vscode leaves the mock bound, breaking subsequent tests
        try (var springApp = mockStatic(SpringApplication.class)) {
            var args = new String [] {""};
            HmctsTaskApplication.main(args);
            springApp.verify(() -> SpringApplication.run(HmctsTaskApplication.class, args), times(1));
        }
    }

}
