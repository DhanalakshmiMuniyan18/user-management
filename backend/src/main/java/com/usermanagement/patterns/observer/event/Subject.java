package com.usermanagement.patterns.observer.event;

public interface Subject {
    void registerObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(String eventData);
}

