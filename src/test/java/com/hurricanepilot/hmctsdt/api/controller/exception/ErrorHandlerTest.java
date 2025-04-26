package com.hurricanepilot.hmctsdt.api.controller.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.hurricanepilot.hmctsdt.api.model.ErrorDetail;
import com.hurricanepilot.hmctsdt.service.exception.TaskNotFoundException;
import com.hurricanepilot.hmctsdt.service.exception.TaskStatusInvalidException;
import com.hurricanepilot.hmctsdt.service.exception.TaskUpdateNotSupportedException;

class ErrorHandlerTest {

    ErrorHandler errorHandler = new ErrorHandler();

	@Test
	void testHandleError() {
        Exception e = new RuntimeException("Internal server error");

        ResponseEntity<ErrorDetail> response = errorHandler.handleError(e);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorDetail error = response.getBody();
        assertNotNull(error);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), error.getStatus());
        assertEquals("Internal Server Error", error.getReason());
	}

	@Test
	void testHandleTaskStatusInvalid() {
        TaskStatusInvalidException e = new TaskStatusInvalidException("Invalid status");

        ResponseEntity<ErrorDetail> response = errorHandler.handleInvalidStatus(e);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorDetail error = response.getBody();
        assertNotNull(error);
        assertEquals(HttpStatus.BAD_REQUEST.value(), error.getStatus());
        assertEquals("Invalid status", error.getReason());
	}

	@Test
	void testHandleTaskUpdateNotSupported() {
        TaskUpdateNotSupportedException e = new TaskUpdateNotSupportedException("Update not supported");

        ResponseEntity<ErrorDetail> response = errorHandler.handleInvalidStatus(e);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorDetail error = response.getBody();
        assertNotNull(error);
        assertEquals(HttpStatus.BAD_REQUEST.value(), error.getStatus());
        assertEquals("Update not supported", error.getReason());
	}

	@Test
	void testHandleTaskNotFound() {
        TaskNotFoundException e = new TaskNotFoundException("Task not found");

        ResponseEntity<ErrorDetail> response = errorHandler.handleTaskNotFound(e);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorDetail error = response.getBody();
        assertNotNull(error);
        assertEquals(HttpStatus.NOT_FOUND.value(), error.getStatus());
        assertEquals("Task not found", error.getReason());
	}

	@Test
	void testHandleValidationError() {
        FieldError fieldError1 = new FieldError("task1", "title", "Title is missing");
        FieldError fieldError2 = new FieldError("task1", "description", "Description is too long");

        var exceptionMock = mock(MethodArgumentNotValidException.class);
        var bindingResultMock = mock(BindingResult.class);

        when(exceptionMock.getBindingResult()).thenReturn(bindingResultMock);
        when(bindingResultMock.getAllErrors()).thenReturn(List.of(fieldError1,fieldError2));
        
        ResponseEntity<ErrorDetail> response = errorHandler.handleValidationError(exceptionMock);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorDetail error = response.getBody();
        assertNotNull(error);
        assertEquals(HttpStatus.BAD_REQUEST.value(), error.getStatus());
        var reason = error.getReason();
        assertTrue(reason.contains("title: Title is missing"));
	}

    @Test
    void testHandleValidationErrorNonField() {
        var fieldError1 = new ObjectError("task1", "Title is missing");
        var fieldError2 = new FieldError("task1", "description", "Description is too long");

        var exceptionMock = mock(MethodArgumentNotValidException.class);
        var bindingResultMock = mock(BindingResult.class);

        when(exceptionMock.getBindingResult()).thenReturn(bindingResultMock);
        when(bindingResultMock.getAllErrors()).thenReturn(List.of(fieldError1,fieldError2));
        
        ResponseEntity<ErrorDetail> response = errorHandler.handleValidationError(exceptionMock);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorDetail error = response.getBody();
        assertNotNull(error);
        assertEquals(HttpStatus.BAD_REQUEST.value(), error.getStatus());
        var reason = error.getReason();
        assertTrue(reason.contains("task1: Title is missing"));
        assertTrue(reason.contains("Description is too long"));
	}
}
