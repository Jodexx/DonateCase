---
id: register-animations
title: Реєстрація анімацій
sidebar_position: 5
---
# Реєстрація анімацій


Для створення класу animation ми будемо використовувати [Animation](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/latest/.cache/unpack/com/jodexindustries/donatecase/api/data/Animation.html) інтерфейс

TestAnimation клас

##### Зверніть увагу на методи: [Case#animationPreEnd](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/2.2.2.4/raw/com/jodexindustries/donatecase/api/Case.html#animationPreEnd(com.jodexindustries.donatecase.api.data.CaseData,org.bukkit.entity.Player,boolean,com.jodexindustries.donatecase.api.data.CaseData.Item)) і [Case#animationEnd](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/2.2.2.4/raw/com/jodexindustries/donatecase/api/Case.html#animationEnd(com.jodexindustries.donatecase.api.data.CaseData,org.bukkit.entity.Player,java.util.UUID,com.jodexindustries.donatecase.api.data.CaseData.Item))
- [Case#animationPreEnd](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/2.2.2.4/raw/com/jodexindustries/donatecase/api/Case.html#animationPreEnd(com.jodexindustries.donatecase.api.data.CaseData,org.bukkit.entity.Player,boolean,com.jodexindustries.donatecase.api.data.CaseData.Item)) покликаний надати групу, відправити повідомлення і т.д.
- [Case#animationEnd](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/2.2.2.4/raw/com/jodexindustries/donatecase/api/Case.html#animationEnd(com.jodexindustries.donatecase.api.data.CaseData,org.bukkit.entity.Player,java.util.UUID,com.jodexindustries.donatecase.api.data.CaseData.Item)) викликається для завершення анімації.
```java
import com.jodexindustries.donatecase.api.Animation;
import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.tools.Tools;
import com.jodexindustries.donatecase.tools.support.PAPISupport;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TestAnimation implements Animation {

    @Override
    public void start(Player player, Location location, UUID uuid, CaseData caseData, CaseData.Item item) {
        Case.animationPreEnd(caseData, player, true, item);
        Bukkit.getScheduler().runTaskLater(Case.getInstance(), () -> Case.animationEnd(caseData, player, uuid, item),20L);
    }
}

```

Main class
```java
    @Override
    public void onEnable() {
        // отримання CaseManager
        CaseManager api = new CaseManager(this);
        // реєстрація анімації
        AnimationManager animationManager = api.getAnimationManager();
        animationManager.registerAnimation("test", new TestAnimation());
    }
```