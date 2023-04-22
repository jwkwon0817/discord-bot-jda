package com.codeverse.commands.utils.get_ip_by_domain;

import ch.qos.logback.classic.Logger;
import com.codeverse.main.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class GetIpByDomain extends ListenerAdapter {
	Logger logger = (Logger) LoggerFactory.getLogger(GetIpByDomain.class);
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
		if (e.getName().equals("utils")) {
			if (e.getSubcommandName().equals("get_ip_by_domain")) {
				String domain = e.getOption("domain").getAsString();
				
				EmbedBuilder eb = new EmbedBuilder();
				eb.setTitle("**IP Address**");
				eb.setAuthor(e.getUser().getAsTag(), null, e.getUser().getAvatarUrl());
				eb.setFooter("Made by Codeverse", Bot.getDeveloperIcon());
				eb.setTimestamp(e.getTimeCreated());
				
				try {
					InetAddress[] addresses = InetAddress.getAllByName(domain);
					eb.setColor(Color.GREEN);
					eb.setDescription("IP address of **" + domain + "**");
					StringBuilder sb = new StringBuilder();
					for (InetAddress address : addresses) {
						sb.append("**·** ").append(address.getHostAddress()).append("\n");
					}
					eb.addField("", sb.toString(), false);
					logger.info("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ") ] IP command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") [ " + domain + " ]");
					Bot.updateLog("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ") ] IP command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") [ " + domain + " ]", "info");
				} catch (UnknownHostException ex) {
					eb.setColor(Color.RED);
					eb.setDescription("Unknown host: **" + domain + "**");
					logger.warn("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ") ] IP command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") [ " + domain + " ], but failed because of an unknown host.");
					Bot.updateLog("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ") ] IP command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") [ " + domain + " ], but failed because of an unknown host.", "warn");
				}
				
				e.replyEmbeds(eb.build()).queue();
			}
		}
	}
}
