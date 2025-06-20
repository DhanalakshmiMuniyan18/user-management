/**
 * Thread-safe, generic Subject interface for Observer Pattern.
 * @author Saravanamuthukumar S
 */
package com.usermanagement.observer.core;

import java.util.Set;

public interface Subject<E extends Event> {
    void registerObserver(Observer<E> observer);
    void removeObserver(Observer<E> observer);
    void notifyObservers(E event);
    Set<Observer<E>> getObservers();
} 