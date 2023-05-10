package me.retamrovec.advancedreport;

import me.retamrovec.advancedreport.alerts.AlertTask;
import me.retamrovec.advancedreport.commands.CommandManager;
import me.retamrovec.advancedreport.commands.ReportAdminCommand;
import me.retamrovec.advancedreport.commands.ReportCommand;
import me.retamrovec.advancedreport.config.ConfigOptions;
import me.retamrovec.advancedreport.database.Database;
import me.retamrovec.advancedreport.database.SQLite;
import me.retamrovec.advancedreport.alerts.DiscordBot;
import me.retamrovec.advancedreport.listeners.InventoryListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

@SuppressWarnings("unused")
public final class AdvancedReport extends JavaPlugin {

    private DiscordBot discordBot;
    private Database db;
    private ConfigOptions configOptions;
    private HashMap<UUID, String> reportReasons;
    private HashMap<UUID, UUID> reporting;
    private AlertTask task;
    private Metrics metrics;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.configOptions = new ConfigOptions(this);
        this.init();
        this.reportReasons = new HashMap<>();
        this.reporting = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(new InventoryListener(this), this);

        CommandManager.register("report", new ReportCommand(this));
        CommandManager.register("adminreport", new ReportAdminCommand(this));

        this.db = new SQLite(this);
        this.db.connect();

        this.task = new AlertTask(this);
        this.task.start();

        if (this.configOptions.getBoolean("bStats")) {
            this.metrics = new Metrics(this, 18067);
        }
    }

    @Override
    public void onDisable() {
        this.task.kill();
        this.db.disconnect();
        this.metrics.shutdown();
    }

    public void init() {
        reloadConfig();
        if (!this.configOptions.getBoolean("discord-bot.enabled")) return;
        String token = this.configOptions.getString("discord-bot.token") == null ? "" : this.configOptions.getString("discord-bot.token");
        assert token != null;
        if (token.equals("")) return;
        JDABuilder bot = JDABuilder.createDefault(token);
        bot.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        Activity activity = Activity.of(Activity.ActivityType.valueOf(this.configOptions.getString("discord-bot.activity")), this.getConfigOptions().getString("discord-bot.status"));
        bot.setActivity(activity);
        bot.setChunkingFilter(ChunkingFilter.NONE);
        bot.setLargeThreshold(50);
        bot.setStatus(OnlineStatus.ONLINE);
        JDA jda = bot.build();
        this.discordBot = new DiscordBot(jda);

        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public DiscordBot getBot() {
        return discordBot;
    }

    public ConfigOptions getConfigOptions() {
        return configOptions;
    }

    public HashMap<UUID, String> getReportReasons() {
        return reportReasons;
    }

    public HashMap<UUID, UUID> getReporting() {
        return reporting;
    }

    public Database getDatabase() {
        return db;
    }
}
