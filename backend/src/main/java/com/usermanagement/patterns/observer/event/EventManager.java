package com.usermanagement.patterns.observer.event;

import java.util.ArrayList;
import java.util.List;

public class EventManager implements Subject {
    private final List<Observer> observers = new ArrayList<>();

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String eventData) {
        for (Observer observer : observers) {
            observer.update(eventData);
        }
    }
}

