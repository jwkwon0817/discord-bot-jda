package com.codeverse.commands.user.avatar;

import com.codeverse.main.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class Avatar extends ListenerAdapter {
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
		if (e.getName().equals("user")) {
			if (e.getSubcommandName().equals("avatar")) {
				User user;
				try {
					user = e.getOption("user").getAsUser();
				} catch (NullPointerException ex) {
					user = e.getUser();
				}
				
				String userAvatar = user.getAvatarUrl();
				
				EmbedBuilder eb = new EmbedBuilder();
				eb.setImage(userAvatar);
				eb.setAuthor(user.getAsTag(), userAvatar, user.getAvatarUrl());
				
				if (userAvatar == null) {
					userAvatar = user.getDefaultAvatarUrl();
					eb.setImage(userAvatar);
					eb.setAuthor(user.getAsTag(), userAvatar, user.getDefaultAvatarUrl());
				}
				
				eb.setTitle("**User Avatar**");

				eb.setFooter("Made by Codeverse", Bot.getDeveloperIcon());
				eb.setTimestamp(e.getTimeCreated());
				eb.setColor(Color.CYAN);
				
				e.replyEmbeds(eb.build()).queue();
			}
		}
	}
}
