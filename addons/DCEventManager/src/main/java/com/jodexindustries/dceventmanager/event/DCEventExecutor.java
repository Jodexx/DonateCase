package com.jodexindustries.dceventmanager.event;

import com.jodexindustries.dceventmanager.data.EventData;
import com.jodexindustries.dceventmanager.data.EventPlaceholder;
import com.jodexindustries.dceventmanager.utils.Reflection;
import com.jodexindustries.dceventmanager.utils.Tools;
import com.jodexindustries.donatecase.api.event.DCEvent;
import com.jodexindustries.donatecase.api.event.plugin.DonateCaseReloadEvent;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import net.kyori.event.EventSubscriber;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;
import java.util.logging.Level;

import static com.jodexindustries.donatecase.api.tools.DCTools.rc;

public class DCEventExecutor implements EventSubscriber<DCEvent> {

    public final Class<? extends DCEvent> clazz;
    public final String name;
    public final Tools tools;

    public DCEventExecutor(Class<? extends DCEvent> clazz, Tools tools) {
        this.clazz = clazz;
        this.name = clazz.getSimpleName();
        this.tools = tools;
    }

    @Override
    public void invoke(@NonNull DCEvent event) {
        if (event instanceof DonateCaseReloadEvent) {
            DonateCaseReloadEvent reloadEvent = (DonateCaseReloadEvent) event;
            if (reloadEvent.type() == DonateCaseReloadEvent.Type.CONFIG) tools.loadConfig();
        }

        EventData data = tools.getConfigManager().getEventConfig().getEvents().get(name);
        if (data == null) return;

        Map<String, Object> placeholders = getPlaceholders(event);

        for (EventData.Executor executor : data.getExecutors()) {
            try {
                if (checkConditions(executor.getConditions(), placeholders)) {
                    executeActions(event, replaceList(executor.getActions(), placeholders));
                }
            } catch (Exception e) {
                tools.main.getLogger().log(Level.WARNING, "Error with executing executor: " + executor.getName(), e);
            }
        }
    }

    private boolean checkConditions(List<EventData.Condition> conditions, Map<String, Object> placeholders) {
        for (EventData.Condition condition : conditions) {
            Object output = placeholders.get(condition.getPlaceholder());
            if(!condition.compare(output)) return false;
        }

        return true;
    }

    private Map<String, Object> getPlaceholders(DCEvent event) {
        Map<String, Object> map = new HashMap<>();

        Map<String, EventPlaceholder> placeholderMap = tools.getConfigManager().getPlaceholderConfig().getEventPlaceholders();

        EventPlaceholder eventPlaceholder = placeholderMap.get(name);
        if (eventPlaceholder == null) return map;

        List<EventPlaceholder.Placeholder> placeholders = eventPlaceholder.getPlaceholders();

        for (EventPlaceholder.Placeholder placeholder : placeholders) {
            if (map.containsKey(placeholder.getReplace())) continue;

            Object value = null;
            try {
                value = Reflection.invokeMethodChain(event, placeholder.getMethod());
            } catch (Exception ex) {
                tools.main.getLogger().log(Level.WARNING, "Failed to parse placeholder: " + placeholder.getName(), ex);
            }

            map.put(placeholder.getReplace(), value);
        }
        return map;
    }

    private void executeActions(DCEvent event, List<String> actions) {
        DCPlayer player;

        // TODO Make the scanner for all fields searching to placeholders autocompleting
        player = Reflection.getVar(event, "player", DCPlayer.class);
        if (player == null) player = Reflection.getVar(event, "getWhoClicked", DCPlayer.class);

        // DonateCase actions
        tools.api.getActionManager().execute(player, actions);

        // DCEventManager actions
        for (String action : actions) {
            if (action.startsWith("[invoke]")) {
                try {
                    Reflection.invokeMethodChain(event, action.replaceFirst("\\[invoke]", "").trim());
                } catch (Exception ex) {
                    tools.main.getLogger().log(Level.WARNING, "Failed to execute action: " + action, ex);
                }
            }
        }
    }

    public List<String> replaceList(List<String> list, Map<String, Object> map) {
        List<String> newList = new ArrayList<>();
        if (list == null) return newList;

        for (String line : list) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                line = line.replace(entry.getKey(), String.valueOf(entry.getValue()));
            }
            newList.add(rc(line));
        }
        return newList;
    }

}
