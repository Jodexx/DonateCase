package com.jodexindustries.donatecase.managers;

import com.jodexindustries.donatecase.actions.BroadcastActionExecutorImpl;
import com.jodexindustries.donatecase.actions.MessageActionExecutorImpl;
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
import java.util.logging.Level;

public class ActionManagerImpl implements ActionManager {

    private static final Map<String, CaseAction> registeredActions = new HashMap<>();
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
        if(isRegistered(action.getName())) throw new ActionException("Action with name " + action.getName() + " already registered!");

        registeredActions.put(action.getName(), action);
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

    @Nullable
    @Override
    public CaseAction get(@NotNull String action) {
        return registeredActions.get(action);
    }

    @Override
    public @NotNull Map<String, CaseAction> getMap() {
        return registeredActions;
    }

    @Override
    public @Nullable String getByStart(@NotNull final String string) {
        return registeredActions.keySet().stream().filter(string::startsWith).findFirst().orElse(null);
    }

    @Override
    public void execute(@NotNull DCPlayer player, @NotNull String action, int cooldown) {
        String temp = getByStart(action);
        if(temp == null) return;

        String context = action.replace(temp, "").trim();

        CaseAction caseAction = get(temp);
        if(caseAction == null) return;

        // TODO cooldown implement with scheduler

        try {
            caseAction.execute(player, context);
        } catch (ActionException e) {
            platform.getLogger().log(Level.WARNING, "Error with executing action: " + context, e);
        }
    }

    @Override
    public void execute(@NotNull DCPlayer player, @NotNull List<String> actions) {
        for (String action : actions) {

            action = DCTools.rc(api.getPlatform().getTools().getPAPI().setPlaceholders(player, action));
            int cooldown = DCTools.extractCooldown(action);
            action = action.replaceFirst("\\[cooldown:(.*?)]", "");

            execute(player, action, cooldown);
        }
    }
}