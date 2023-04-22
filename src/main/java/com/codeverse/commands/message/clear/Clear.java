package com.codeverse.commands.message.clear;

import com.codeverse.main.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.List;

public class Clear extends ListenerAdapter {
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
		if (e.getName().equals("message")) {
			if (e.getSubcommandName().equals("clear")) {
				if (!e.getMember().hasPermission(e.getChannel().asTextChannel(), Permission.MESSAGE_MANAGE)) {
					EmbedBuilder eb = new EmbedBuilder();
					eb.setTitle("**Error**");
					eb.setDescription("You do not have permission to use this command.");
					eb.setColor(Color.RED);
					e.replyEmbeds(eb.build()).setEphemeral(true).queue();
					return;
				}
				
				int amount = e.getOption("amount").getAsInt();
				
				if (amount > 100) {
					EmbedBuilder eb = new EmbedBuilder();
					eb.setTitle("**Error**");
					eb.setDescription("You can only clear down to 100 messages at a time.");
					eb.setColor(Color.RED);
					e.replyEmbeds(eb.build()).setEphemeral(true).queue();
					return;
				}
				
				EmbedBuilder eb = new EmbedBuilder();
				eb.clear();
				
				List<Message> messages = e.getChannel().getHistory().retrievePast(amount).complete();
				
				e.getChannel().purgeMessages(messages);
				
				eb.setTitle("**Messages cleared**");
				eb.setDescription("Cleared " + amount + " messages.");
				eb.setColor(Color.CYAN);
				e.replyEmbeds(eb.build()).queue();
			}
		}
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		String message = e.getMessage().getContentRaw();
		String command = message.split(" ")[0];
		String subcommand = message.split(" ")[1];
		
		if (command.equals(Bot.getPrefix() + "message")) {
			if (subcommand.equals("clear")) {
				if (!e.getMember().hasPermission(e.getChannel().asTextChannel(), Permission.MESSAGE_MANAGE)) {
					e.getMessage().delete().queue();
					EmbedBuilder eb = new EmbedBuilder();
					eb.setTitle("**Error**");
					eb.setDescription("You do not have permission to use this command.");
					eb.setColor(Color.RED);
					e.getMessage().replyEmbeds(eb.build()).queue();
					e.getAuthor().openPrivateChannel().queue((channel) -> {
						channel.sendMessageEmbeds(eb.build()).queue();
					});
					return;
				}
				
				int amount;
				try {
					amount = Integer.parseInt(message.split(" ")[2]);
				} catch (NumberFormatException ex) {
					EmbedBuilder eb = new EmbedBuilder();
					eb.setTitle("**Error**");
					eb.setDescription("Please enter a valid number.");
					eb.setColor(Color.RED);
					e.getMessage().replyEmbeds(eb.build()).queue();
					return;
				}
				
				if (amount > 100) {
					EmbedBuilder eb = new EmbedBuilder();
					eb.setTitle("**Error**");
					eb.setDescription("You can only clear down to 100 messages at a time.");
					eb.setColor(Color.RED);
					e.getMessage().replyEmbeds(eb.build()).queue();
					return;
				}
				
				EmbedBuilder eb = new EmbedBuilder();
				eb.clear();
				
				List<Message> messages = e.getChannel().getHistory().retrievePast(amount).complete();
				
				e.getChannel().purgeMessages(messages);
				
				eb.setTitle("**Messages cleared**");
				eb.setDescription("Cleared " + amount + " messages.");
				eb.setColor(Color.CYAN);
				e.getMessage().replyEmbeds(eb.build()).queue();
			}
		}
	}
}
