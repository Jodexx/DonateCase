---
id: register-animations
title: Register Animations
sidebar_position: 6
---
# Register custom animations


To create an animation class, we will use the [JavaAnimation](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/spigot/2.2.6.3/raw/com/jodexindustries/donatecase/api/data/JavaAnimation.html) abstract class

##### Pay attention to the methods: [Case#animationPreEnd](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/spigot/2.2.6.3/raw/com/jodexindustries/donatecase/api/Case.html#animationPreEnd(com.jodexindustries.donatecase.api.data.CaseData,org.bukkit.OfflinePlayer,boolean,com.jodexindustries.donatecase.api.data.CaseData.Item,org.bukkit.Location)) and [Case#animationEnd](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/spigot/2.2.6.3/raw/com/jodexindustries/donatecase/api/Case.html#animationEnd(com.jodexindustries.donatecase.api.data.CaseData,org.bukkit.entity.Player,java.util.UUID,com.jodexindustries.donatecase.api.data.CaseData.Item))
- [Case#animationPreEnd](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/spigot/2.2.6.3/raw/com/jodexindustries/donatecase/api/Case.html#animationPreEnd(com.jodexindustries.donatecase.api.data.CaseData,org.bukkit.entity.Player,boolean,com.jodexindustries.donatecase.api.data.CaseData.Item)) is called to grant a group, send a message, and more.
- [Case#animationEnd](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/spigot/2.2.6.3/raw/com/jodexindustries/donatecase/api/Case.html#animationEnd(com.jodexindustries.donatecase.api.data.CaseData,org.bukkit.entity.Player,java.util.UUID,com.jodexindustries.donatecase.api.data.CaseData.Item)) is called to completely end the animation.

> TestAnimation class
```java
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.JavaAnimation;
import org.bukkit.Bukkit;

public class TestAnimation extends JavaAnimation {
    @Override
    public void start() {
        Case.animationPreEnd(getCaseData(), getPlayer(), getUuid(), getWinItem());
        Bukkit.getScheduler().runTaskLater(Case.getInstance(), () -> Case.animationEnd(getCaseData(), getPlayer(), getUuid(), getWinItem()),20L);
    }
}


```

> Main class
```java
    @Override
    public void onEnable() {
        // getting CaseManager for addon
        CaseManager api = new CaseManager(this);
        // register animation
        AnimationManager animationManager = api.getAnimationManager();
        animationManager.registerAnimation("test", TestAnimation.class);
    }
```