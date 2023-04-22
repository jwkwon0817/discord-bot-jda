package com.codeverse.commands.manage.ban;

import ch.qos.logback.classic.Logger;
import com.codeverse.main.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Ban extends ListenerAdapter {
	Logger logger = (Logger) LoggerFactory.getLogger(Ban.class);
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
		if (e.getName().equals("manage")) {
			if (e.getSubcommandName().equals("ban")) {
				User user = e.getOption("user").getAsUser();
				
				String reason;
				if (e.getOption("reason") != null) {
					reason = e.getOption("reason").getAsString();
				} else {
					reason = "No reason.";
				}
				
				e.getGuild().ban(user, 0, TimeUnit.SECONDS).reason(reason).queue(
						success -> {
							EmbedBuilder eb = new EmbedBuilder();
							eb.setColor(Color.GREEN);
							eb.setTitle("**Suceessfully banned an user.**");
							eb.setDescription("Banned " + user.getAsMention());
							eb.addField("Reason", reason, false);
							e.replyEmbeds(eb.build()).queue();
							String log = "[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ") ] Ban command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") [ " + user.getAsTag() + " (" + user.getId() + ") ] [ " + reason + " ]";
							logger.info(log);
							Bot.updateLog(log, "info");
						}, exception -> {
							EmbedBuilder eb = new EmbedBuilder();
							eb.setColor(Color.RED);
							eb.setTitle("**Failed to ban an user.**");
							eb.setDescription("Failed to ban " + user.getAsMention());
							eb.addField("**Reason**", exception.getMessage(), false);
							e.replyEmbeds(eb.build()).queue();
							String log = "[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ") ] Ban command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") [ " + user.getAsTag() + " (" + user.getId() + ") ] [ " + reason + " ], but failed. Reason: " + exception.getMessage();
							logger.info(log);
							Bot.updateLog(log, "error");
						}
				);
			}
		}
	}
}
