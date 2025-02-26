package com.jodexindustries.donatecase.common.event;

import java.util.function.Predicate;
import java.util.logging.Level;

import com.google.common.collect.SetMultimap;
import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.event.DCEvent;
import com.jodexindustries.donatecase.api.event.EventBus;
import com.jodexindustries.donatecase.api.event.Subscriber;
import net.kyori.event.EventSubscriber;
import net.kyori.event.PostResult;
import net.kyori.event.SimpleEventBus;
import net.kyori.event.method.MethodHandleEventExecutorFactory;
import net.kyori.event.method.MethodSubscriptionAdapter;
import net.kyori.event.method.SimpleMethodSubscriptionAdapter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

public class EventBusImpl implements EventBus {

    private final SimpleEventBus<DCEvent> bus = new SimpleEventBus<>(DCEvent.class);
    private final MethodSubscriptionAdapter<Subscriber> methodAdapter = new SimpleMethodSubscriptionAdapter<>(this, new MethodHandleEventExecutorFactory<>());

    @Override
    public @NonNull Class<DCEvent> eventType() {
        return bus.eventType();
    }

    @Override
    public @NonNull PostResult post(@NonNull DCEvent event) {
        return bus.post(event);
    }

    @Override
    public void register(@NotNull Subscriber listener) {
        try {
            methodAdapter.register(listener);
        } catch (Exception e) {
            DCAPI.getInstance().getPlatform().getLogger().log(Level.WARNING, "Error with event listener " + listener.getClass() + " registration:", e);
        }
    }

    @Override
    public <T extends DCEvent> void register(@NonNull Class<T> clazz, @NonNull EventSubscriber<? super T> subscriber) {
        try {
            bus.register(clazz, subscriber);
        } catch (Exception e) {
            DCAPI.getInstance().getPlatform().getLogger().log(Level.WARNING, "Error with event subscriber " + subscriber.getClass() + " registration:", e);
        }
    }

    @Override
    public void unregister(@NotNull Subscriber listener) {
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