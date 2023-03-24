package me.retamrovec.advancedreport.commands;

import me.retamrovec.advancedreport.AdvancedReport;
import me.retamrovec.advancedreport.config.ConfigOptions;
import me.retamrovec.advancedreport.config.ConfigReplace;
import me.retamrovec.advancedreport.database.Database;
import me.retamrovec.advancedreport.debug.DebugReport;
import me.retamrovec.advancedreport.utils.Formatter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ReportAdminCommand implements CommandExecutor {
    AdvancedReport reportClass;
    ConfigOptions configOptions;
    @Contract(pure = true)
    public ReportAdminCommand(@NotNull AdvancedReport reportClass) {
        this.reportClass = reportClass;
        this.configOptions = this.reportClass.getConfigOptions();
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("advancedreport.admin")) {
            sender.sendMessage(Formatter.chatColors(this.configOptions.getString("messages.no-permissions")));
            return false;
        }
        Database database = this.reportClass.getDatabase();
        // following command is:
        // /adminreport id NUMBER
        // NUMBER will be replaced with actual number
        if (args.length == 2 && args[0].equalsIgnoreCase("id")) {
            int id;
            try { id = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) { sender.sendMessage(Formatter.chatColors(this.configOptions.getString("messages.numberexception"))); return false; }
            // database manipulation
            Bukkit.getScheduler().runTaskAsynchronously(reportClass, () -> {
                boolean used = false;
                try (PreparedStatement ps = database.prepareStatement("SELECT * FROM " + database.getTable() + " WHERE id = (?);")) {
                    ps.setInt(1, id);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        if (!used) used = true;
                        String reported = rs.getString("reported");
                        String reporter = rs.getString("reporter");
                        String reason = rs.getString("reason");
                        sender.sendMessage(Formatter.chatColors(this.configOptions.getString("messages.player-result", new ConfigReplace()
                                .addPlaceholder(ConfigReplace.Placeholder.REPORT_ID, "" + id)
                                .addPlaceholder(ConfigReplace.Placeholder.PLAYER_NAME, reported)
                                .addPlaceholder(ConfigReplace.Placeholder.REPORTER_NAME, reporter)
                                .addPlaceholder(ConfigReplace.Placeholder.REASON, reason))));
                    }
                } catch (SQLException e) {
                    DebugReport.foundDatabase("Database couldn't finish task!", Thread.currentThread());
                }
                if (!used) sender.sendMessage(Formatter.chatColors(this.configOptions.getString("messages.no-result")));
            });
            return false;
        }
        // following command is:
        // /adminreport player PLAYER_NAME
        // PLAYER_NAME will be player's name
        if (args.length == 2 && args[0].equalsIgnoreCase("player")) {
            // database manipulation
            Bukkit.getScheduler().runTaskAsynchronously(reportClass, () -> {
                boolean used = false;
                try (PreparedStatement ps = database.prepareStatement("SELECT * FROM " + database.getTable() + " WHERE reporter = (?);")) {
                    ps.setString(1, args[1]);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        if (!used) used = true;
                        String reported = rs.getString("reported");
                        String reporter = rs.getString("reporter");
                        String reason = rs.getString("reason");
                        int id = rs.getInt("id");
                        sender.sendMessage(Formatter.chatColors(this.configOptions.getString("messages.player-result", new ConfigReplace()
                                .addPlaceholder(ConfigReplace.Placeholder.REPORT_ID, "" + id)
                                .addPlaceholder(ConfigReplace.Placeholder.PLAYER_NAME, reported)
                                .addPlaceholder(ConfigReplace.Placeholder.REPORTER_NAME, reporter)
                                .addPlaceholder(ConfigReplace.Placeholder.REASON, reason))));
                    }
                } catch (SQLException e) {
                    DebugReport.foundDatabase("Database couldn't finish task!", Thread.currentThread());
                }
                if (!used) sender.sendMessage(Formatter.chatColors(this.configOptions.getString("messages.no-result")));
            });
            return false;
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("clear")) {
            // following command is:
            // /adminreport clear id NUMBER
            // NUMBER will be replaced with actual number
            if (args[1].equalsIgnoreCase("id")) {
                int id;
                try { id = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) { sender.sendMessage(Formatter.chatColors(this.configOptions.getString("messages.numberexception"))); return false; }
                // database manipulation
                Bukkit.getScheduler().runTaskAsynchronously(reportClass, () -> {
                    try (PreparedStatement ps = database.prepareStatement("DELETE FROM " + database.getTable() + " WHERE id = (?);")) {
                        ps.setInt(1, id);
                        ps.executeUpdate();
                        sender.sendMessage(Formatter.chatColors(this.configOptions.getString("messages.deleted-record", new ConfigReplace()
                                .addPlaceholder(ConfigReplace.Placeholder.REPORT_ID, "" + id))));
                    } catch (SQLException e) {
                        DebugReport.foundDatabase("Database couldn't finish task!", Thread.currentThread());
                    }
                });
                return false;
            }
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            List<Component> helpList = this.configOptions.getComponentList("messages.help", null);
            helpList.forEach(sender::sendMessage);
            return false;
        }
        sender.sendMessage(Formatter.chatColors(this.configOptions.getString("messages.invalid-use", new ConfigReplace()
                .addPlaceholder(ConfigReplace.Placeholder.COMMAND, "adminreport")
                .addPlaceholder(ConfigReplace.Placeholder.COMMAND_ARGS, "help"))));
        return false;
    }
}
