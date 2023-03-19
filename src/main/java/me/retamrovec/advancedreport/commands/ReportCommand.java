package me.retamrovec.advancedreport.commands;

import me.retamrovec.advancedreport.AdvancedReport;
import me.retamrovec.advancedreport.config.ConfigOptions;
import me.retamrovec.advancedreport.config.ConfigReplace;
import me.retamrovec.advancedreport.inventories.Builder;
import me.retamrovec.advancedreport.utils.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ReportCommand implements CommandExecutor {
    AdvancedReport reportClass;
    ConfigOptions configOptions;
    @Contract(pure = true)
    public ReportCommand(@NotNull AdvancedReport reportClass) {
        this.reportClass = reportClass;
        this.configOptions = this.reportClass.getConfigOptions();
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Formatter.chatColors(this.configOptions.getString("messages.player-only")));
            return false;
        }
        if (!player.hasPermission("advancedreport.use")) {
            sender.sendMessage(Formatter.chatColors(this.configOptions.getString("messages.no-permissions")));
            return false;
        }
        if (args.length < 2) {
            sender.sendMessage(Formatter.chatColors(this.configOptions.getString("messages.invalid-use")));
            return false;
        }
        Builder invManager = Builder.getInstance("Report", 27, player);
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            builder.append(args[i]).append(" ");
        }
        String reportReason = builder.toString();
        OfflinePlayer reported = Bukkit.getOfflinePlayer(args[0]);

        // Yes Button
        ItemStack yes = new ItemStack(Material.LIME_WOOL);
        ItemMeta yesMeta = yes.getItemMeta();
        yesMeta.displayName(Formatter.chatColors(this.configOptions.getString("report-menu.yes-button.display_name")));
        yesMeta.lore(this.configOptions.getComponentList("report-menu.yes-button.lore", null));
        yes.setItemMeta(yesMeta);

        // No Button
        ItemStack no = new ItemStack(Material.RED_WOOL);
        ItemMeta noMeta = no.getItemMeta();
        noMeta.displayName(Formatter.chatColors(this.configOptions.getString("report-menu.no-button.display_name")));
        noMeta.lore(this.configOptions.getComponentList("report-menu.no-button.lore", null));
        no.setItemMeta(noMeta);

        // Player Head
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
        skullMeta.setOwningPlayer(reported);
        skullMeta.displayName(Formatter.chatColors(this.configOptions.getString("report-menu.player-head.display_name", new ConfigReplace()
                .addPlaceholder(ConfigReplace.Placeholder.PLAYER_NAME, reported.getName()))));
        skullMeta.lore(this.configOptions.getComponentList("report-menu.player-head.lore", new ConfigReplace()
                .addPlaceholder(ConfigReplace.Placeholder.REASON, reportReason)
                .addPlaceholder(ConfigReplace.Placeholder.PLAYER_NAME, reported.getName())));
        playerHead.setItemMeta(skullMeta);

        invManager.addItem(4, playerHead);
        invManager.addItem(11, yes);
        invManager.addItem(15, no);
        player.openInventory(invManager.getInventory());
        this.reportClass.getReportReasons().put(player.getUniqueId(), reportReason);
        this.reportClass.getReporting().put(player.getUniqueId(), reported.getUniqueId());
        return false;
    }
}
