/**
 * Observer for sending email notifications on order events.
 * @author Saravanamuthukumar S
 */
package com.usermanagement.observer.impl;

import com.usermanagement.observer.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationObserver implements Observer<OrderEvent> {
    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationObserver.class);

    @Override
    public void onEvent(OrderEvent event) throws Exception {
        if (event == null) {
            logger.error("Received null event");
            throw new IllegalArgumentException("Event cannot be null");
        }
        logger.info("[Email] Sending order confirmation for order {} to user {}", event.getOrderId(), event.getUserId());
        // Simulate email sending...
    }

    @Override
    public ObserverPriority getPriority() {
        return ObserverPriority.HIGH;
    }

    @Override
    public boolean supports(OrderEvent event) {
        return event != null;
    }
} 