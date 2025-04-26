package com.hurricanepilot.hmctsdt.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Schema(description = "The schema used for all Error responses from the REST service")
public class Error {
    @Schema(description = "The HTTP status code for the error", accessMode = AccessMode.READ_ONLY)
    private final int status;
    @Schema(description = "The error detail message", accessMode = AccessMode.READ_ONLY)
    private final String reason;
}
