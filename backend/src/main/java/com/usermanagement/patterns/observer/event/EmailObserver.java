package com.usermanagement.patterns.observer.event;

public class EmailObserver implements Observer {
    @Override
    public void update(String eventData) {
        System.out.println("Email sent: " + eventData);
    }
}

