package com.usermanagement.patterns.observer.event;

import org.junit.jupiter.api.Test;

class ObserverPatternTest {
    @Test
    void testEventNotification() {
        EventManager eventManager = new EventManager();
        Observer email = new EmailObserver();
        Observer sms = new SMSObserver();
        Observer logger = new LoggerObserver();

        eventManager.registerObserver(email);
        eventManager.registerObserver(sms);
        eventManager.registerObserver(logger);

        eventManager.notifyObservers("User Registered");
        eventManager.removeObserver(sms);
        eventManager.notifyObservers("User Deleted");
    }
}

