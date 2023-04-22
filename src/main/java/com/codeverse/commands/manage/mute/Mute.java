package com.codeverse.commands.manage.mute;

import ch.qos.logback.classic.Logger;
import com.codeverse.main.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Mute extends ListenerAdapter {
	Logger logger = (Logger) LoggerFactory.getLogger(Mute.class);
	
	User user;
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
		if (e.getName().equals("manage")) {
			if (e.getSubcommandName().equals("mute")) {
				user = e.getOption("user").getAsUser();
				
				String reason;
				if (e.getOption("reason") != null) {
					reason = e.getOption("reason").getAsString();
				} else {
					reason = "No reason.";
				}

				String time = Objects.requireNonNull(e.getOption("time")).getAsString();
				
				String timeStr = time.split("")[time.length() - 1];
				String timeMessage;
				
				int timeNum = Integer.parseInt(time.substring(0, time.length() - 1));
				
				TimeUnit timeUnit;
				
				if (timeStr.equalsIgnoreCase("s") || timeStr.equalsIgnoreCase("sec") || timeStr.equalsIgnoreCase("secs") || timeStr.equalsIgnoreCase("second") || timeStr.equalsIgnoreCase("seconds")) {
					timeUnit = TimeUnit.SECONDS;
					String period = timeNum == 1 ? "second" : "seconds";
					timeMessage = timeNum + " " + period;
				} else if (timeStr.equalsIgnoreCase("m") || (timeStr.equalsIgnoreCase("min")) || (timeStr.equalsIgnoreCase("mins")) || (timeStr.equalsIgnoreCase("minute")) || (timeStr.equalsIgnoreCase("minutes"))) {
					timeUnit = TimeUnit.MINUTES;
					String period = timeNum == 1 ? "minute" : "minutes";
					timeMessage = timeNum + " " + period;
				} else if (timeStr.equalsIgnoreCase("h") || (timeStr.equalsIgnoreCase("hr")) || (timeStr.equalsIgnoreCase("hrs")) || (timeStr.equalsIgnoreCase("hour")) || (timeStr.equalsIgnoreCase("hours"))) {
					String period = timeNum == 1 ? "hour" : "hours";
					timeMessage = timeNum + " " + period;
					timeUnit = TimeUnit.HOURS;
				} else if (timeStr.equalsIgnoreCase("d") || (timeStr.equalsIgnoreCase("day")) || (timeStr.equalsIgnoreCase("days"))) {
					String period = timeNum == 1 ? "day" : "days";
					timeMessage = timeNum + " " + period;
					timeUnit = TimeUnit.DAYS;
				} else {
					EmbedBuilder eb = new EmbedBuilder();
					eb.setColor(Color.RED);
					eb.setTitle("**Failed to mute an user.**");
					eb.setDescription("Failed to mute " + user.getAsMention());
					eb.addField("Duration", time, false);
					eb.addField("Reason", reason, false);
					eb.addField("Error", "Invalid time format.", false);
					eb.addField("Example", "1d, 1h, 1m, 1s", false);
					e.replyEmbeds(eb.build()).setEphemeral(true).queue();
					return;
				}
				
				Button unmuteButton = Button.success("unmute", "Unmute");
				
				try {
					e.getGuild().timeoutFor(user, timeNum, timeUnit).reason(reason).queue();
					EmbedBuilder eb = new EmbedBuilder();
					eb.setColor(Color.GREEN);
					eb.setTitle("**Suceessfully muted an user.**");
					eb.setDescription("Muted " + user.getAsMention());
					eb.addField("Duration", timeMessage, false);
					eb.addField("Reason", reason, false);
					e.reply(user.getAsMention()).addEmbeds(eb.build()).setActionRow(unmuteButton).queue();
					String log = "[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ") ] Mute command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") [ " + user.getAsTag() + " (" + user.getId() + ") ] [ " + reason + " ] [ " + timeMessage + " ]";
					logger.info(log);
					Bot.updateLog(log, "info");
				} catch (Exception ex) {
					EmbedBuilder eb = new EmbedBuilder();
					eb.setColor(Color.RED);
					eb.setTitle("**Failed to mute an user.**");
					eb.setDescription("Failed to mute " + user.getAsMention());
					eb.addField("Duration", timeMessage, false);
					eb.addField("**Reason**", reason, false);
					e.replyEmbeds(eb.build()).queue();
					String log = "[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ") ] Mute command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") [ " + user.getAsTag() + " (" + user.getId() + ") ] [ " + reason + " ] [ " + timeMessage + " ], but failed to mute.";
					logger.info(log);
					Bot.updateLog(log, "error");
					ex.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void onButtonInteraction(ButtonInteractionEvent e) {
		if (e.getComponentId().equals("unmute")) {
			if (!e.getMember().hasPermission(Permission.MODERATE_MEMBERS)) {
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(Color.RED);
				eb.setTitle("**Failed to unmute an user.**");
				eb.setDescription("Failed to unmute " + user.getAsMention());
				eb.addField("Reason", "You don't have permission to unmute users.", false);
				e.replyEmbeds(eb.build()).setEphemeral(true).queue();
				String log = "[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ") ] Unmute command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") [ " + user.getAsTag() + " (" + user.getId() + ") ] but failed to unmute.";
				logger.info(log);
				Bot.updateLog(log, "warn");
				return;
			}
			
			e.getGuild().removeTimeout(user).queue();
			EmbedBuilder eb = new EmbedBuilder();
			eb.setColor(Color.GREEN);
			eb.setTitle("**Suceessfully unmuted an user.**");
			eb.setDescription("Unmuted " + user.getAsMention());
			eb.setAuthor(e.getUser().getAsTag(), null, e.getUser().getAvatarUrl());
			eb.setFooter("Unmuted by " + e.getUser().getAsTag(), e.getUser().getAvatarUrl());
			e.reply(user.getAsMention()).addEmbeds(eb.build()).queue();
			Button unmutedButton = Button.danger("unmuted", "Unmuted");
			e.getMessage().editMessageEmbeds(eb.build()).setActionRow(unmutedButton.asDisabled()).queue();
			String log = "[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ") ] Unmute command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") [ " + user.getAsTag() + " (" + user.getId() + ") ]";
			logger.info(log);
			Bot.updateLog(log, "info");
		}
	}
}