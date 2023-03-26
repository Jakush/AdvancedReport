package me.retamrovec.advancedreport.debug;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class DebugReport {

    private final static String PREFIX = "[AdvancedReport] ";

    public static void foundIssue(@NotNull String issue, StackTraceElement[] traceElement) {
        Bukkit.getLogger().severe(PREFIX + "-----------------------");
        Bukkit.getLogger().severe(PREFIX + " ");
        Bukkit.getLogger().severe(PREFIX + "DO NOT REPORT THIS TO PAPER OR SPIGOT!");
        Bukkit.getLogger().severe(PREFIX + " ");
        Bukkit.getLogger().severe(PREFIX + "There is issue in plugin!");
        Bukkit.getLogger().severe(PREFIX + "Throwing all useful information under");
        Bukkit.getLogger().severe(PREFIX + " ");
        Arrays.stream(traceElement).toList().forEach(clazz -> Bukkit.getLogger().severe(PREFIX + clazz.getClassName() + ":" + clazz.getLineNumber()));
        Bukkit.getLogger().severe(PREFIX + " ");
        Bukkit.getLogger().severe(PREFIX + "Description of the issue: ");
        Bukkit.getLogger().severe(PREFIX + issue);
        Bukkit.getLogger().severe(PREFIX + " ");
        Bukkit.getLogger().severe(PREFIX + "-----------------------");
    }

    public static void foundDatabase(String issue, @NotNull Thread thread) {
        Bukkit.getLogger().severe(PREFIX + "-----------------------");
        Bukkit.getLogger().severe(PREFIX + " ");
        Bukkit.getLogger().severe(PREFIX + "DO NOT REPORT THIS TO PAPER OR SPIGOT!");
        Bukkit.getLogger().severe(PREFIX + " ");
        Bukkit.getLogger().severe(PREFIX + "There is major database issue in plugin!");
        Bukkit.getLogger().severe(PREFIX + "Throwing all useful information under");
        Bukkit.getLogger().severe(PREFIX + " ");
        Arrays.stream(thread.getStackTrace()).toList().forEach(clazz -> Bukkit.getLogger().severe(PREFIX + clazz.getClassName() + ":" + clazz.getLineNumber()));
        Bukkit.getLogger().severe(PREFIX + " ");
        Bukkit.getLogger().severe(PREFIX + "Specific issue: " + issue);
        Bukkit.getLogger().severe(PREFIX + " ");
        Bukkit.getLogger().severe(PREFIX + " ");
        Bukkit.getLogger().severe(PREFIX + "-----------------------");
    }

    public static void successTask(String taskID, String details) {
        String d = (details != null) ? (": " + details) : "";
        Bukkit.getLogger().info(PREFIX + "Successfully finished task (" + taskID + ")" + d);
    }

    public static void successTask(String taskID) {
        successTask(taskID, null);
    }
}
