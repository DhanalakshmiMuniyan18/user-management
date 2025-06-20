/**
 * Observer interface with priority and error handling contract.
 * @author Saravanamuthukumar S
 */
package com.usermanagement.observer.core;

public interface Observer<E extends Event> extends Comparable<Observer<E>> {
    void onEvent(E event) throws Exception;
    ObserverPriority getPriority();
    boolean supports(E event);

    @Override
    default int compareTo(Observer<E> o) {
        return this.getPriority().compareTo(o.getPriority());
    }
} 