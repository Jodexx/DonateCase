package com.jodexindustries.donatecase.api.event;

import org.jetbrains.annotations.NotNull;

public interface EventBus extends net.kyori.event.EventBus<DCEvent> {

    /**
     * Scans the given listener for any method that is annotated with
     * {@link net.kyori.event.method.annotation.Subscribe} and registers it as a listener to this event bus.
     *
     * <p>All methods in this class that are annotated with {@link net.kyori.event.method.annotation.Subscribe}
     * must meet the following criteria:
     * <ul>
     *     <li>It must be <strong>public</strong>.</li>
     *     <li>It must <strong>not</strong> be abstract.</li>
     *     <li>It must have exactly <strong>one</strong> parameter.</li>
     *     <li>Its single parameter must be <strong>an event</strong>.</li>
     * </ul>
     * If a method does not meet the above criteria, an exception <strong>will</strong>
     * be thrown.
     *
     * @param listener the listener to register
     * @throws RuntimeException if a method does not meet the criteria
     */
    void register(@NotNull Subscriber listener);

    /**
     * Scans the given listener for any method that is annotated with
     * {@link net.kyori.event.method.annotation.Subscribe} and unregisters it from this event bus.
     *
     * @param listener the listener to unregister
     */
    void unregister(@NotNull Subscriber listener);
}
