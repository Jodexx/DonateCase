---
id: register-subcommands
title: Реєстрація субкоманд
sidebar_position: 7
---
# Реєстрація субкоманд

## Перший спосіб
> TestCommand клас
```java
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TestCommand implements SubCommandExecutor, SubCommandTabCompleter {

    /**
     * Виконує задану субкоманду
     * @param sender Джерело команди
     * @param label Ярлик команди
     * @param args Передаваємі аргументи команди
     */
    @Override
    public void execute(CommandSender sender, @NotNull String label, String[] args) {
        // звичайний код, як для Bukkit команди
        sender.sendMessage("Bukkit command");
    }

    /**
     * Наповнення табуляції
     * @param sender Джерело команди
     * @param label Ярлик команди
     * @param args Передаваємі аргументи команди
     * @return наповнення
     */
    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        return new ArrayList<>();
    }

}
```

> Main клас
```java
    @Override
    public void onEnable() {
        // отримання CaseManager
        CaseManager api = new CaseManager(this);
        // реєстрація субкоманди
        SubCommandManager subCommandManager = api.getSubCommandManager();
    
        TestCommand executor = new TestCommand();
    
        SubCommand subCommand = subCommandManager.builder("test")
                .type(SubCommandType.PLAYER)
                .executor(executor)
                .tabCompleter(executor)
                .args(new String[]{"(test)", "(test2)"})
                .description("This is cool command!")
                .build();
    
        subCommandManager.registerSubCommand(subCommand);
}
```

## Другий спосіб
> SecondCommand клас
```java
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SecondCommand extends SubCommand {

    public SecondCommand(String name, Addon addon) {
        super(name, addon);

        setType(SubCommandType.PLAYER);
        setDescription("This is a second command");
        setArgs(new String[]{"(test)", "(test2)"});
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        sender.sendMessage("Second command");
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        return new ArrayList<>();
    }
}
```

> Main class
```java
    @Override
    public void onEnable() {
        // отримання CaseManager
        CaseManager api = new CaseManager(this);
        // реєстрація субкоманди
        SubCommandManager subCommandManager = api.getSubCommandManager();
        
        SecondCommand second = new SecondCommand("test2", api.getAddon());
        subCommandManager.registerSubCommand(second);
    }
```


## ~~Старий метод~~
Щоб створити клас підкоманди, ми будемо використовувати [SubCommand](https://repo.jodexindustries.xyz/javadoc/releases/com/jodexindustries/donatecase/DonateCaseAPI/latest/.cache/unpack/com/jodexindustries/donatecase/api/data/SubCommand.html) інтерфейс

> TestSubCommand клас
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

> Main class
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