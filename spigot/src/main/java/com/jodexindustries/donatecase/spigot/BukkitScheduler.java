package com.jodexindustries.donatecase.spigot;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.scheduler.SchedulerTask;
import com.jodexindustries.donatecase.common.scheduler.BackendScheduler;
import com.jodexindustries.donatecase.common.scheduler.WrappedTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

public class BukkitScheduler extends BackendScheduler {

    private final Plugin plugin;
    private final org.bukkit.scheduler.BukkitScheduler scheduler;

    public BukkitScheduler(BukkitBackend backend) {
        this.plugin = backend.getPlugin();
        this.scheduler = Bukkit.getScheduler();
    }

    private WrappedTask add(Addon addon, BukkitTask task) {
        WrappedTask wrappedTask = new WrappedTask(addon, task.getTaskId(), task.isSync(), task);
        add(wrappedTask);
        return wrappedTask;
    }

    @Override
    public SchedulerTask run(Addon addon, Runnable task, long delay) {
        BukkitTask bukkitTask = scheduler.runTaskLater(plugin, task, delay);
        return add(addon, bukkitTask);
    }

    @Override
    public SchedulerTask run(Addon addon, Runnable task, long delay, long period) {
        BukkitTask bukkitTask = scheduler.runTaskTimer(plugin, task, delay, period);
        return add(addon, bukkitTask);
    }

    @Override
    public void run(Addon addon, Consumer<SchedulerTask> task, long delay) {
        scheduler.runTaskLater(plugin, (bukkitTask) -> {
            WrappedTask wrappedTask = add(addon, bukkitTask);
            task.accept(wrappedTask);
        }, delay);
    }

    @Override
    public void run(Addon addon, Consumer<SchedulerTask> task, long delay, long period) {
        scheduler.runTaskTimer(plugin, (bukkitTask) -> {
            WrappedTask wrappedTask = add(addon, bukkitTask);
            task.accept(wrappedTask);
        }, delay, period);
    }

    @Override
    public SchedulerTask async(Addon addon, Runnable task, long delay) {
        BukkitTask bukkitTask = scheduler.runTaskLaterAsynchronously(plugin, task, delay);
        return add(addon, bukkitTask);
    }

    @Override
    public SchedulerTask async(Addon addon, Runnable task, long delay, long period) {
        BukkitTask bukkitTask = scheduler.runTaskTimerAsynchronously(plugin, task, delay, period);
        return add(addon, bukkitTask);
    }

    @Override
    public void async(Addon addon, Consumer<SchedulerTask> task, long delay) {
        scheduler.runTaskLaterAsynchronously(plugin, (bukkitTask) -> {
            WrappedTask wrappedTask = add(addon, bukkitTask);
            task.accept(wrappedTask);
        }, delay);
    }

    @Override
    public void async(Addon addon, Consumer<SchedulerTask> task, long delay, long period) {
        scheduler.runTaskTimerAsynchronously(plugin, (bukkitTask) -> {
            WrappedTask wrappedTask = add(addon, bukkitTask);
            task.accept(wrappedTask);
        }, delay, period);
    }

    @Override
    public void cancel(int taskId) {
        remove(taskId);
        Bukkit.getScheduler().cancelTask(taskId);
    }

    @Override
    public void shutdown() {
        tasks.values().forEach(task -> {
            task.cancel();
            Bukkit.getScheduler().cancelTask(task.getTaskId());
        });
        tasks.clear();
    }
}
