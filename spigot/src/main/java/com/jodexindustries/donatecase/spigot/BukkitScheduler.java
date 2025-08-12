package com.jodexindustries.donatecase.spigot;

import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.scheduler.SchedulerTask;
import com.jodexindustries.donatecase.common.scheduler.BackendScheduler;
import com.jodexindustries.donatecase.common.scheduler.WrappedTask;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

public class BukkitScheduler extends BackendScheduler {

    private final Plugin plugin;
    private final org.bukkit.scheduler.BukkitScheduler scheduler;

    public BukkitScheduler(BukkitBackend backend) {
        this.plugin = backend.getPlugin();
        this.scheduler = plugin.getServer().getScheduler();
    }

    private WrappedTask wrapper(Addon addon, BukkitTask task) {
        return new WrappedTask(addon, task.getTaskId(), task.isSync(), task);
    }

    @Override
    public SchedulerTask run(Addon addon, Runnable task) {
        BukkitTask bukkitTask = scheduler.runTask(plugin, task);
        return wrapper(addon, bukkitTask);
    }

    @Override
    public SchedulerTask run(Addon addon, Runnable task, long delay) {
        BukkitTask bukkitTask = scheduler.runTaskLater(plugin, task, delay);
        return wrapper(addon, bukkitTask);
    }

    @Override
    public SchedulerTask run(Addon addon, Runnable task, long delay, long period) {
        BukkitTask bukkitTask = scheduler.runTaskTimer(plugin, task, delay, period);
        return wrapper(addon, bukkitTask);
    }

    @Override
    public void run(Addon addon, Consumer<SchedulerTask> task) {
        scheduler.runTask(plugin, (bukkitTask) -> {
            WrappedTask wrappedTask = wrapper(addon, bukkitTask);
            task.accept(wrappedTask);
        });
    }

    @Override
    public void run(Addon addon, Consumer<SchedulerTask> task, long delay) {
        scheduler.runTaskLater(plugin, (bukkitTask) -> {
            WrappedTask wrappedTask = wrapper(addon, bukkitTask);
            task.accept(wrappedTask);
        }, delay);
    }

    @Override
    public void run(Addon addon, Consumer<SchedulerTask> task, long delay, long period) {
        scheduler.runTaskTimer(plugin, (bukkitTask) -> {
            WrappedTask wrappedTask = wrapper(addon, bukkitTask);
            task.accept(wrappedTask);
        }, delay, period);
    }

    @Override
    public void cancel(int taskId) {
        super.cancel(taskId);
        scheduler.cancelTask(taskId);
    }
}
