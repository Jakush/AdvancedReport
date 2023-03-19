package me.retamrovec.advancedreport.discord;

import me.retamrovec.advancedreport.debug.DebugReport;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;

public class Bot {

    JDA jda;
    public Bot(@NotNull JDA jda) {
        this.jda = jda;
    }

    public void sendEmbed(String id, MessageEmbed message) {
        TextChannel channel = jda.getTextChannelById(id);
        if (channel == null) {
            DebugReport.foundMajor("Channel " + id + " was not found! Sending report to discord was cancelled.", false, true, false);
            return;
        }
        channel.sendMessageEmbeds(message).queue();
    }
}
