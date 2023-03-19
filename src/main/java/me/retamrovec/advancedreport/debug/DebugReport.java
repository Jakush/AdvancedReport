package me.retamrovec.advancedreport.debug;

import org.bukkit.Bukkit;

public class DebugReport {

    private final static String PREFIX = "[AdvancedReport] ";

    public static void foundMajor(String issue, boolean configIssue, boolean discordIssue, boolean otherIssue) {
        Bukkit.getLogger().severe(PREFIX + "-----------------------");
        Bukkit.getLogger().severe(PREFIX + " ");
        Bukkit.getLogger().severe(PREFIX + "DO NOT REPORT THIS TO PAPER OR SPIGOT!");
        Bukkit.getLogger().severe(PREFIX + " ");
        Bukkit.getLogger().severe(PREFIX + "There is major issue in plugin!");
        Bukkit.getLogger().severe(PREFIX + "Throwing all useful information under");
        Bukkit.getLogger().severe(PREFIX + "Config issue: " + configIssue);
        Bukkit.getLogger().severe(PREFIX + "DiscordBot issue: " + discordIssue);
        Bukkit.getLogger().severe(PREFIX + "Plugin's or something another issue: " + otherIssue);
        Bukkit.getLogger().severe(PREFIX + " ");
        Bukkit.getLogger().severe(PREFIX + " ");
        Bukkit.getLogger().severe(PREFIX + "Specific issue: " + issue);
        Bukkit.getLogger().severe(PREFIX + " ");
        Bukkit.getLogger().severe(PREFIX + " ");
        Bukkit.getLogger().severe(PREFIX + "-----------------------");
    }
}
