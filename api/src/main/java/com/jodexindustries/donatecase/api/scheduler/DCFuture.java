package com.jodexindustries.donatecase.api.scheduler;

import com.google.common.base.Suppliers;
import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.platform.Platform;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class DCFuture <T> extends CompletableFuture<T> {

    private static final Supplier<Platform> PLATFORM = Suppliers.memoize(() -> DCAPI.getInstance().getPlatform());
    private static final Supplier<Scheduler> SCHEDULER = Suppliers.memoize(() -> PLATFORM.get().getScheduler());

    public static <U> DCFuture<U> completedFuture(U value) {
        DCFuture<U> future = new DCFuture<>();
        future.complete(value);
        return future;
    }

    public <U> @NotNull DCFuture<U> thenComposeAsync(@NotNull Function<? super T, ? extends CompletionStage<U>> fn) {
        DCFuture<U> future = new DCFuture<>();
        super.thenComposeAsync(fn)
                .whenComplete((res, ex) -> {
                    if (ex != null) future.completeExceptionally(ex);
                    else future.complete(res);
                });
        return future;
    }

    public static <U> DCFuture<U> supplyAsync(Supplier<U> supplier) {
        DCFuture<U> future = new DCFuture<>();
        CompletableFuture.supplyAsync(supplier)
                .whenComplete((result, error) -> {
                    if (error != null) {
                        future.completeExceptionally(error);
                    } else {
                        future.complete(result);
                    }
                });
        return future;
    }

    public static <U> DCFuture<U> supplyAsync(Supplier<U> supplier, Executor executor) {
        DCFuture<U> future = new DCFuture<>();
        CompletableFuture.supplyAsync(supplier, executor)
                .whenComplete((result, error) -> {
                    if (error != null) {
                        future.completeExceptionally(error);
                    } else {
                        future.complete(result);
                    }
                });
        return future;
    }

    public DCFuture<T> thenAcceptSync(Consumer<? super T> action) {
        super.thenAccept(result -> SCHEDULER.get().run(PLATFORM.get(), () -> action.accept(result)));
        return this;
    }

    public DCFuture<T> thenAcceptSync(Consumer<? super T> action, long delay) {
        super.thenAccept(result -> SCHEDULER.get().run(PLATFORM.get(), () -> action.accept(result), delay));
        return this;
    }

    public <U> DCFuture<U> thenApplySync(Function<? super T, ? extends U> fn) {
        DCFuture<U> next = new DCFuture<>();
        super.thenAccept(result ->
                SCHEDULER.get().run(PLATFORM.get(), () -> next.complete(fn.apply(result)))
        );
        return next;
    }

    public DCFuture<T> thenRunSync(Runnable action) {
        super.thenAccept(result -> SCHEDULER.get().run(PLATFORM.get(), action));
        return this;
    }

    public DCFuture<T> whenCompleteSync(BiConsumer<? super T, ? super Throwable> action) {
        super.whenComplete((res, ex) ->
                SCHEDULER.get().run(PLATFORM.get(), () -> action.accept(res, ex))
        );
        return this;
    }
}
