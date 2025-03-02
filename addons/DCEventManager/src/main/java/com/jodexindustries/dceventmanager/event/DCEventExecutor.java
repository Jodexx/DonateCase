package com.jodexindustries.dceventmanager.event;

import com.jodexindustries.dceventmanager.data.EventData;
import com.jodexindustries.dceventmanager.data.Placeholder;
import com.jodexindustries.dceventmanager.utils.Reflection;
import com.jodexindustries.dceventmanager.utils.Tools;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.event.DCEvent;
import com.jodexindustries.donatecase.api.event.plugin.DonateCaseReloadEvent;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import net.kyori.event.EventSubscriber;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.jodexindustries.dceventmanager.utils.Tools.eventMap;
import static com.jodexindustries.dceventmanager.utils.Tools.placeholderMap;
import static com.jodexindustries.donatecase.api.tools.DCTools.rc;

public class DCEventExecutor implements EventSubscriber<DCEvent> {

    public final Class<? extends DCEvent> clazz;
    public final String name;
    public final Tools tools;

    public DCEventExecutor(Class<? extends DCEvent> clazz, Tools tools) {
        this.clazz = clazz;
        this.name = clazz.getSimpleName().toUpperCase();
        this.tools = tools;
    }

    @Override
    public void invoke(@NonNull DCEvent event) {
        if (event instanceof DonateCaseReloadEvent) {
            DonateCaseReloadEvent reloadEvent = (DonateCaseReloadEvent) event;
            if(reloadEvent.type() == DonateCaseReloadEvent.Type.CONFIG) tools.reloadConfig();
        }

        final List<EventData> list = eventMap.getOrDefault(name, new ArrayList<>());
        String caseType = Reflection.getVar(event, "caseType", String.class);
        Integer slot = Reflection.getVar(event, "slot", Integer.class);

        if (caseType == null) {
            CaseData caseData = Reflection.getVar(event, "caseData", CaseData.class);
            if (caseData != null) {
                caseType = caseData.caseType();
            }
        }

        for (EventData data : list) {
            if (data.getCaseType() != null && !data.getCaseType().equalsIgnoreCase(caseType)) {
                continue;
            }

            if (data.getSlot() != -1 && slot != null && slot != data.getSlot()) {
                continue;
            }

            executeActions(event, replaceList(data.getActions(), getPlaceholders(event)
            ));

        }
    }

    private String[] getPlaceholders(DCEvent event) {
        List<Placeholder> placeholders = placeholderMap.getOrDefault(name, new ArrayList<>());
        String[] values = new String[placeholders.size() * 2];

        int index = 0;
        for (Placeholder placeholder : placeholders) {
            values[index] = placeholder.getName();
            values[index + 1] = String.valueOf(Reflection.invokeMethodChain(event, placeholder.getMethod()));

            index += 2;
        }
        return values;
    }

    private void executeActions(DCEvent event, List<String> actions) {
        DCPlayer player = null;

        // TODO Make the field player in placeholders.yml for selecting correct method for player
        // TODO Make the scanner for DCPlayer field searching to placeholders autocompleting
        if(Reflection.hasVar(event, "getWhoClicked")) {
            player = Reflection.getVar(event, "getWhoClicked", DCPlayer.class);
        } else if(Reflection.hasVar(event, "player")) {
            player = Reflection.getVar(event, "player", DCPlayer.class);
        }

        // DonateCase actions
        if (player != null) {
            tools.getMain().api.getActionManager().execute(player, actions);
        }

        // DCEventManager actions
        List<String> oldActions = actions.stream().filter(action -> action.startsWith("[invoke]")).collect(Collectors.toList());

        for (String action : oldActions) {
            if (action.startsWith("[invoke]")) {
                action = action.replaceFirst("\\[invoke] ", "");
                Reflection.invokeMethodChain(event, action);
            }
        }
    }

    public List<String> replaceList(List<String> list, String... args) {
        List<String> newList = new ArrayList<>();
        for (String line : list) {
            for (int i = 0; i+1 < args.length; i+=2) {
                String repl = args[i+1];
                if(repl != null) {
                    line = line.replace(args[i], repl);
                }
            }
            newList.add(rc(line));
        }
        return newList;
    }

}
