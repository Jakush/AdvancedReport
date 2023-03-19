package me.retamrovec.advancedreport.listeners;

import me.retamrovec.advancedreport.AdvancedReport;
import me.retamrovec.advancedreport.config.ConfigOptions;
import me.retamrovec.advancedreport.config.ConfigReplace;
import me.retamrovec.advancedreport.utils.Formatter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Date;

public class InventoryListener implements Listener {
    AdvancedReport reportClass;
    ConfigOptions configOptions;
    public InventoryListener(AdvancedReport reportClass) {
        this.reportClass = reportClass;
        this.configOptions = this.reportClass.getConfigOptions();
    }
    @EventHandler
    public void onClick(@NotNull InventoryClickEvent e) {
        String title = PlainTextComponentSerializer.plainText().serialize(e.getView().title());
        if (title.equals("Report")) {
            e.setCancelled(true);
            Player p = (Player) e.getWhoClicked();
            int slot = e.getSlot();
            if (slot == 11) {
                String reportReason = this.reportClass.getReportReasons().getOrDefault(p.getUniqueId(), "REASON IS MISING");
                OfflinePlayer reported = Bukkit.getOfflinePlayer(this.reportClass.getReporting().get(p.getUniqueId()));
                Bukkit.getOnlinePlayers().forEach(player -> {
                    if (player.hasPermission("advancedreport.receive")) {
                        player.sendMessage(Formatter.chatColors(this.configOptions.getString("messages.alert", new ConfigReplace()
                                .addPlaceholder(ConfigReplace.Placeholder.PLAYER_NAME, reported.getName())
                                .addPlaceholder(ConfigReplace.Placeholder.REPORTER_NAME, p.getName())
                                .addPlaceholder(ConfigReplace.Placeholder.REASON, reportReason))));
                    }
                });
                p.sendMessage(Formatter.chatColors(this.configOptions.getString("messages.reported", new ConfigReplace()
                        .addPlaceholder(ConfigReplace.Placeholder.PLAYER_NAME, reported.getName())
                        .addPlaceholder(ConfigReplace.Placeholder.REPORTER_NAME, p.getName())
                        .addPlaceholder(ConfigReplace.Placeholder.REASON, reportReason))));
                if (this.reportClass.getBot() != null) {
                    EmbedBuilder handler = new EmbedBuilder();
                    handler.setTitle(this.configOptions.getString("discord-bot.log-message.title", new ConfigReplace()
                            .addPlaceholder(ConfigReplace.Placeholder.REPORTER_NAME, p.getName())
                            .addPlaceholder(ConfigReplace.Placeholder.PLAYER_NAME, reported.getName())));
                    handler.setColor(new Color(
                            this.configOptions.getInt("discord-bot.log-message.color.r"),
                            this.configOptions.getInt("discord-bot.log-message.color.g"),
                            this.configOptions.getInt("discord-bot.log-message.color.b")));
                    handler.setDescription(this.configOptions.getString("discord-bot.log-message.description", new ConfigReplace()
                            .addPlaceholder(ConfigReplace.Placeholder.REASON, reportReason)
                            .addPlaceholder(ConfigReplace.Placeholder.REPORTER_NAME, p.getName())
                            .addPlaceholder(ConfigReplace.Placeholder.PLAYER_NAME, reported.getName())));
                    if (this.configOptions.getBoolean("discord-bot.log-message.timestamp")) handler.setTimestamp(new Date().toInstant());
                    if (this.configOptions.getBoolean("discord-bot.log-message.thumbnail.enabled")) {
                        handler.setThumbnail(this.configOptions.getString("discord-bot.log-message.thumbnail.url", new ConfigReplace()
                                .addPlaceholder(ConfigReplace.Placeholder.PLAYER_UUID, reported.getUniqueId().toString())
                                .addPlaceholder(ConfigReplace.Placeholder.REPORTER_UUID, p.getUniqueId().toString())
                                .addPlaceholder(ConfigReplace.Placeholder.REPORTER_NAME, p.getName())
                                .addPlaceholder(ConfigReplace.Placeholder.PLAYER_NAME, reported.getName())));
                    }
                    handler.addField(this.configOptions.getString("discord-bot.log-message.field-reason.title"),
                            this.configOptions.getString("discord-bot.log-message.field-reason.description", new ConfigReplace()
                                    .addPlaceholder(ConfigReplace.Placeholder.REASON, reportReason)
                                    .addPlaceholder(ConfigReplace.Placeholder.REPORTER_NAME, p.getName())
                                    .addPlaceholder(ConfigReplace.Placeholder.PLAYER_NAME, reported.getName())), true);
                    handler.addField(this.configOptions.getString("discord-bot.log-message.field-reported.title"),
                            this.configOptions.getString("discord-bot.log-message.field-reported.description", new ConfigReplace()
                                    .addPlaceholder(ConfigReplace.Placeholder.REASON, reportReason)
                                    .addPlaceholder(ConfigReplace.Placeholder.REPORTER_NAME, p.getName())
                                    .addPlaceholder(ConfigReplace.Placeholder.PLAYER_NAME, reported.getName())), true);
                    this.reportClass.getBot().sendEmbed(this.configOptions.getString("discord-bot.log-channel"), handler.build());
                }
                e.getInventory().close();
                this.reportClass.getReportReasons().remove(p.getUniqueId());
                this.reportClass.getReporting().remove(p.getUniqueId());
                return;
            }
            if (e.getSlot() == 15) {
                e.getInventory().close();
                this.reportClass.getReportReasons().remove(p.getUniqueId());
                this.reportClass.getReporting().remove(p.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onClose(@NotNull InventoryCloseEvent e) {
        String title = PlainTextComponentSerializer.plainText().serialize(e.getView().title());
        if (title.equals("Report")) {
            Player p = (Player) e.getPlayer();
            this.reportClass.getReportReasons().remove(p.getUniqueId());
            this.reportClass.getReporting().remove(p.getUniqueId());
        }
    }
}
