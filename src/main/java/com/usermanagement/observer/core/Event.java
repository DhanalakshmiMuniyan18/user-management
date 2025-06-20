/**
 * Immutable event base class for Observer Pattern.
 * @author Saravanamuthukumar S
 */
package com.usermanagement.observer.core;

import java.time.Instant;
import java.util.UUID;

public abstract class Event {
    private final String eventId = UUID.randomUUID().toString();
    private final Instant timestamp = Instant.now();

    public String getEventId() { return eventId; }
    public Instant getTimestamp() { return timestamp; }
} 