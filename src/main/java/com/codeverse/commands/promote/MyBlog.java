package com.codeverse.commands.promote;

import com.codeverse.main.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class MyBlog extends ListenerAdapter {
	String blogLink = "https://beginnerdevdiary.tistory.com/";
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
		if (e.getName().equals("promote")) {
			if (e.getSubcommandName().equals("my_blog")) {
				EmbedBuilder eb = new EmbedBuilder();
				eb.setTitle("**My Blog**", blogLink);
				eb.setAuthor(e.getUser().getAsTag(), blogLink, e.getUser().getAvatarUrl());
				eb.setFooter("Made by Codeverse", Bot.getDeveloperIcon());
				eb.setTimestamp(e.getTimeCreated());
				eb.setColor(0x00ff00);
				eb.setDescription("My blog link: " + blogLink);
				
				Button button = Button.link(blogLink, "\uD83D\uDDD2️ Blog Link");
				
				e.replyEmbeds(eb.build()).setActionRow(button).queue();
			}
		}
	}
}
