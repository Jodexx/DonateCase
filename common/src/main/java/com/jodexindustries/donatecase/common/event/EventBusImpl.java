package com.jodexindustries.donatecase.common.event;

import java.util.function.Predicate;

import com.google.common.collect.SetMultimap;
import com.jodexindustries.donatecase.api.event.DCEvent;
import com.jodexindustries.donatecase.api.event.EventBus;
import net.kyori.event.EventSubscriber;
import net.kyori.event.PostResult;
import net.kyori.event.SimpleEventBus;
import net.kyori.event.method.MethodHandleEventExecutorFactory;
import net.kyori.event.method.MethodSubscriptionAdapter;
import net.kyori.event.method.SimpleMethodSubscriptionAdapter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

public class EventBusImpl implements EventBus {

    private final SimpleEventBus<DCEvent> bus;
    private final MethodSubscriptionAdapter<Object> methodAdapter;

    public EventBusImpl() {
        bus = new SimpleEventBus<>(DCEvent.class);
        methodAdapter = new SimpleMethodSubscriptionAdapter<>(bus, new MethodHandleEventExecutorFactory<>());
    }

    @Override
    public @NonNull Class<DCEvent> eventType() {
        return bus.eventType();
    }

    @Override
    public @NonNull PostResult post(@NonNull DCEvent event) {
        return bus.post(event);
    }

    @Override
    public void register(@NotNull Object listener) {
        methodAdapter.register(listener);
    }

    @Override
    public <T extends DCEvent> void register(@NonNull Class<T> clazz, @NonNull EventSubscriber<? super T> subscriber) {
        bus.register(clazz, subscriber);
    }

    @Override
    public void unregister(@NotNull Object listener) {
        methodAdapter.unregister(listener);
    }

    @Override
    public void unregister(@NonNull EventSubscriber<?> subscriber) {
        bus.unregister(subscriber);
    }

    @Override
    public void unregister(@NonNull Predicate<EventSubscriber<?>> predicate) {
        bus.unregister(predicate);
    }

    @Override
    public void unregisterAll() {
        bus.unregisterAll();
    }

    @Override
    public <T extends DCEvent> boolean hasSubscribers(@NonNull Class<T> clazz) {
        return bus.hasSubscribers(clazz);
    }

    @Override
    public @NonNull SetMultimap<Class<?>, EventSubscriber<?>> subscribers() {
        return bus.subscribers();
    }

}