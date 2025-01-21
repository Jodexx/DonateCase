package com.jodexindustries.donatecase.gui.items;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedata.*;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGui;
import com.jodexindustries.donatecase.api.data.casedata.gui.TypedItemHandler;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGuiWrapper;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.tools.DCToolsBukkit;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class HISTORYItemHandlerImpl implements TypedItemHandler {

    @NotNull
    @Override
    public CaseGui.Item handle(@NotNull CaseGuiWrapper caseGui, CaseGui.@NotNull Item item) {
        CaseData caseData = caseGui.getCaseData();

        boolean handled = handleHistoryItem(caseData, item, caseGui.getGlobalHistoryData());

        CaseDataMaterial material = item.getMaterial();

        if (!handled) {
            ConfigurationNode config =  DCAPI.getInstance().getConfig().getConfigCases().getCase(caseData.getCaseType());
            if(config != null) {
                ConfigurationNode section = config.node("case", "Gui", "Items", item.getItemName(), "HistoryNotFound");
                if (section != null) {
                    material.setId(section.node("Material").getString());
                    material.setDisplayName(section.node("DisplayName").getString());

                    try {
                        material.setLore(section.node("Lore").getList(String.class));
                    } catch (SerializationException ignored) {}

                    material.setEnchanted(section.node("Enchanted").getBoolean());
                    material.setRgb(DCToolsBukkit.parseRGB(section.node("Rgb").getString("")));
                    material.setModelData(section.node("ModelData").getInt(-1));
                } else {
                    material.setId("AIR");
                }
            }
        }

        return item;
    }

    private boolean handleHistoryItem(CaseData caseData, CaseGui.Item item, List<CaseData.CaseDataHistory> globalHistoryData) {
        CaseDataMaterial itemMaterial = item.getMaterial();

        String caseType = caseData.getCaseType();

        String[] typeArgs = item.getType().split("-");
        int index = Integer.parseInt(typeArgs[1]);
        caseType = (typeArgs.length >= 3) ? typeArgs[2] : caseType;
        boolean isGlobal = caseType.equalsIgnoreCase("GLOBAL");

        CaseData historyCaseData = isGlobal ? null : DCAPI.getInstance().getCaseManager().getCase(caseType);
        if (historyCaseData == null && !isGlobal) {
            DCAPI.getInstance().getPlatform().getLogger().warning("Case " + caseType + " HistoryData is null!");
            return false;
        }

        if (!isGlobal) historyCaseData = historyCaseData.clone();

        CaseData.CaseDataHistory data = getHistoryData(caseType, isGlobal, globalHistoryData, index);
        if (data == null) return false;

        if (isGlobal) historyCaseData =  DCAPI.getInstance().getCaseManager().getCase(data.getCaseType());
        if (historyCaseData == null) return false;

        CaseDataItem historyItem = historyCaseData.getItem(data.getItem());
        if (historyItem == null) return false;
        String material = item.getMaterial().getId();
        if (material == null) material = "HEAD:" + data.getPlayerName();

        if (material.equalsIgnoreCase("DEFAULT")) material = historyItem.getMaterial().getId();

        String[] template = getTemplate(historyCaseData, data, historyItem);

        String displayName = DCTools.rt(item.getMaterial().getDisplayName(), template);
        List<String> lore = DCTools.rt(item.getMaterial().getLore(), template);

        itemMaterial.setId(material);
        itemMaterial.setDisplayName(displayName);
        itemMaterial.setLore(lore);

        return true;
    }

    private String[] getTemplate(CaseData historyCaseData, CaseData.CaseDataHistory data, CaseDataItem historyItem) {

        DateFormat formatter = new SimpleDateFormat(DCAPI.getInstance().getConfig().getConfig().node("DonateCase.DateFormat").getString("dd.MM HH:mm:ss"));
        String dateFormatted = formatter.format(new Date(data.getTime()));
        String group = data.getGroup();
        String groupDisplayName = data.getItem() != null ? historyItem.getMaterial().getDisplayName() : "group_not_found";
        String action = data.getAction() != null ? data.getAction() : group;

        String randomActionDisplayName = getActionDisplayName(action, groupDisplayName, historyItem);

        return new String[]{
                "%action%:" + action,
                "%actiondisplayname%:" + randomActionDisplayName,
                "%casedisplayname%:" + historyCaseData.getCaseDisplayName(),
                "%casename%:" + data.getCaseType(),
                "%casetitle%:" + historyCaseData.getCaseGui().getTitle(),
                "%time%:" + dateFormatted,
                "%group%:" + group,
                "%player%:" + data.getPlayerName(),
                "%groupdisplayname%:" + groupDisplayName
        };
    }

    public static String getActionDisplayName(String action, String groupDisplayName, CaseDataItem historyItem) {
        String randomActionDisplayName = "random_action_not_found";
        if (action != null && !action.isEmpty()) {
            CaseDataItem.RandomAction randomAction = historyItem.getRandomActions().get(action);
            if (randomAction != null) {
                randomActionDisplayName = randomAction.getDisplayName();
            }
        } else {
            randomActionDisplayName = groupDisplayName;
        }

        return randomActionDisplayName;
    }

    private CaseData.CaseDataHistory getHistoryData(String caseType, boolean isGlobal, List<CaseData.CaseDataHistory> globalHistoryData, int index) {
        CaseData.CaseDataHistory data = null;
        if (isGlobal) {
            if (globalHistoryData.size() <= index) return null;
            data = globalHistoryData.get(index);
        } else {
            List<CaseData.CaseDataHistory> dbData = DCTools.sortHistoryDataByCase(globalHistoryData, caseType);
            if (!dbData.isEmpty() && dbData.size() > index) {
                data = dbData.get(index);
            }
        }
        return data;
    }

}