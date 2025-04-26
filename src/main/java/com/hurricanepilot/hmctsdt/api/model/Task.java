package com.hurricanepilot.hmctsdt.api.model;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hurricanepilot.hmctsdt.constants.Status;
import com.hurricanepilot.hmctsdt.persistence.entity.TaskEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Defines a Task object for transmission between the web application and the REST service")
public class Task {

    @Schema(description = "The ID of the Task object - assigned by the server during creation", accessMode = AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Title must be specified")
    @Size(min=1, max = 80, message = "Title should not be more than 80 characters")
    @Schema(description = "The title of the Task object", requiredMode = RequiredMode.REQUIRED)
    private String title;
    @Size(max = 2000, message = "Description should not be more than 2000 characters")
    @Schema(description = "The description of the Task object", requiredMode = RequiredMode.NOT_REQUIRED)
    private String description;
    @NotNull
    @Builder.Default
    @Schema(description = "The status of the Task object", requiredMode = RequiredMode.REQUIRED, defaultValue = "NEW")
    private Status status = Status.NEW;
    @NotNull
    @Schema(description = "The due date/time of the Task object", requiredMode = RequiredMode.REQUIRED)
    private ZonedDateTime dueDateTime;

    public static Task fromEntity(TaskEntity task) {
        return new Task(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDueDateTime());
    }

    public TaskEntity toEntity() {
        var task = new TaskEntity(this.title, this.description,
            this.dueDateTime);
        task.setStatus(this.status);
        return task;
    }
}