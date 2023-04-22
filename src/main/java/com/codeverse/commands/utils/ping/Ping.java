package com.codeverse.commands.utils.ping;

import ch.qos.logback.classic.Logger;
import com.codeverse.main.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.LoggerFactory;

public class Ping extends ListenerAdapter {
	Logger logger = (Logger) LoggerFactory.getLogger(Ping.class);
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
		if (e.getName().equals("utils")) {
			if (e.getSubcommandName().equals("ping")) {
				EmbedBuilder eb = new EmbedBuilder();
				eb.setTitle("Pong!");
				long ping = e.getJDA().getGatewayPing();
				eb.setDescription(ping + "ms");
				eb.setColor(0x00ff00);
				eb.setAuthor(e.getUser().getAsTag(), null, e.getUser().getAvatarUrl());
				eb.setFooter("Made by Codeverse", Bot.getDeveloperIcon());
				eb.setTimestamp(e.getTimeCreated());
				e.replyEmbeds(eb.build()).queue();
				logger.info("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ") ] Ping command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") [ " + ping + "ms ]");
				Bot.updateLog("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ") ] Ping command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") [ " + ping + "ms ]", "info");
			}
		}
	}
}
