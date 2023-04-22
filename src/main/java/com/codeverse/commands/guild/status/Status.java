package com.codeverse.commands.guild.status;

import ch.qos.logback.classic.Logger;
import com.codeverse.main.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.TimeFormat;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class Status extends ListenerAdapter {
	Logger logger = (Logger) LoggerFactory.getLogger(Status.class);
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
		if (e.getName().equals("guild")) {
			if (e.getSubcommandName().equals("status")) {
				boolean showDetails = false;
				
				if (e.getOption("details") != null) {
					showDetails = e.getOption("details").getAsBoolean();
				}
				
				
				EmbedBuilder eb = new EmbedBuilder();
				eb.setTitle("**Guild Information**");
				eb.setAuthor(e.getUser().getAsTag(), null, e.getUser().getAvatarUrl());
				eb.setFooter("Made by Codeverse", Bot.getDeveloperIcon());
				eb.setTimestamp(e.getTimeCreated());
				eb.setColor(Color.GREEN);
				
				TimeFormat timeFormat = showDetails ? TimeFormat.DATE_TIME_LONG : TimeFormat.DATE_TIME_SHORT;
				String timestamp = timeFormat.format(e.getGuild().getTimeCreated());
				
				eb.addField("**Guild Name**", e.getGuild().getName(), true);
				eb.addField("**Guild ID**", e.getGuild().getId(), true);
				eb.addField("**Guild Created At**", timestamp, true);
				eb.addField("**Guild Owner**", e.getGuild().getOwner().getUser().getAsMention(), true);
				eb.addField("**Guild Owner ID**", e.getGuild().getOwner().getUser().getId(), true);
				eb.addField("**Guild Member Count**", String.valueOf(e.getGuild().getMemberCount()), true);
				eb.addField("**Guild Channel Count**", String.valueOf(e.getGuild().getChannels().size()), true);
				
				if (showDetails) {
					eb.addField("**Guild Role Count**", String.valueOf(e.getGuild().getRoles().size()), true);
					eb.addField("**Guild Boost Count**", String.valueOf(e.getGuild().getBoostCount()), true);
					eb.addField("**Guild Boost Tier**", String.valueOf(e.getGuild().getBoostTier()), true);
					eb.addField("**Guild Verification Level**", String.valueOf(e.getGuild().getVerificationLevel()), true);
					eb.addField("**Guild Explicit Content Filter**", String.valueOf(e.getGuild().getExplicitContentLevel()), true);
					eb.addField("**Guild Emote Count**", String.valueOf(e.getGuild().getEmojis().size()), true);
					eb.addField("**Guild Max Emote Count**", String.valueOf(e.getGuild().getMaxEmojis()), true);
					eb.addField("**Guild Max Bitrate**", String.valueOf(e.getGuild().getMaxBitrate()), true);
					eb.addField("**Guild Max File Size**", String.valueOf(e.getGuild().getMaxFileSize()), true);
					eb.addField("**Guild Max Members**", String.valueOf(e.getGuild().getMaxMembers()), true);
					eb.addField("**Guild Max Presences**", String.valueOf(e.getGuild().getMaxPresences()), true);
				}
				
				
				eb.setColor(Color.GREEN);
				
				e.replyEmbeds(eb.build()).queue();
				
				logger.info("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ") ] Guild Status command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ")");
				Bot.updateLog("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ") ] Guild Status command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ")", "info");
			}
		}
	}
}
