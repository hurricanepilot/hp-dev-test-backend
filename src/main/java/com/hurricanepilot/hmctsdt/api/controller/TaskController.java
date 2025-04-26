package com.hurricanepilot.hmctsdt.api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.hurricanepilot.hmctsdt.api.model.Error;
import com.hurricanepilot.hmctsdt.api.model.Task;
import com.hurricanepilot.hmctsdt.service.TaskService;
import com.hurricanepilot.hmctsdt.service.exception.TaskNotFoundException;
import com.hurricanepilot.hmctsdt.service.exception.TaskStatusInvalidException;
import com.hurricanepilot.hmctsdt.service.exception.TaskUpdateNotSupportedException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@CrossOrigin
@RestController
@RequestMapping(value = "/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Task API")
public class TaskController {

    private final TaskService taskService;

    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create new Task", description = "Stores the given Task data and return the ID of the stored task")
    @ApiResponse(responseCode = "201", description = "Task successfully created")
    public ResponseEntity<Long> createTask(@Valid @RequestBody Task task) {
        var result = taskService.create(task.toEntity());
        var location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(result).toUri();

        return ResponseEntity.created(location).body(result);
    }

    @GetMapping(path = "/{id}")
    @Operation(summary = "Retrieve a Task", description = "Retrieves the Task related to the given ID")
    @ApiResponse(responseCode = "200", description = "The retrieved Task", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Task.class)))
    @ApiResponse(responseCode = "404", description = "Task not found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Error.class)))
    public ResponseEntity<Task> retrieveTask(@PathVariable Long id) throws TaskNotFoundException {
        return ResponseEntity.ok().body(Task.fromEntity(taskService.find(id)));
    }

    @GetMapping(path = "")
    @Operation(summary = "Retrieve all Tasks", description = "Retrieves all Tasks")
    @ApiResponse(responseCode = "200", description = "The retrieved Tasks", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = Error.class))))
    public ResponseEntity<List<Task>> retrieveAllTasks() {
        return ResponseEntity.ok().body(taskService.retrieveAll().stream().map(Task::fromEntity).toList());
    }

    @DeleteMapping(path = "/{id}")
    @Operation(summary = "Delete a Task", description = "Deletes the Task related to the given ID")
    @ApiResponse(responseCode = "204", description = "Task Deleted")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        this.taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(path = "/{id}")
    @Operation(summary = "Update a Task", description = "Updates the task for the given ID")
    @ApiResponse(responseCode = "200", description = "Task updated")
    @ApiResponse(responseCode = "400", description = "Invalid update specified", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Error.class)))
    @ApiResponse(responseCode = "404", description = "Task not found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Error.class)))
    public ResponseEntity<Void> updateTaskStatus(@PathVariable Long id, @RequestBody Map<String, String> taskUpdates)
            throws TaskNotFoundException, TaskStatusInvalidException, TaskUpdateNotSupportedException {
        // can't be null as taskUpdates is a required field
        if (taskUpdates.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No updates specified");
        }
        this.taskService.updateTask(id, taskUpdates);
        return ResponseEntity.ok().build();
    }
}
