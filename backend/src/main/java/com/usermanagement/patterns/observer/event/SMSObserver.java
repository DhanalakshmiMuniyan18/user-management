package com.usermanagement.patterns.observer.event;

public class SMSObserver implements Observer {
    @Override
    public void update(String eventData) {
        System.out.println("SMS sent: " + eventData);
    }
}

