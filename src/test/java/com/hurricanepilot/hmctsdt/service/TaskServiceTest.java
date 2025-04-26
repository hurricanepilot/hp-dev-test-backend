package com.hurricanepilot.hmctsdt.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.hurricanepilot.hmctsdt.constants.Status;
import com.hurricanepilot.hmctsdt.persistence.entity.TaskEntity;
import com.hurricanepilot.hmctsdt.service.exception.TaskNotFoundException;
import com.hurricanepilot.hmctsdt.service.exception.TaskStatusInvalidException;
import com.hurricanepilot.hmctsdt.service.exception.TaskUpdateNotSupportedException;

@SpringBootTest
@Transactional
class TaskServiceTest {

    @Autowired
    TaskService taskService;

    @Test
    void testCreate() {
        var task = new TaskEntity("Task 1", ZonedDateTime.now());
        var id = taskService.create(task);
        assertNotNull(id);
    }

    @Test
    void testDelete() throws TaskNotFoundException {
        var task = new TaskEntity("Task 1", ZonedDateTime.now());
        var id = taskService.create(task);
        assertNotNull(id);

        var result = taskService.find(id);
        assertNotNull(result);
        assertEquals(id, result.getId());

        taskService.delete(id);

        assertThrows(TaskNotFoundException.class, () -> taskService.find(id));
    }

    @Test
    void testFind() throws TaskNotFoundException {
        var task = new TaskEntity("Task 1", ZonedDateTime.now());
        var id = taskService.create(task);
        assertNotNull(id);

        var result = taskService.find(id);
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(task.getTitle(), result.getTitle());

        assertThrows(TaskNotFoundException.class, () -> taskService.find(id + 1));
    }

    @Test
    void testRetrieveAll() {
        var tasks = List.of(
                new TaskEntity("Task 1", ZonedDateTime.now()),
                new TaskEntity("Task 2", ZonedDateTime.now()),
                new TaskEntity("Task 3", ZonedDateTime.now()),
                new TaskEntity("Task 4", ZonedDateTime.now()));
        tasks.forEach(taskService::create);

        var result = taskService.retrieveAll();

        assertNotNull(result);
        assertEquals(tasks.size(), result.size());
    }

    @Test
    void testUpdateTaskStatus()
            throws TaskNotFoundException, TaskStatusInvalidException, TaskUpdateNotSupportedException {
        var task = new TaskEntity("Task 1", ZonedDateTime.now());
        var id = taskService.create(task);
        assertNotNull(id);

        var result = taskService.find(id);
        assertNotNull(result);
        assertEquals(Status.NEW, result.getStatus());

        taskService.updateTask(id, Map.of("status", Status.COMPLETED.name()));

        result = taskService.find(id);
        assertNotNull(result);
        assertEquals(Status.COMPLETED, result.getStatus());

        assertThrows(TaskStatusInvalidException.class,
                () -> taskService.updateTask(id, Map.of("status", Status.NEW.name())));
    }

    @Test
    void testUpdateTaskStatusSameStatusIgnoredIfNew()
            throws TaskNotFoundException, TaskStatusInvalidException, TaskUpdateNotSupportedException {
        var task = new TaskEntity("Task 1", ZonedDateTime.now());
        var id = taskService.create(task);
        assertNotNull(id);

        var result = taskService.find(id);
        assertNotNull(result);
        assertEquals(Status.NEW, result.getStatus());

        taskService.updateTask(id, Map.of("status", Status.NEW.name()));

        result = taskService.find(id);
        assertNotNull(result);
        assertEquals(Status.NEW, result.getStatus());

        taskService.updateTask(id, Map.of("status", Status.COMPLETED.name()));
        result = taskService.find(id);
        assertNotNull(result);
        assertEquals(Status.COMPLETED, result.getStatus());

        assertThrows(TaskStatusInvalidException.class,
                () -> taskService.updateTask(id, Map.of("status", Status.NEW.name())));
    }

    @Test
    void testUpdateTaskOnlyStatus()
            throws TaskNotFoundException {
        var task = new TaskEntity("Task 1", ZonedDateTime.now());
        var id = taskService.create(task);
        assertNotNull(id);

        var result = taskService.find(id);
        assertNotNull(result);
        assertEquals(Status.NEW, result.getStatus());

        assertThrows(TaskUpdateNotSupportedException.class,
                () -> taskService.updateTask(id, Map.of("description", "new description")));
    }
}
