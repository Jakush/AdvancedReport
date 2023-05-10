package me.retamrovec.advancedreport.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

public class CommandManager {

    public static void register(String command, TabCompleter tabCompleter) {
        PluginCommand pluginCommand = Bukkit.getPluginCommand(command);
        if (pluginCommand == null || !pluginCommand.isRegistered()) {
            Bukkit.getLogger().warning("Command " + command + " was not found!");
            return;
        }
        pluginCommand.setTabCompleter(tabCompleter);
    }

    public static void register(String command, CommandExecutor commandExecutor) {
        PluginCommand pluginCommand = Bukkit.getPluginCommand(command);
        if (pluginCommand == null || !pluginCommand.isRegistered()) {
            Bukkit.getLogger().warning("Command " + command + " was not found!");
            return;
        }
        pluginCommand.setExecutor(commandExecutor);
    }

    public static void registerBoth(String command, Object clazz) {
        CommandExecutor commandExecutor = null;
        TabCompleter tabCompleter = null;
        if (clazz instanceof CommandExecutor) commandExecutor = (CommandExecutor) clazz;
        if (clazz instanceof TabCompleter) tabCompleter = (TabCompleter) clazz;
        if (commandExecutor != null) register(command, commandExecutor);
        if (tabCompleter != null) register(command, tabCompleter);
    }

}
