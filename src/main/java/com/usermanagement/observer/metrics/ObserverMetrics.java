/**
 * Utility for recording observer notification metrics.
 * @author Saravanamuthukumar S
 */
package com.usermanagement.observer.metrics;

import com.usermanagement.observer.core.Observer;
import com.usermanagement.observer.core.Event;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ObserverMetrics {
    private static final Logger logger = LoggerFactory.getLogger(ObserverMetrics.class);
    private static MeterRegistry meterRegistry;

    @Autowired
    public ObserverMetrics(MeterRegistry meterRegistry) {
        ObserverMetrics.meterRegistry = meterRegistry;
    }

    public static void recordSuccess(Observer<?> observer, Event event) {
        if (meterRegistry != null) {
            meterRegistry.counter("observer.success", "observer", observer.getClass().getSimpleName()).increment();
        }
        logger.debug("Observer {} successfully handled event {}", observer.getClass().getSimpleName(), event.getEventId());
    }

    public static void recordFailure(Observer<?> observer, Event event, Exception ex) {
        if (meterRegistry != null) {
            meterRegistry.counter("observer.failure", "observer", observer.getClass().getSimpleName()).increment();
        }
        logger.warn("Observer {} failed to handle event {}: {}", observer.getClass().getSimpleName(), event.getEventId(), ex.getMessage());
    }
} 