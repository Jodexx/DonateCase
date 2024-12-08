package com.jodexindustries.donatecase.impl.managers;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.action.ActionExecutor;
import com.jodexindustries.donatecase.api.data.action.CaseAction;
import com.jodexindustries.donatecase.api.events.CaseActionRegisteredEvent;
import com.jodexindustries.donatecase.api.events.CaseActionUnregisteredEvent;
import com.jodexindustries.donatecase.api.manager.ActionManager;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.tools.DCToolsBukkit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jodexindustries.donatecase.DonateCase.instance;

public class ActionManagerImpl implements ActionManager<Player> {

    private static final Map<String, CaseAction<Player>> registeredActions = new HashMap<>();
    private final Addon addon;

    /**
     * Default constructor
     *
     * @param addon An addon that will manage actions
     */
    public ActionManagerImpl(Addon addon) {
        this.addon = addon;
    }

    @Override
    public boolean registerAction(@NotNull String name, @NotNull ActionExecutor<Player> actionExecutor, @NotNull String description) {
        if (!isRegistered(name)) {
            CaseAction<Player> caseAction = new CaseAction<>(actionExecutor, addon, name, description);
            registeredActions.put(name, caseAction);
            CaseActionRegisteredEvent event = new CaseActionRegisteredEvent(caseAction);
            Bukkit.getPluginManager().callEvent(event);
            return true;
        } else {
            addon.getLogger().warning("CaseAction with name " + name + " already registered!");
        }
        return false;
    }

    @Override
    public void unregisterAction(@NotNull String name) {
        if (isRegistered(name)) {
            registeredActions.remove(name);
            CaseActionUnregisteredEvent event = new CaseActionUnregisteredEvent(name);
            Bukkit.getServer().getPluginManager().callEvent(event);
        } else {
            addon.getLogger().warning("CaseAction with name " + name + " already unregistered!");
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
    public CaseAction<Player> getRegisteredAction(@NotNull String action) {
        return registeredActions.get(action);
    }

    @Override
    public @NotNull Map<String, CaseAction<Player>> getRegisteredActions() {
        return registeredActions;
    }

    @Override
    public @Nullable String getByStart(@NotNull final String string) {
        return registeredActions.keySet().stream().filter(string::startsWith).findFirst().orElse(null);
    }

    @Override
    public void executeAction(@NotNull Player player, @NotNull String action, int cooldown) {
        String temp = instance.api.getActionManager().getByStart(action);
        if(temp == null) return;

        String context = action.replace(temp, "").trim();

        ActionExecutor<Player> actionExecutor = instance.api.getActionManager().getRegisteredAction(temp);
        if(actionExecutor == null) return;

        actionExecutor.execute(player.getPlayer(), context, cooldown);
    }

    @Override
    public void executeActions(@NotNull Player player, @NotNull List<String> actions) {
        for (String action : actions) {

            action = DCToolsBukkit.rc(Case.getInstance().papi.setPlaceholders(player, action));
            int cooldown = DCTools.extractCooldown(action);
            action = action.replaceFirst("\\[cooldown:(.*?)]", "");

            executeAction(player, action, cooldown);
        }
    }
}