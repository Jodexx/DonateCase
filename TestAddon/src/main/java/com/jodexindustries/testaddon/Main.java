package com.jodexindustries.testaddon;

import com.jodexindustries.donatecase.api.AnimationManager;
import com.jodexindustries.donatecase.api.addon.JavaAddon;

public class Main extends JavaAddon {
    @Override
    public void onEnable() {
        AnimationManager.registerAnimation("test", new TestAnimation());
    }
}