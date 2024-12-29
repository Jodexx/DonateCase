package com.jodexindustries.donatecase.impl;

import com.jodexindustries.donatecase.DonateCase;
import com.jodexindustries.donatecase.api.DCAPIBukkit;
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.config.ConfigBukkit;
import com.jodexindustries.donatecase.api.data.animation.JavaAnimationBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataBukkit;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterialBukkit;
import com.jodexindustries.donatecase.api.database.CaseDatabase;
import com.jodexindustries.donatecase.api.events.CaseGuiClickEvent;
import com.jodexindustries.donatecase.api.gui.CaseGui;
import com.jodexindustries.donatecase.api.manager.*;
import com.jodexindustries.donatecase.impl.managers.*;
import com.jodexindustries.donatecase.tools.DCToolsBukkit;
import com.jodexindustries.donatecase.tools.ToolsImpl;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class DCAPIBukkitImpl extends DCAPIBukkit {

    private final ActionManager<Player> actionManager = new ActionManagerImpl(addon);
    private final AddonManager addonManager = new AddonManagerImpl(this);
    private final AnimationManager<JavaAnimationBukkit, CaseDataMaterialBukkit, Player, Location, Block, CaseDataBukkit> animationmanager = new AnimationManagerImpl(this);
    private final GUIManager<Inventory, Location, Player, CaseDataBukkit, CaseDataMaterialBukkit> guiManager = new GUIManagerImpl(addon);
    private final GUITypedItemManager<CaseDataMaterialBukkit, CaseGui<Inventory, Location, Player, CaseDataBukkit, CaseDataMaterialBukkit>, CaseGuiClickEvent> guiTypedItemManager = new GUITypedItemManagerImpl(addon);
    private final MaterialManager<ItemStack> materialManager = new MaterialManagerImpl(addon);
    private final SubCommandManager<CommandSender> subCommandManager = new SubCommandManagerImpl(addon);

    private static final CaseKeyManager caseKeyManager = new CaseKeyManagerImpl();
    private static final CaseOpenManager caseOpenManager = new CaseOpenManagerImpl();
    private static final CaseManager<CaseDataBukkit> caseManager = new CaseManagerImpl();
    private static final DCToolsBukkit tools = new ToolsImpl(DonateCase.instance);

    public DCAPIBukkitImpl(Addon addon) {
        super(addon);
    }

    public DCAPIBukkitImpl(Plugin plugin) {
        super(plugin);
    }

    @Override
    public ActionManager<Player> getActionManager() {
        return actionManager;
    }

    @Override
    public AddonManager getAddonManager() {
        return addonManager;
    }

    @Override
    public AnimationManager<JavaAnimationBukkit, CaseDataMaterialBukkit, Player, Location, Block, CaseDataBukkit> getAnimationManager() {
        return animationmanager;
    }

    @Override
    public CaseKeyManager getCaseKeyManager() {
        return caseKeyManager;
    }

    @Override
    public CaseManager<CaseDataBukkit> getCaseManager() {
        return caseManager;
    }

    @Override
    public CaseOpenManager getCaseOpenManager() {
        return caseOpenManager;
    }

    @Override
    public GUIManager<Inventory, Location, Player, CaseDataBukkit, CaseDataMaterialBukkit> getGUIManager() {
        return guiManager;
    }

    @Override
    public GUITypedItemManager<CaseDataMaterialBukkit, CaseGui<Inventory, Location, Player, CaseDataBukkit, CaseDataMaterialBukkit>, CaseGuiClickEvent> getGuiTypedItemManager() {
        return guiTypedItemManager;
    }

    @Override
    public MaterialManager<ItemStack> getMaterialManager() {
        return materialManager;
    }

    @Override
    public SubCommandManager<CommandSender> getSubCommandManager() {
        return subCommandManager;
    }

    @Override
    public CaseDatabase getDatabase() {
        return DonateCase.instance.database;
    }

    @Override
    public ConfigBukkit getConfig() {
        return DonateCase.instance.config;
    }

    @Override
    public DCToolsBukkit getTools() {
        return tools;
    }

    @Override
    public @NotNull Plugin getDonateCase() {
        return DonateCase.instance;
    }

}
