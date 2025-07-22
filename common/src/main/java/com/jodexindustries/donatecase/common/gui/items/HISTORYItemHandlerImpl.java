package com.jodexindustries.donatecase.common.gui.items;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedata.*;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGuiWrapper;
import com.jodexindustries.donatecase.api.data.casedata.gui.typeditem.TypedItemException;
import com.jodexindustries.donatecase.api.data.casedata.gui.typeditem.TypedItemHandler;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseDefinition;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseItem;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseMaterial;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseMenu;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.common.tools.LocalPlaceholder;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.*;

/**
 * Handler for history-based typed items in case GUIs.
 */
public class HISTORYItemHandlerImpl implements TypedItemHandler {

    @NotNull
    @Override
    public CaseMenu.Item handle(@NotNull CaseGuiWrapper caseGui, @NotNull CaseMenu.Item item)
            throws TypedItemException {
        CaseDefinition definition = caseGui.getDefinition();
        boolean handled = handleHistoryItem(definition, item, caseGui.getGlobalHistoryData());

        if (!handled) {
            applyFallbackMaterial(item);
        }

        return item;
    }

    private void applyFallbackMaterial(CaseMenu.Item item) throws TypedItemException {
        try {
            ConfigurationNode notFoundNode = item.node().node("HistoryNotFound");
            if (!notFoundNode.isNull()) {
                item.material(notFoundNode.get(CaseMaterial.class));
            } else {
                item.material().id("AIR");
            }
        } catch (SerializationException e) {
            throw new TypedItemException("Error serializing 'HistoryNotFound' material", e);
        }
    }

    private boolean handleHistoryItem(CaseDefinition definition, CaseMenu.Item item,
                                      List<CaseData.History> globalHistoryData) {
        CaseMaterial itemMaterial = item.material();
        String[] typeArgs = item.type().split("-");

        if (typeArgs.length < 2) return false;

        int index = parseIndex(typeArgs[1]);
        if (index < 0) return false;

        String caseType = (typeArgs.length >= 3) ? typeArgs[2] : definition.settings().type();
        boolean isGlobal = caseType.equalsIgnoreCase("GLOBAL");

        CaseDefinition historyCaseData = getHistoryDefinition(caseType, isGlobal);
        if (historyCaseData == null && !isGlobal) return false;

        CaseData.History history = getHistoryData(caseType, isGlobal, globalHistoryData, index);
        if (history == null) return false;

        if (isGlobal) {
            historyCaseData = DCAPI.getInstance().getCaseManager().getByType(history.caseType()).orElse(null);
        }

        if (historyCaseData == null) return false;

        CaseItem historyItem = historyCaseData.items().getItem(history.item());
        if (historyItem == null) return false;

        applyMaterial(itemMaterial, history, historyItem);
        applyPlaceholders(itemMaterial, history, historyItem, historyCaseData);

        return true;
    }

    private int parseIndex(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }

    private CaseDefinition getHistoryDefinition(String caseType, boolean isGlobal) {
        if (isGlobal) return null;

        Optional<CaseDefinition> optional = DCAPI.getInstance().getCaseManager().getByType(caseType);
        return optional.map(CaseDefinition::clone).orElse(null);
    }


    private void applyMaterial(CaseMaterial itemMaterial, CaseData.History data, CaseItem historyItem) {
        String id = itemMaterial.id();

        String material;
        if (id == null) {
            material = "HEAD:" + data.playerName();
        } else if (id.equalsIgnoreCase("DEFAULT")) {
            material = historyItem.material().id();
        } else {
            material = id;
        }

        itemMaterial.id(material);
    }

    private void applyPlaceholders(CaseMaterial itemMaterial, CaseData.History data,
                                   CaseItem historyItem, CaseDefinition caseDefinition) {
        Collection<LocalPlaceholder> placeholders = getPlaceholders(caseDefinition, data, historyItem);
        itemMaterial.displayName(DCTools.rt(itemMaterial.displayName(), placeholders));
        itemMaterial.lore(DCTools.rt(itemMaterial.lore(), placeholders));
    }

    private Collection<LocalPlaceholder> getPlaceholders(CaseDefinition caseDefinition, CaseData.History data,
                                                         CaseItem historyItem) {
        String group = data.group();
        String groupDisplayName = data.item() != null
                ? historyItem.material().displayName()
                : "group_not_found";
        String action = Optional.ofNullable(data.action()).orElse(group);

        String actionDisplayName = getActionDisplayName(action, groupDisplayName, historyItem.randomActions());

        List<LocalPlaceholder> placeholders = new ArrayList<>(LocalPlaceholder.of(data));
        placeholders.addAll(LocalPlaceholder.of(caseDefinition));
        placeholders.add(LocalPlaceholder.of("%actiondisplayname%", actionDisplayName));
        placeholders.add(LocalPlaceholder.of("%groupdisplayname%", groupDisplayName));
        return placeholders;
    }

    private String getActionDisplayName(String action, String fallback,
                                        Map<String, CaseItem.RandomAction> actions) {
        if (action == null || action.isEmpty()) return fallback;
        return Optional.ofNullable(actions.get(action))
                .map(CaseItem.RandomAction::displayName)
                .orElse("random_action_not_found");
    }

    private CaseData.History getHistoryData(String caseType, boolean isGlobal,
                                            List<CaseData.History> globalHistoryData, int index) {
        if (isGlobal) {
            return index < globalHistoryData.size() ? globalHistoryData.get(index) : null;
        }

        List<CaseData.History> filtered = DCTools.sortHistoryDataByCase(globalHistoryData, caseType);
        return (index < filtered.size()) ? filtered.get(index) : null;
    }
}
