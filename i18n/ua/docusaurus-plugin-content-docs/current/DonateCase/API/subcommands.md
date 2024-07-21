---
id: register-subcommands
title: Реєстрація субкоманд
sidebar_position: 6
---
# Реєстрація субкоманд

Щоб створити клас підкоманди, ми будемо використовувати [SubCommand](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/latest/.cache/unpack/com/jodexindustries/donatecase/api/data/SubCommand.html) інтерфейс

TestSubCommand клас
```java
import com.jodexindustries.donatecase.api.SubCommand;
import com.jodexindustries.donatecase.api.SubCommandType;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class TestSubCommand implements SubCommand {
    /**
     * Виконує задану підкоманду
     * @param sender Джерело команди
     * @param args Передані аргументи команди
     */
    @Override
    public void execute(CommandSender sender, String[] args) {
        // Тут код схожий на звичайну команду Bukkit
    }

    /**
     *  Заповнення вкладки
     * @param sender Джерело команди
     * @param args Передані аргументи команди
     * @return Заповнення вкладки
     */
    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        // Код вкладки
        return new ArrayList<>();
    }

    /**
     * Тип команди (ADMIN, MODER, PLAYER)
     * @return SubCommandType
     */

    @Override
    public SubCommandType getType() {
        return SubCommandType.PLAYER;
    }
    
    /**
     * Отримання аргументів команди * для відображення в описі команди в довідці /dc
     * @return Аргументи команди
     */
    @Override
    public String[] getArgs() {
        return null;
    }
    
    /**
     * Отримати опис команди
     * Опис, який має слугувати описом команди у довідці /dc
     * @return Аргументи команди
     */
    @Override
    public String getDescription() {
        return null;
    }
}

```

Main class
```java
    @Override
    public void onEnable() {
        // отримання CaseManager
        CaseManager api = new CaseManager(this);
        // реєстрація субкоманди
        SubCommandManager subCommandManager = api.getSubCommandManager();
        subCommandManager.registerSubCommand("test", new TestCommand());
    }
```