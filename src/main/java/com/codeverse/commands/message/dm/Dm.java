package com.codeverse.commands.message.dm;

import com.codeverse.main.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class Dm extends ListenerAdapter {
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
		if (e.getName().equals("message")) {
			if (e.getSubcommandName().equals("dm")) {
				if (!e.getUser().getId().equals(Bot.getOwnerId())) {
					EmbedBuilder eb = new EmbedBuilder();
					eb.setTitle("**Error**");
					eb.setDescription("You do not have permission to use this command.");
					eb.setColor(Color.RED);
					e.replyEmbeds(eb.build()).setEphemeral(true).queue();
					return;
				}
				
				User user = e.getOption("user").getAsUser();
				String text = e.getOption("message").getAsString();
				
				EmbedBuilder eb = new EmbedBuilder();
				eb.clear();
				
				user.openPrivateChannel().queue((channel) -> {
					channel.sendMessage(text).queue(
							message -> {
								eb.setTitle("**Message sent**");
								eb.setDescription("Message sent to " + user.getAsTag() + " (" + user.getId() + ")");
								eb.addField("Message", text, false);
								eb.setColor(Color.GREEN);
								e.replyEmbeds(eb.build()).setEphemeral(true).queue();
							},
							error -> {
								eb.setTitle("**Message not sent**");
								eb.setDescription("Failed to send message to " + user.getAsTag() + " (" + user.getId() + ")");
								eb.addField("Error", error.getMessage(), false);
								eb.setColor(Color.RED);
								e.replyEmbeds(eb.build()).queue();
							}
					);
				});
			}
		}
	}
}
