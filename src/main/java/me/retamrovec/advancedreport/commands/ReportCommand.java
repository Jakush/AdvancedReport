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

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ReportCommand implements CommandExecutor {
    AdvancedReport reportClass;
    Builder invManager;
    ConfigOptions configOptions;
    @Contract(pure = true)
    public ReportCommand(@NotNull AdvancedReport reportClass) {
        this.reportClass = reportClass;
        this.configOptions = this.reportClass.getConfigOptions();

        this.invManager = Builder.getInstance("Report", 27, null);
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

        invManager.addItem(11, yes);
        invManager.addItem(15, no);
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
            sender.sendMessage(Formatter.chatColors(this.configOptions.getString("messages.invalid-use", new ConfigReplace()
                    .addPlaceholder(ConfigReplace.Placeholder.COMMAND, "report")
                    .addPlaceholder(ConfigReplace.Placeholder.COMMAND_ARGS, this.configOptions.getString("messages.command.args")))));
            return false;
        }
        OfflinePlayer reported = Bukkit.getOfflinePlayer(args[0]);
        if (reported.getUniqueId().equals(player.getUniqueId())) {
            sender.sendMessage(Formatter.chatColors(this.configOptions.getString("messages.report-yourself", new ConfigReplace()
                    .addPlaceholder(ConfigReplace.Placeholder.PLAYER_NAME, reported.getName()))));
            return false;
        }
        if (!reported.hasPlayedBefore()) {
            sender.sendMessage(Formatter.chatColors(this.configOptions.getString("messages.invalid-player", new ConfigReplace()
                    .addPlaceholder(ConfigReplace.Placeholder.PLAYER_NAME, reported.getName()))));
            return false;
        }
        String builder = IntStream.range(1, args.length).mapToObj(i -> args[i] + " ").collect(Collectors.joining());

        // Player Head
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
        skullMeta.setOwningPlayer(reported);
        skullMeta.displayName(Formatter.chatColors(this.configOptions.getString("report-menu.player-head.display_name", new ConfigReplace()
                .addPlaceholder(ConfigReplace.Placeholder.PLAYER_NAME, reported.getName()))));
        skullMeta.lore(this.configOptions.getComponentList("report-menu.player-head.lore", new ConfigReplace()
                .addPlaceholder(ConfigReplace.Placeholder.REASON, builder)
                .addPlaceholder(ConfigReplace.Placeholder.PLAYER_NAME, reported.getName())));
        playerHead.setItemMeta(skullMeta);

        invManager.addItem(4, playerHead);
        player.openInventory(invManager.getInventory());
        this.reportClass.getReportReasons().put(player.getUniqueId(), builder);
        this.reportClass.getReporting().put(player.getUniqueId(), reported.getUniqueId());
        return false;
    }
}
