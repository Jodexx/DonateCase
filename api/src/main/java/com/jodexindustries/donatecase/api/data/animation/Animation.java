package com.jodexindustries.donatecase.api.data.animation;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataItem;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseDefinition;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseItem;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import lombok.Getter;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.UUID;

/**
 * Base class representing an animation that occurs when a player opens a case.
 * This class holds all the necessary context for running an animation and provides
 * lifecycle hooks such as {@link #start()}, {@link #preEnd()}, and {@link #end()}.
 */
@Getter
public abstract class Animation {

    /**
     * The player who triggered the animation.
     */
    private DCPlayer player;

    /**
     * The location where the animation is taking place.
     */
    private CaseLocation location;

    /**
     * Unique identifier of the currently active case animation.
     */
    private UUID uuid;

    /**
     * The definition of the case being opened (contains GUI, rewards, settings).
     */
    private CaseDefinition definition;

    /**
     * The winning item from the case.
     */
    private CaseItem item;

    /**
     * The configuration node with additional animation-specific settings.
     */
    private ConfigurationNode settings;

    /**
     * Default constructor.
     */
    public Animation() {
    }

    /**
     * Initializes the animation with the provided context.
     *
     * @param player         The player who is opening the case.
     * @param location       The location where the animation is being shown.
     * @param uuid           The UUID of this animation session.
     * @param caseDefinition The definition of the case being opened.
     * @param winItem        The item that the player has won.
     * @param settings       Additional configuration data for this animation.
     */
    public void init(DCPlayer player, CaseLocation location, UUID uuid, CaseDefinition caseDefinition,
                     CaseItem winItem, ConfigurationNode settings) {
        this.player = player;
        this.location = location;
        this.uuid = uuid;
        this.definition = caseDefinition;
        this.item = winItem;
        this.settings = settings;
    }

    /**
     * Starts the animation. Implement this in a subclass to define specific behavior.
     */
    public abstract void start();

    /**
     * Gets the player object associated with this animation.
     *
     * @return the {@link DCPlayer} who triggered the animation
     */
    public Object getPlayer() {
        return player;
    }

    /**
     * Signals the animation manager to trigger any "pre-end" behavior for this animation.
     * Useful for gracefully stopping animations or applying effects before finalization.
     */
    public final void preEnd() {
        DCAPI.getInstance().getAnimationManager().preEnd(getUuid());
    }

    /**
     * Signals the animation manager to finalize and clean up this animation.
     * This should be called when the animation has fully completed.
     */
    public final void end() {
        DCAPI.getInstance().getAnimationManager().end(getUuid());
    }

    /**
     * @return a temporary {@link CaseData} representation built from the case definition.
     * @deprecated This method creates a new {@link CaseData} from the definition and should
     *             be avoided for performance reasons. Use {@link #definition} directly instead.
     */
    @Deprecated
    public CaseData getCaseData() {
        return CaseData.fromDefinition(definition);
    }

    /**
     * @return a temporary {@link CaseDataItem} representation of the winning item.
     * @deprecated This method creates a new {@link CaseDataItem} from the winning {@link CaseItem}
     *             and should be avoided in performance-critical paths.
     */
    @Deprecated
    public CaseDataItem getWinItem() {
        return CaseDataItem.fromItem(item);
    }
}
