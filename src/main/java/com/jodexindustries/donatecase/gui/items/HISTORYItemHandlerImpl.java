package com.jodexindustries.donatecase.gui.items;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.GUITypedItemManager;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.data.GUI;
import com.jodexindustries.donatecase.api.data.gui.GUITypedItem;
import com.jodexindustries.donatecase.api.data.gui.TypedItemHandler;
import com.jodexindustries.donatecase.gui.CaseGui;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HISTORYItemHandlerImpl implements TypedItemHandler {

    public static void register(GUITypedItemManager manager) {
        HISTORYItemHandlerImpl handler = new HISTORYItemHandlerImpl();

        GUITypedItem item = manager.builder("HISTORY")
                .description("Type for displaying the history of case openings")
                .handler(handler)
                .build();

        manager.registerItem(item);
    }

    @NotNull
    @Override
    public GUI.Item handle(@NotNull CaseGui caseGui, GUI.@NotNull Item item) {
        CaseData caseData = caseGui.getCaseData();

        boolean handled = handleHistoryItem(caseData, item, caseGui.getGlobalHistoryData());

        CaseData.Item.Material material = item.getMaterial();

        if (!handled) {
            YamlConfiguration config = Case.getConfig().getCasesConfig().getCase(caseData.getCaseType()).getSecond();
            String path = "case.Gui.Items." + item.getItemName() + ".HistoryNotFound";
            ConfigurationSection section = config.getConfigurationSection(path);
            if (section != null) {
                material.setId(section.getString("Material"));
                material.setDisplayName(section.getString("DisplayName"));
                material.setLore(section.getStringList("Lore"));
                material.setEnchanted(section.getBoolean("Enchanted"));
                material.setRgb(Tools.parseRGB(section.getString("Rgb")));
                material.setModelData(section.getInt("ModelData", -1));
            } else {
                material.setId("AIR");
            }
        }

        return item;
    }

    private boolean handleHistoryItem(CaseData caseData, GUI.Item item, List<CaseData.HistoryData> globalHistoryData) {
        CaseData.Item.Material itemMaterial = item.getMaterial();

        String caseType = caseData.getCaseType();

        String[] typeArgs = item.getType().split("-");
        int index = Integer.parseInt(typeArgs[1]);
        caseType = (typeArgs.length >= 3) ? typeArgs[2] : caseType;
        boolean isGlobal = caseType.equalsIgnoreCase("GLOBAL");

        CaseData historyCaseData = isGlobal ? null : Case.getCase(caseType);
        if (historyCaseData == null && !isGlobal) {
            Case.getInstance().getLogger().warning("Case " + caseType + " HistoryData is null!");
            return false;
        }

        if (!isGlobal) historyCaseData = historyCaseData.clone();

        CaseData.HistoryData data = getHistoryData(caseType, isGlobal, globalHistoryData, index, historyCaseData);
        if (data == null) return false;

        if (isGlobal) historyCaseData = Case.getCase(data.getCaseType());
        if (historyCaseData == null) return false;

        CaseData.Item historyItem = historyCaseData.getItem(data.getItem());
        if (historyItem == null) return false;
        String material = item.getMaterial().getId();
        if (material == null) material = "HEAD:" + data.getPlayerName();

        if (material.equalsIgnoreCase("DEFAULT")) material = historyItem.getMaterial().getId();

        String[] template = getTemplate(historyCaseData, data, historyItem);

        String displayName = Tools.rt(item.getMaterial().getDisplayName(), template);
        List<String> lore = Tools.rt(item.getMaterial().getLore(), template);

        itemMaterial.setId(material);
        itemMaterial.setDisplayName(displayName);
        itemMaterial.setLore(lore);

        return true;
    }

    private String[] getTemplate(CaseData historyCaseData, CaseData.HistoryData data, CaseData.Item historyItem) {

        DateFormat formatter = new SimpleDateFormat(Case.getConfig().getConfig().getString("DonateCase.DateFormat", "dd.MM HH:mm:ss"));
        String dateFormatted = formatter.format(new Date(data.getTime()));
        String group = data.getGroup();
        String groupDisplayName = data.getItem() != null ? historyItem.getMaterial().getDisplayName() : "group_not_found";
        String action = data.getAction() != null ? data.getAction() : group;

        String randomActionDisplayName = "random_action_not_found";
        if (data.getAction() != null && !data.getAction().isEmpty()) {
            CaseData.Item.RandomAction randomAction = historyItem.getRandomAction(data.getAction());
            if (randomAction != null) {
                randomActionDisplayName = randomAction.getDisplayName();
            }
        } else {
            randomActionDisplayName = groupDisplayName;
        }

        return new String[]{
                "%action%:" + action,
                "%actiondisplayname%:" + randomActionDisplayName,
                "%casedisplayname%:" + historyCaseData.getCaseDisplayName(),
                "%casename%:" + data.getCaseType(),
                "%casetitle%:" + historyCaseData.getCaseTitle(),
                "%time%:" + dateFormatted,
                "%group%:" + group,
                "%player%:" + data.getPlayerName(),
                "%groupdisplayname%:" + groupDisplayName
        };
    }

    private CaseData.HistoryData getHistoryData(String caseType, boolean isGlobal, List<CaseData.HistoryData> globalHistoryData, int index, CaseData historyCaseData) {
        CaseData.HistoryData data = null;
        if (isGlobal) {
            if (globalHistoryData.size() <= index) return null;
            data = globalHistoryData.get(index);
        } else {
            if (!Case.getInstance().sql) {
                data = historyCaseData.getHistoryData()[index];
            } else {
                List<CaseData.HistoryData> dbData = Case.sortHistoryDataByCase(globalHistoryData, caseType);
                if (!dbData.isEmpty() && dbData.size() > index) {
                    data = dbData.get(index);
                }
            }
        }
        return data;
    }

}