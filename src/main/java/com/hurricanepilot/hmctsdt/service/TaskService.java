package com.hurricanepilot.hmctsdt.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.hurricanepilot.hmctsdt.constants.Status;
import com.hurricanepilot.hmctsdt.persistence.entity.TaskEntity;
import com.hurricanepilot.hmctsdt.persistence.repository.TaskRepository;
import com.hurricanepilot.hmctsdt.service.exception.TaskNotFoundException;
import com.hurricanepilot.hmctsdt.service.exception.TaskStatusInvalidException;
import com.hurricanepilot.hmctsdt.service.exception.TaskUpdateNotSupportedException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public Long create(TaskEntity task) {
        return this.taskRepository.save(task).getId();
    }

    public TaskEntity find(Long id) throws TaskNotFoundException {
        return this.taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found for ID: " + id));
    }

    public List<TaskEntity> retrieveAll() {
        var taskSet = new ArrayList<TaskEntity>();
        this.taskRepository.findAll().forEach(taskSet::add);
        return taskSet;
    }

    @Transactional // this is the only non-atomic database update
    public void updateTask(Long id, Map<String, String> updates)
            throws TaskNotFoundException, TaskStatusInvalidException, TaskUpdateNotSupportedException {

        var task = find(id);

        for (var entry : updates.entrySet()) {
            if ("status".equalsIgnoreCase(entry.getKey())) {
                updateTaskStatus(task, Status.valueOf(entry.getValue()));
            } else {
                throw new TaskUpdateNotSupportedException(entry.getKey());
            }
        }
    }

    private void updateTaskStatus(TaskEntity task, Status newStatus) throws TaskStatusInvalidException {
        var currentStatus = task.getStatus();
        if (currentStatus != newStatus) {
            // the one thing we won't allow is putting a status back in to NEW.
            // Moving between IN_PROGRESS, DEFERRED and COMPLETED are being
            // considered as acceptable.
            if (newStatus == Status.NEW) {
                throw new TaskStatusInvalidException("Task cannot be set to NEW once work has commenced.");
            }
            task.setStatus(newStatus);
            this.taskRepository.save(task);
        }
    }

    public void delete(Long id) {
        this.taskRepository.deleteById(id);
    }
}
