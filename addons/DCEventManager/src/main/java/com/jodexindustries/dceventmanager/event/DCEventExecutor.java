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
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

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

        Map<String, EventPlaceholder> placeholderMap = tools.getConfigManager().getPlaceholderConfig().getEventPlaceholders();

        EventPlaceholder eventPlaceholder = placeholderMap.getOrDefault(name, new EventPlaceholder());
        List<EventPlaceholder.Placeholder> placeholders = eventPlaceholder.getPlaceholders();

        Map<String, Object> parsedPlaceholders = getPlaceholders(event, placeholders);

        for (EventData.Executor executor : data.getExecutors()) {
            try {
                DCPlayer player = getPlayer(event, placeholders);

                if (checkConditions(player, executor.getConditions(), parsedPlaceholders)) {
                    executeActions(event, player, replaceList(executor.getActions(), parsedPlaceholders));
                }
            } catch (Exception e) {
                tools.main.getLogger().log(Level.WARNING, "Error with executing executor: " + executor.getName(), e);
            }
        }
    }

    private boolean checkConditions(@Nullable DCPlayer player, List<EventData.Condition> conditions, Map<String, Object> placeholders) {
        return conditions.stream().allMatch(condition -> {
            Object output = placeholders.getOrDefault(condition.getPlaceholder(),
                    (player != null) ? tools.api.getPlatform().getPAPI().setPlaceholders(player, condition.getPlaceholder()) : null);
            return condition.compare(output);
        });
    }

    private Map<String, Object> getPlaceholders(DCEvent event, List<EventPlaceholder.Placeholder> placeholders) {
        Map<String, Object> map = new HashMap<>();
        if (placeholders == null) return map;

        for (EventPlaceholder.Placeholder placeholder : placeholders) {
            map.computeIfAbsent(placeholder.getReplace(), key -> {
                try {
                    return Reflection.invokeMethodChain(event, placeholder.getMethod());
                } catch (Exception ex) {
                    tools.main.getLogger().log(Level.WARNING, "Failed to parse placeholder: " + placeholder.getName(), ex);
                    return null;
                }
            });
        }
        return map;
    }

    private void executeActions(DCEvent event, @Nullable DCPlayer player, List<String> actions) {
        // DonateCase actions
        if (player != null) tools.api.getActionManager().execute(player, actions);

        // DCEventManager actions
        actions.stream()
                .filter(action -> action.startsWith("[invoke]"))
                .forEach(action -> {
                    try {
                        Reflection.invokeMethodChain(event, action.substring(8).trim());
                    } catch (Exception e) {
                        tools.main.getLogger().log(Level.WARNING, "Error executing action: " + action, e);
                    }
                });
    }

    @Nullable
    private DCPlayer getPlayer(DCEvent event, List<EventPlaceholder.Placeholder> placeholders) {
        if (placeholders == null) return null;

        return placeholders.stream()
                .filter(placeholder -> "player".equals(placeholder.getName()))
                .findFirst()
                .map(placeholder -> {
                    try {
                        Object player = Reflection.invokeMethodChain(event, placeholder.getMethod().replace("#getName", ""));
                        return (player instanceof DCPlayer) ? (DCPlayer) player : null;
                    } catch (Exception ignored) {
                        return null;
                    }
                })
                .orElse(null);
    }

    private List<String> replaceList(List<String> list, Map<String, Object> map) {
        if (list == null) return Collections.emptyList();

        return list.stream().map(line -> {
            StringBuilder sb = new StringBuilder(line);
            map.forEach((key, value) -> {
                int index;
                while ((index = sb.indexOf(key)) != -1) {
                    sb.replace(index, index + key.length(), String.valueOf(value));
                }
            });
            return rc(sb.toString());
        }).collect(Collectors.toList());
    }

}
