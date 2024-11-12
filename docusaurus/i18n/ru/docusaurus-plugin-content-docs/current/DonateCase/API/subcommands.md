---
id: register-subcommands
title: Регистрация субкоманд
sidebar_position: 7
---
# Регистрация субкоманд

## Первый способ
TestCommand класс
```java
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TestCommand implements SubCommandExecutor, SubCommandTabCompleter {

    /**
     * Executes the given sub command
     * @param sender Source of the command
     * @param label Command label
     * @param args Passed command arguments
     */
    @Override
    public void execute(CommandSender sender, @NotNull String label, String[] args) {
        sender.sendMessage("Bukkit command");
    }

    /**
     * Get command tab completions
     * @param sender Command sender
     * @param label Command label
     * @param args Command args
     * @return tab completions
     */
    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        return new ArrayList<>();
    }

}
```

Main class
```java
    @Override
    public void onEnable() {
        // получение CaseManager
        CaseManager api = new CaseManager(this);
        // регистрация команды
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

## Второй способ
SecondCommand класс
```java
import com.jodexindustries.donatecase.api.addon.Addon;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandType;
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

Main class
```java
    @Override
    public void onEnable() {
        // получение CaseManager
        CaseManager api = new CaseManager(this);
        // регистрация субкоманды
        SubCommandManager subCommandManager = api.getSubCommandManager();
        
        SecondCommand second = new SecondCommand("test2", api.getAddon());
        subCommandManager.registerSubCommand(second);
    }
```

Main class
```java
    @Override
    public void onEnable() {
        // получение CaseManager
        CaseManager api = new CaseManager(this);
        // регистрация суб команды
        SubCommandManager subCommandManager = api.getSubCommandManager();
        subCommandManager.registerSubCommand("test", new TestSubCommand());
    }
```