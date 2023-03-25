package me.retamrovec.advancedreport.alerts;

import me.retamrovec.advancedreport.AdvancedReport;
import me.retamrovec.advancedreport.config.ConfigOptions;
import me.retamrovec.advancedreport.config.ConfigReplace;
import me.retamrovec.advancedreport.database.Database;
import me.retamrovec.advancedreport.debug.DebugReport;
import me.retamrovec.advancedreport.utils.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AlertTask {

    BukkitTask bukkitTask;
    AdvancedReport reportClass;
    ConfigOptions configOptions;
    Database database;
    @Contract(pure = true)
    public AlertTask(@NotNull AdvancedReport reportClass) {
        this.reportClass = reportClass;
        this.configOptions = reportClass.getConfigOptions();
        this.database = reportClass.getDatabase();
    }

    public void start() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(reportClass, () -> {
            try (PreparedStatement ps = this.database.prepareStatement("SELECT * FROM " + this.database.getTable() + " " +
                    "WHERE 1 >= ((" + System.currentTimeMillis() + " - timestamp) / 1000)")) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String reported = rs.getString("reported");
                    String reporter = rs.getString("reporter");
                    String reportReason = rs.getString("reason");
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        if (player.hasPermission("advancedreport.receive")) {
                            player.sendMessage(Formatter.chatColors(this.configOptions.getString("messages.alert", new ConfigReplace()
                                    .addPlaceholder(ConfigReplace.Placeholder.PLAYER_NAME, reported)
                                    .addPlaceholder(ConfigReplace.Placeholder.REPORTER_NAME, reporter)
                                    .addPlaceholder(ConfigReplace.Placeholder.REASON, reportReason))));
                        }
                    });
                }
            } catch (SQLException e) {
                DebugReport.foundDatabase("Database couldn't finish task!", Thread.currentThread());
            }
        }, 40, 40);
    }

    public void kill() {
        if (bukkitTask != null) bukkitTask.cancel();
    }
}
