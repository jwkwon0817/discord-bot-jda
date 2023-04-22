package com.codeverse.commands.message.reverse;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class Reverse extends ListenerAdapter {
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
		if (e.getName().equals("message")) {
			if (e.getSubcommandName().equals("reverse")) {
				String text = e.getOption("message").getAsString();
				
				EmbedBuilder eb = new EmbedBuilder();
				eb.clear();
				
				eb.setTitle("**Reversed message**");
				eb.setDescription("Original message: " + text);
				eb.addField("Reversed message", reverse(text), false);
				eb.setColor(Color.CYAN);
				e.replyEmbeds(eb.build()).queue();
			}
		}
	}
	public static String reverse(String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}
		return str.charAt(str.length() - 1) + reverse(str.substring(0, str.length() - 1));
	}
}
