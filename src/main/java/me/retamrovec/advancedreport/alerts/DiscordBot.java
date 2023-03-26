package me.retamrovec.advancedreport.alerts;

import me.retamrovec.advancedreport.debug.DebugReport;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;

public class DiscordBot {

    JDA jda;
    public DiscordBot(@NotNull JDA jda) {
        this.jda = jda;
    }

    public void sendEmbed(String id, MessageEmbed message) {
        TextChannel channel = jda.getTextChannelById(id);
        if (channel == null) {
            DebugReport.foundIssue("Channel " + id + " was not found! Sending report to discord was cancelled.", new StackTraceElement[]{
                    new StackTraceElement(getClass().getName(), "sendEmbed", "DiscordBot.java", 17)
            });
            return;
        }
        channel.sendMessageEmbeds(message).queue();
    }
}
