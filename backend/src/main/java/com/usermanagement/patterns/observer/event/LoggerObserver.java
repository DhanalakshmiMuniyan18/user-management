package com.usermanagement.patterns.observer.event;

public class LoggerObserver implements Observer {
    @Override
    public void update(String eventData) {
        System.out.println("Log entry: " + eventData);
    }
}

