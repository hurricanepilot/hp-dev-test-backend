package com.hurricanepilot.hmctsdt.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hurricanepilot.hmctsdt.api.controller.TaskController;
import com.hurricanepilot.hmctsdt.api.model.Task;
import com.hurricanepilot.hmctsdt.config.HmctsTestConfiguration;
import com.hurricanepilot.hmctsdt.constants.Status;
import com.hurricanepilot.hmctsdt.persistence.entity.TaskEntity;
import com.hurricanepilot.hmctsdt.service.TaskService;
import com.hurricanepilot.hmctsdt.service.exception.TaskNotFoundException;
import com.hurricanepilot.hmctsdt.service.exception.TaskStatusInvalidException;
import com.hurricanepilot.hmctsdt.service.exception.TaskUpdateNotSupportedException;

@AutoConfigureRestDocs
@Import(HmctsTestConfiguration.class)
@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @Test
    void createTaskRequiredFieldsOnly() throws Exception {

        var task = Task
                .builder()
                .title("My New Task")
                .dueDateTime(ZonedDateTime.now().plusDays(1)).build();

        when(taskService.create(any(TaskEntity.class))).thenReturn(Long.valueOf(1));

        this.mockMvc
                .perform(RestDocumentationRequestBuilders.post("/tasks")
                        .content(objectMapper.writeValueAsString(task))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("Location"));
    }

    @Test
    void createTaskInvalidTitle() throws Exception {

        var task = Task
                .builder()
                .title("")
                .dueDateTime(ZonedDateTime.now().plusDays(1)).build();

        when(taskService.create(any(TaskEntity.class))).thenReturn(Long.valueOf(1));

        this.mockMvc
                .perform(RestDocumentationRequestBuilders.post("/tasks")
                        .content(objectMapper.writeValueAsString(task))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void createTaskMaxLengthTitle() throws Exception {

        var task = Task
                .builder()
                .title(createStringOfLength(80))
                .dueDateTime(ZonedDateTime.now().plusDays(1)).build();

        when(taskService.create(any(TaskEntity.class))).thenReturn(Long.valueOf(1));

        this.mockMvc
                .perform(RestDocumentationRequestBuilders.post("/tasks")
                        .content(objectMapper.writeValueAsString(task))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void createTaskInvalidDescription() throws Exception {
        var badDescription = createStringOfLength(2001);

        var task = Task
                .builder()
                .title("My New Task")
                .description(new String(badDescription))
                .dueDateTime(ZonedDateTime.now().plusDays(1)).build();

        when(taskService.create(any(TaskEntity.class))).thenReturn(Long.valueOf(1));

        this.mockMvc
                .perform(RestDocumentationRequestBuilders.post("/tasks")
                        .content(objectMapper.writeValueAsString(task))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void createTask() throws Exception {

        var task = Task
                .builder()
                .title("My Task")
                .description("A task that's currently underway")
                .status(Status.IN_PROGRESS)
                .dueDateTime(ZonedDateTime.now().plusDays(2)).build();

        when(taskService.create(any(TaskEntity.class))).thenReturn(Long.valueOf(2));

        this.mockMvc
                .perform(RestDocumentationRequestBuilders.post("/tasks")
                        .content(objectMapper.writeValueAsString(task))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("Location"));
    }

    @Test
    void retrieveTaskById() throws Exception {

        var task = new TaskEntity("Test Task 1", ZonedDateTime.now().plusDays(1));
        task.setId(1L);
        task.setDescription("Description of test task 1");
        task.setStatus(Status.DEFERRED);

        when(taskService.find(Long.valueOf(1))).thenReturn(task);

        this.mockMvc
                .perform(RestDocumentationRequestBuilders.get("/tasks/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
    }

    @Test
    void retrieveTaskByIdNotFound() throws Exception {

        when(taskService.find(Long.valueOf(1))).thenThrow(new TaskNotFoundException("Failed to locate Task for id 1"));

        this.mockMvc
                .perform(RestDocumentationRequestBuilders.get("/tasks/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void retrieveAllTasks() throws Exception {

        var taskList = new ArrayList<TaskEntity>();
        for (int i = 0; i < 10; i++) {
            var task = new TaskEntity("Test Task " + i, ZonedDateTime.now().plusDays(1));
            task.setId(Long.valueOf((long) i));
            taskList.add(task);

        }

        when(taskService.retrieveAll()).thenReturn(taskList);

        this.mockMvc
                .perform(RestDocumentationRequestBuilders.get("/tasks")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[*]", hasSize(10)));

    }

    @Test
    void updateTaskStatus() throws Exception {

        doNothing().when(taskService).updateTask(1L, Map.of("status", Status.COMPLETED.name()));

        this.mockMvc
                .perform(RestDocumentationRequestBuilders.patch("/tasks/{id}", 1)
                        .content(objectMapper.writeValueAsString(Map.of("status", Status.COMPLETED.name())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void updateTaskNotFound() throws Exception {
        doThrow(new TaskNotFoundException("Failed to locate Task for id 1")).when(taskService)
                .updateTask(1L, Map.of("status", Status.COMPLETED.name()));

        this.mockMvc
                .perform(RestDocumentationRequestBuilders.patch("/tasks/{id}", 1)
                        .content(objectMapper.writeValueAsString(Map.of("status", Status.COMPLETED.name())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void updateTaskInvalidStatus() throws Exception {
        doThrow(new TaskStatusInvalidException("Task cannot be set to NEW once work has commenced.")).when(taskService)
                .updateTask(1L, Map.of("status", "NEW"));

        this.mockMvc
                .perform(RestDocumentationRequestBuilders.patch("/tasks/{id}", 1)
                        .content(objectMapper.writeValueAsString(Map.of("status", "NEW")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void updateTaskUnsupportedTaskFragment() throws Exception {
        doThrow(new TaskUpdateNotSupportedException("description")).when(taskService)
                .updateTask(1L, Map.of("description", "wibble"));

        this.mockMvc
                .perform(RestDocumentationRequestBuilders.patch("/tasks/{id}", 1)
                        .content(objectMapper.writeValueAsString(Map.of("description", "wibble")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void updateTaskInvalidTaskFragment() throws Exception {
        doThrow(new TaskUpdateNotSupportedException("description")).when(taskService)
                .updateTask(1L, Map.of("description", "wibble"));

        this.mockMvc
                .perform(RestDocumentationRequestBuilders.patch("/tasks/{id}", 1)
                        .content(objectMapper.writeValueAsString(Collections.emptyMap()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void deleteTask() throws Exception {
        doNothing().when(taskService).delete(1L);

        this.mockMvc
                .perform(RestDocumentationRequestBuilders.delete("/tasks/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    private String createStringOfLength(int len) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        var strChars = new char[len];
        for (int i = 0; i < strChars.length; i++) {
            strChars[i] = chars.charAt(random.nextInt(chars.length()));
        }
        return new String(strChars);
    }
}
