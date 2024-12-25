package com.jodexindustries.donatecase.gui.items;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.casedata.*;
import com.jodexindustries.donatecase.api.data.casedata.gui.GUI;
import com.jodexindustries.donatecase.api.data.casedata.gui.GUITypedItem;
import com.jodexindustries.donatecase.api.data.casedata.gui.TypedItemHandler;
import com.jodexindustries.donatecase.api.events.CaseGuiClickEvent;
import com.jodexindustries.donatecase.api.gui.CaseGui;
import com.jodexindustries.donatecase.api.manager.GUITypedItemManager;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.tools.DCToolsBukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.jodexindustries.donatecase.DonateCase.instance;

public class HISTORYItemHandlerImpl implements TypedItemHandler<CaseDataMaterialBukkit, CaseGui<Inventory, Location, Player, CaseDataBukkit, CaseDataMaterialBukkit>> {

    public static void register(GUITypedItemManager<CaseDataMaterialBukkit, CaseGui<Inventory, Location, Player, CaseDataBukkit, CaseDataMaterialBukkit>, CaseGuiClickEvent> manager) {
        TypedItemHandler<CaseDataMaterialBukkit, CaseGui<Inventory, Location, Player, CaseDataBukkit, CaseDataMaterialBukkit>> handler = new HISTORYItemHandlerImpl();

        GUITypedItem<CaseDataMaterialBukkit, CaseGui<Inventory, Location, Player, CaseDataBukkit, CaseDataMaterialBukkit>, CaseGuiClickEvent> item = manager.builder("HISTORY")
                .description("Type for displaying the history of case openings")
                .handler(handler)
                .build();

        manager.registerItem(item);
    }

    @NotNull
    @Override
    public GUI.Item<CaseDataMaterialBukkit> handle(@NotNull CaseGui<Inventory, Location, Player, CaseDataBukkit, CaseDataMaterialBukkit> caseGui, GUI.@NotNull Item<CaseDataMaterialBukkit> item) {
        CaseDataBukkit caseData = caseGui.getCaseData();

        boolean handled = handleHistoryItem(caseData, item, caseGui.getGlobalHistoryData());

        CaseDataMaterial<ItemStack> material = item.getMaterial();

        if (!handled) {
            YamlConfiguration config = instance.api.getConfig().getConfigCases().getCase(caseData.getCaseType()).getSecond();
            String path = "case.Gui.Items." + item.getItemName() + ".HistoryNotFound";
            ConfigurationSection section = config.getConfigurationSection(path);
            if (section != null) {
                material.setId(section.getString("Material"));
                material.setDisplayName(section.getString("DisplayName"));
                material.setLore(section.getStringList("Lore"));
                material.setEnchanted(section.getBoolean("Enchanted"));
                material.setRgb(DCToolsBukkit.parseRGB(section.getString("Rgb", "")));
                material.setModelData(section.getInt("ModelData", -1));
            } else {
                material.setId("AIR");
            }
        }

        return item;
    }

    private boolean handleHistoryItem(CaseDataBukkit caseData, GUI.Item<CaseDataMaterialBukkit> item, List<CaseDataHistory> globalHistoryData) {
        CaseDataMaterialBukkit itemMaterial = item.getMaterial();

        String caseType = caseData.getCaseType();

        String[] typeArgs = item.getType().split("-");
        int index = Integer.parseInt(typeArgs[1]);
        caseType = (typeArgs.length >= 3) ? typeArgs[2] : caseType;
        boolean isGlobal = caseType.equalsIgnoreCase("GLOBAL");

        CaseDataBukkit historyCaseData = isGlobal ? null : instance.api.getCaseManager().getCase(caseType);
        if (historyCaseData == null && !isGlobal) {
            Case.getInstance().getLogger().warning("Case " + caseType + " HistoryData is null!");
            return false;
        }

        if (!isGlobal) historyCaseData = historyCaseData.clone();

        CaseDataHistory data = getHistoryData(caseType, isGlobal, globalHistoryData, index);
        if (data == null) return false;

        if (isGlobal) historyCaseData = instance.api.getCaseManager().getCase(data.getCaseType());
        if (historyCaseData == null) return false;

        CaseDataItem<CaseDataMaterialBukkit> historyItem = historyCaseData.getItem(data.getItem());
        if (historyItem == null) return false;
        String material = item.getMaterial().getId();
        if (material == null) material = "HEAD:" + data.getPlayerName();

        if (material.equalsIgnoreCase("DEFAULT")) material = historyItem.getMaterial().getId();

        String[] template = getTemplate(historyCaseData, data, historyItem);

        String displayName = DCToolsBukkit.rt(item.getMaterial().getDisplayName(), template);
        List<String> lore = DCToolsBukkit.rt(item.getMaterial().getLore(), template);

        itemMaterial.setId(material);
        itemMaterial.setDisplayName(displayName);
        itemMaterial.setLore(lore);

        return true;
    }

    private String[] getTemplate(CaseDataBukkit historyCaseData, CaseDataHistory data, CaseDataItem<CaseDataMaterialBukkit> historyItem) {

        DateFormat formatter = new SimpleDateFormat(instance.api.getConfig().getConfig().getString("DonateCase.DateFormat", "dd.MM HH:mm:ss"));
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
                "%casetitle%:" + historyCaseData.getCaseTitle(),
                "%time%:" + dateFormatted,
                "%group%:" + group,
                "%player%:" + data.getPlayerName(),
                "%groupdisplayname%:" + groupDisplayName
        };
    }

    public static String getActionDisplayName(String action, String groupDisplayName, CaseDataItem<CaseDataMaterialBukkit> historyItem) {
        String randomActionDisplayName = "random_action_not_found";
        if (action != null && !action.isEmpty()) {
            CaseDataItem.RandomAction randomAction = historyItem.getRandomAction(action);
            if (randomAction != null) {
                randomActionDisplayName = randomAction.getDisplayName();
            }
        } else {
            randomActionDisplayName = groupDisplayName;
        }

        return randomActionDisplayName;
    }

    private CaseDataHistory getHistoryData(String caseType, boolean isGlobal, List<CaseDataHistory> globalHistoryData, int index) {
        CaseDataHistory data = null;
        if (isGlobal) {
            if (globalHistoryData.size() <= index) return null;
            data = globalHistoryData.get(index);
        } else {
            List<CaseDataHistory> dbData = DCTools.sortHistoryDataByCase(globalHistoryData, caseType);
            if (!dbData.isEmpty() && dbData.size() > index) {
                data = dbData.get(index);
            }
        }
        return data;
    }

}