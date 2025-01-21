package com.jodexindustries.donatecase.managers;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.action.ActionExecutor;
import com.jodexindustries.donatecase.api.data.action.CaseAction;
import com.jodexindustries.donatecase.api.manager.ActionManager;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import com.jodexindustries.donatecase.api.platform.Platform;
import com.jodexindustries.donatecase.api.tools.DCTools;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionManagerImpl implements ActionManager {

    private static final Map<String, CaseAction> registeredActions = new HashMap<>();
    private final DCAPI api;
    private final Platform platform;

    public ActionManagerImpl(DCAPI api) {
        this.api = api;
        this.platform = api.getPlatform();
    }

    @Override
    public boolean registerAction(@NotNull String name, @NotNull ActionExecutor actionExecutor, @NotNull String description, @NotNull Addon addon) {
        if (!isRegistered(name)) {
            CaseAction caseAction = new CaseAction(actionExecutor, addon, name, description);
            registeredActions.put(name, caseAction);
            return true;
        } else {
            platform.getLogger().warning("CaseAction with name " + name + " already registered!");
        }
        return false;
    }

    @Override
    public void unregisterAction(@NotNull String name) {
        if (isRegistered(name)) {
            registeredActions.remove(name);
        } else {
            platform.getLogger().warning("CaseAction with name " + name + " already unregistered!");
        }
    }

    @Override
    public void unregisterActions() {
        List<String> list = new ArrayList<>(registeredActions.keySet());
        list.forEach(this::unregisterAction);
    }

    @Override
    public boolean isRegistered(@NotNull String name) {
        return registeredActions.containsKey(name);
    }

    @Nullable
    @Override
    public CaseAction getRegisteredAction(@NotNull String action) {
        return registeredActions.get(action);
    }

    @Override
    public @NotNull Map<String, CaseAction> getRegisteredActions() {
        return registeredActions;
    }

    @Override
    public @Nullable String getByStart(@NotNull final String string) {
        return registeredActions.keySet().stream().filter(string::startsWith).findFirst().orElse(null);
    }

    @Override
    public void executeAction(@NotNull DCPlayer player, @NotNull String action, int cooldown) {
        String temp = getByStart(action);
        if(temp == null) return;

        String context = action.replace(temp, "").trim();

        CaseAction caseAction = getRegisteredAction(temp);
        if(caseAction == null) return;

        caseAction.execute(player, context, cooldown);
    }

    @Override
    public void executeActions(@NotNull DCPlayer player, @NotNull List<String> actions) {
        for (String action : actions) {

            action = DCTools.rc(api.getPlatform().getTools().getPAPI().setPlaceholders(player, action));
            int cooldown = DCTools.extractCooldown(action);
            action = action.replaceFirst("\\[cooldown:(.*?)]", "");

            executeAction(player, action, cooldown);
        }
    }
}