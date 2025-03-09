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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

        for (EventData.Executor executor : data.getExecutors()) {
            executeActions(event, replaceList(executor.getActions(), getPlaceholders(event)
            ));
        }
    }

    private String[] getPlaceholders(DCEvent event) {
        Map<String, EventPlaceholder> map = tools.getConfigManager().getPlaceholderConfig().getEventPlaceholders();

        EventPlaceholder eventPlaceholder = map.get(name);
        if (eventPlaceholder == null) return new String[0];

        List<EventPlaceholder.Placeholder> placeholders = eventPlaceholder.getPlaceholders();

        String[] values = new String[placeholders.size() * 2];

        int index = 0;
        for (EventPlaceholder.Placeholder placeholder : placeholders) {
            values[index] = placeholder.getReplace();

            String value = null;
            try {
                value = String.valueOf(Reflection.invokeMethodChain(event, placeholder.getMethod()));
            } catch (Exception ex) {
                tools.main.getLogger().log(Level.WARNING, "Failed to parse placeholder: " + placeholder.getName(), ex);
            }

            values[index + 1] = value;

            index += 2;
        }
        return values;
    }

    private void executeActions(DCEvent event, List<String> actions) {
        DCPlayer player;

        // TODO Make the field player in placeholders.yml for selecting correct method for player
        // TODO Make the scanner for DCPlayer field searching to placeholders autocompleting
        player = Reflection.getVar(event, "player", DCPlayer.class);
        if (player == null) player = Reflection.getVar(event, "getWhoClicked", DCPlayer.class);

        // DonateCase actions
        if (player != null) tools.api.getActionManager().execute(player, actions);

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

    public List<String> replaceList(List<String> list, String... args) {
        List<String> newList = new ArrayList<>();
        if (list == null) return newList;

        for (String line : list) {
            for (int i = 0; i + 1 < args.length; i += 2) {
                String repl = args[i + 1];
                if (repl != null) {
                    line = line.replace(args[i], repl);
                }
            }
            newList.add(rc(line));
        }
        return newList;
    }

}
