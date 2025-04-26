package com.hurricanepilot.hmctsdt.persistence.repository;

import org.springframework.data.repository.CrudRepository;

import com.hurricanepilot.hmctsdt.persistence.entity.TaskEntity;

public interface TaskRepository extends CrudRepository<TaskEntity, Long> { /* magic! */}
