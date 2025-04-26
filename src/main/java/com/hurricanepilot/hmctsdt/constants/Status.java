package com.hurricanepilot.hmctsdt.constants;

public enum Status {
    /** Ready to be started */
    NEW,
    /** Currently being actively worked on */
    IN_PROGRESS,
    /** Specifically deferred/parked */
    DEFERRED,
    /** Completed */
    COMPLETED;
}