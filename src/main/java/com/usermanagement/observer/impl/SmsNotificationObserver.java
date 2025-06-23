/**
 * Observer for sending SMS notifications on high-value order events.
 * @author Saravanamuthukumar S
 */
package com.usermanagement.observer.impl;

import com.usermanagement.observer.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SmsNotificationObserver implements Observer<OrderEvent> {
    private static final Logger logger = LoggerFactory.getLogger(SmsNotificationObserver.class);

    @Override
    public void onEvent(OrderEvent event) throws Exception {
        if (event == null) {
            logger.error("Received null event");
            throw new IllegalArgumentException("Event cannot be null");
        }
        logger.info("[SMS] Sending SMS for order {} to user {}", event.getOrderId(), event.getUserId());
        // Simulate SMS sending...
    }

    @Override
    public ObserverPriority getPriority() {
        return ObserverPriority.MEDIUM;
    }

    @Override
    public boolean supports(OrderEvent event) {
        return event != null && event.getAmount() > 100; // Only for high-value orders
    }
} 