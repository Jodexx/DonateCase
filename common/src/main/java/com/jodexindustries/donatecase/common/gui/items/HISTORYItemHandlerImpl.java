package com.jodexindustries.donatecase.common.gui.items;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedata.*;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGui;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGuiWrapper;
import com.jodexindustries.donatecase.api.data.casedata.gui.typeditem.TypedItemException;
import com.jodexindustries.donatecase.api.data.casedata.gui.typeditem.TypedItemHandler;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.common.tools.LocalPlaceholder;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public class HISTORYItemHandlerImpl implements TypedItemHandler {

    @NotNull
    @Override
    public CaseGui.Item handle(@NotNull CaseGuiWrapper caseGui, CaseGui.@NotNull Item item) throws TypedItemException {
        CaseData caseData = caseGui.getCaseData();

        boolean handled = handleHistoryItem(caseData, item, caseGui.getGlobalHistoryData());

        if (!handled) {
            try {
                ConfigurationNode notFoundNode = item.node().node("HistoryNotFound");
                if (!notFoundNode.isNull()) {
                    item.material(notFoundNode.get(CaseDataMaterial.class));
                } else {
                    item.material().id("AIR");
                }
            } catch (SerializationException e) {
                throw new TypedItemException("Error with serialization HistoryNotFound material", e);
            }
        }

        return item;
    }

    private boolean handleHistoryItem(CaseData caseData, CaseGui.Item item, List<CaseData.History> globalHistoryData) {
        CaseDataMaterial itemMaterial = item.material();

        String caseType = caseData.caseType();

        String[] typeArgs = item.type().split("-");
        int index = Integer.parseInt(typeArgs[1]);
        caseType = (typeArgs.length >= 3) ? typeArgs[2] : caseType;
        boolean isGlobal = caseType.equalsIgnoreCase("GLOBAL");

        CaseData historyCaseData = isGlobal ? null : DCAPI.getInstance().getCaseManager().get(caseType);
        if (historyCaseData == null && !isGlobal) {
            return false;
        }

        if (!isGlobal) historyCaseData = historyCaseData.clone();

        CaseData.History data = getHistoryData(caseType, isGlobal, globalHistoryData, index);
        if (data == null) return false;

        if (isGlobal) historyCaseData =  DCAPI.getInstance().getCaseManager().get(data.caseType());
        if (historyCaseData == null) return false;

        CaseDataItem historyItem = historyCaseData.getItem(data.item());
        if (historyItem == null) return false;

        String material = itemMaterial.id();
        if (material == null) material = "HEAD:" + data.playerName();

        if (material.equalsIgnoreCase("DEFAULT")) material = historyItem.material().id();

        Collection<LocalPlaceholder> placeholders = getPlaceholders(historyCaseData, data, historyItem);

        String displayName = DCTools.rt(itemMaterial.displayName(), placeholders);
        List<String> lore = DCTools.rt(itemMaterial.lore(), placeholders);

        itemMaterial.id(material);
        itemMaterial.displayName(displayName);
        itemMaterial.lore(lore);

        return true;
    }

    private Collection<LocalPlaceholder> getPlaceholders(CaseData historyCaseData, CaseData.History data, CaseDataItem historyItem) {
        String group = data.group();
        String groupDisplayName = data.item() != null ? historyItem.material().displayName() : "group_not_found";
        String action = data.action() != null ? data.action() : group;

        String randomActionDisplayName = getActionDisplayName(action, groupDisplayName, historyItem.randomActions());

        Collection<LocalPlaceholder> placeholders = LocalPlaceholder.of(data);
        placeholders.addAll(LocalPlaceholder.of(historyCaseData));
        placeholders.add(LocalPlaceholder.of("%actiondisplayname%", randomActionDisplayName));
        placeholders.add(LocalPlaceholder.of("%groupdisplayname%", groupDisplayName));
        return placeholders;
    }

    private static String getActionDisplayName(String action, String groupDisplayName, Map<String, CaseDataItem.RandomAction> randomActions) {
        if (action == null || action.isEmpty()) {
            return groupDisplayName;
        }

        String displayName = randomActions
                .getOrDefault(action, new CaseDataItem.RandomAction())
                .displayName();

        return displayName != null ? displayName : "random_action_not_found";
    }


    private CaseData.History getHistoryData(String caseType, boolean isGlobal, List<CaseData.History> globalHistoryData, int index) {
        CaseData.History data = null;
        if (isGlobal) {
            if (globalHistoryData.size() <= index) return null;
            data = globalHistoryData.get(index);
        } else {
            List<CaseData.History> dbData = DCTools.sortHistoryDataByCase(globalHistoryData, caseType);
            if (!dbData.isEmpty() && dbData.size() > index) {
                data = dbData.get(index);
            }
        }
        return data;
    }

}