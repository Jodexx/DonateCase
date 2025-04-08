package com.jodexindustries.donatecase.common.managers;

import com.jodexindustries.donatecase.common.actions.BroadcastActionExecutorImpl;
import com.jodexindustries.donatecase.common.actions.MessageActionExecutorImpl;
import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.action.ActionException;
import com.jodexindustries.donatecase.api.data.action.CaseAction;
import com.jodexindustries.donatecase.api.manager.ActionManager;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import com.jodexindustries.donatecase.api.platform.Platform;
import com.jodexindustries.donatecase.api.tools.DCTools;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class ActionManagerImpl implements ActionManager {

    private static final Map<String, CaseAction> registeredActions = new ConcurrentHashMap<>();

    private final DCAPI api;
    private final Platform platform;

    public ActionManagerImpl(DCAPI api) {
        this.api = api;
        this.platform = api.getPlatform();

        List<? extends CaseAction> defaultActions = Arrays.asList(
                CaseAction.builder()
                        .name("[message]")
                        .addon(platform)
                        .executor(new MessageActionExecutorImpl())
                        .description("Sends a message in the player's chat")
                        .build(),
                CaseAction.builder()
                        .name("[broadcast]")
                        .addon(platform)
                        .executor(new BroadcastActionExecutorImpl())
                        .description("Sends a broadcast to the players")
                        .build()
        );

        defaultActions.forEach(this::register);
    }

    @Override
    public void register(CaseAction action) throws ActionException {
        if(isRegistered(action.name())) throw new ActionException("Action with name " + action.name() + " already registered!");

        registeredActions.put(action.name(), action);
    }

    @Override
    public void unregister(@NotNull String name) throws ActionException {
        if (!isRegistered(name)) throw new ActionException("Action with name " + name + " already unregistered!");

        registeredActions.remove(name);
    }

    @Override
    public void unregister() {
        List<String> list = new ArrayList<>(registeredActions.keySet());
        list.forEach(this::unregister);
    }

    @Override
    public @NotNull Map<String, CaseAction> getMap() {
        return registeredActions;
    }

    @Override
    public void execute(@Nullable DCPlayer player, @NotNull String action, int cooldown) {
        Optional<String> temp = getByStart(action);
        if(!temp.isPresent()) return;

        String context = action.replace(temp.get(), "").trim();

        Optional<CaseAction> caseAction = get(temp.get());
        if(!caseAction.isPresent()) return;

        platform.getScheduler().run(platform, () -> {
            try {
                caseAction.get().execute(player, context);
            } catch (ActionException e) {
                platform.getLogger().log(Level.WARNING, "Error with executing action: " + context, e);
            }
        }, cooldown);
    }

    @Override
    public void execute(@Nullable DCPlayer player, @NotNull List<String> actions) {
        for (String action : actions) {

            if(player != null) action = DCTools.rc(api.getPlatform().getPAPI().setPlaceholders(player, action));
            int cooldown = DCTools.extractCooldown(action);
            action = action.replaceFirst("\\[cooldown:(.*?)]", "");

            execute(player, action, cooldown);
        }
    }
}