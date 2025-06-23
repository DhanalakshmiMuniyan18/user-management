/**
 * Observer for recording analytics on order events.
 * @author Saravanamuthukumar S
 */
package com.usermanagement.observer.impl;

import com.usermanagement.observer.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsObserver implements Observer<OrderEvent> {
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsObserver.class);

    @Override
    public void onEvent(OrderEvent event) throws Exception {
        if (event == null) {
            logger.error("Received null event");
            throw new IllegalArgumentException("Event cannot be null");
        }
        logger.info("[Analytics] Recording order {} for analytics", event.getOrderId());
        // Simulate analytics event...
    }

    @Override
    public ObserverPriority getPriority() {
        return ObserverPriority.LOW;
    }

    @Override
    public boolean supports(OrderEvent event) {
        return event != null;
    }
} 