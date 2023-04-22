package com.codeverse.commands.game.rock_scissor_paper;

import ch.qos.logback.classic.Logger;
import com.codeverse.main.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RockScissorsPaper extends ListenerAdapter {
	Logger logger = (Logger) LoggerFactory.getLogger(RockScissorsPaper.class);
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
		if (e.getName().equals("game")) {
			if (e.getSubcommandName().equals("rock_scissors_paper")) {
				EmbedBuilder eb = new EmbedBuilder();
				eb.setTitle("**Rock, Paper, Scissors**");
				eb.setDescription(e.getUser().getAsMention() + " Choose one of your options: rock, scissors or paper");
				eb.setColor(Color.CYAN);
				Button rock = Button.success("rock", Emoji.fromUnicode("✊"));
				Button scissors = Button.danger("scissors", Emoji.fromUnicode("✌"));
				Button paper = Button.primary("paper", Emoji.fromUnicode("🖐"));
				List buttons = Arrays.asList(rock, scissors, paper);
				Collections.shuffle(buttons);
				e.replyEmbeds(eb.build()).setActionRow(buttons).queue();
			}
		}
	}
	
	@Override
	public void onButtonInteraction(ButtonInteractionEvent e) {
		if (!(e.getComponentId().equals("rock") || e.getComponentId().equals("scissors") || e.getComponentId().equals("paper"))) return;
		
		String userId = e.getMessage().getEmbeds().get(0).getDescription().split(" ")[0].replace("<@", "").replace(">", "");
		if (!e.getUser().getId().equals(userId)) {
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("**Rock, Paper, Scissors**");
			eb.setDescription("You can't choose an option for someone else!");
			eb.setColor(Color.RED);
			eb.setFooter("Made by Codeverse", Bot.getDeveloperIcon());
			eb.setTimestamp(e.getTimeCreated());
			e.replyEmbeds(eb.build()).setEphemeral(true).queue();
			return;
		}
		
		String userChoice = e.getComponentId();
		
		String[] choices = {"rock", "scissors", "paper"};
		Map<String, String> emojis = Map.of("rock", "✊", "scissors", "✌", "paper", "🖐");
		String botChoice = choices[(int) (Math.random() * choices.length)];
		
		String result = "";
		String logMessage = "";
		if (userChoice.equals(botChoice)) {
			result = "draw";
			logMessage = "[ " + e.getGuild().getName() + "(" + e.getGuild().getId() + ") ] Rock-Scissor-Paper command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") " + " played rock, paper, scissors and got a draw! (" + userChoice + ", " + botChoice + ")";
		} else if (userChoice.equals("rock") && botChoice.equals("scissors")) {
			result = "win";
			logMessage = "[ " + e.getGuild().getName() + "(" + e.getGuild().getId() + ") ] Rock-Scissor-Paper command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") " + " played rock, paper, scissors and won! (" + userChoice + ", " + botChoice + ")";
		} else if (userChoice.equals("rock") && botChoice.equals("paper")) {
			result = "lose";
			logMessage = "[ " + e.getGuild().getName() + "(" + e.getGuild().getId() + ") ] Rock-Scissor-Paper command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") " + " played rock, paper, scissors and lost! (" + botChoice + ", " + userChoice + ")";
		} else if (userChoice.equals("scissors") && botChoice.equals("rock")) {
			result = "lose";
			logMessage = "[ " + e.getGuild().getName() + "(" + e.getGuild().getId() + ") ] Rock-Scissor-Paper command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") " + " played rock, paper, scissors and lost! (" + botChoice + ", " + userChoice + ")";
		} else if (userChoice.equals("scissors") && botChoice.equals("paper")) {
			result = "win";
			logMessage = "[ " + e.getGuild().getName() + "(" + e.getGuild().getId() + ") ] Rock-Scissor-Paper command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") " + " played rock, paper, scissors and won! (" + userChoice + ", " + botChoice + ")";
		} else if (userChoice.equals("paper") && botChoice.equals("rock")) {
			result = "win";
			logMessage = "[ " + e.getGuild().getName() + "(" + e.getGuild().getId() + ") ] Rock-Scissor-Paper command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") " + " played rock, paper, scissors and won! (" + userChoice + ", " + botChoice + ")";
		} else if (userChoice.equals("paper") && botChoice.equals("scissors")) {
			result = "lose";
			logMessage = "[ " + e.getGuild().getName() + "(" + e.getGuild().getId() + ") ] Rock-Scissor-Paper command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") " + " played rock, paper, scissors and lost! (" + botChoice + ", " + userChoice + ")";
		}
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setAuthor(e.getUser().getAsTag(), null, e.getUser().getAvatarUrl());
		eb.setTitle("**Rock, Paper, Scissors**");
		eb.addField("**You**", emojis.get(userChoice), true);
		eb.addField("**Bot**", emojis.get(botChoice), true);
		eb.setFooter("Made by Codeverse", Bot.getDeveloperIcon());
		eb.setTimestamp(e.getTimeCreated());
		
		switch (result) {
			case "win":
				eb.setDescription("You won!");
				eb.setColor(Color.GREEN);
				break;
			case "lose":
				eb.setDescription("You lost!");
				eb.setColor(Color.RED);
				break;
			case "draw":
				eb.setDescription("It's a draw!");
				eb.setColor(Color.YELLOW);
				break;
		}
		
		e.replyEmbeds(eb.build()).queue();
		Bot.updateLog(logMessage, "info");
		logger.info(logMessage);
	}
}
