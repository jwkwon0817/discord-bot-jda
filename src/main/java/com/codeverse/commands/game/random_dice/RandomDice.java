package com.codeverse.commands.game.random_dice;

import com.codeverse.commands.utils.ping.Ping;
import com.codeverse.main.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;

import java.awt.*;

public class RandomDice extends ListenerAdapter {
	String[] diceId = {"1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣"};
	
	Logger logger = (Logger) LoggerFactory.getLogger(Ping.class);
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
		if (e.getName().equals("game")) {
			if (e.getSubcommandName().equals("random_dice")) {
				int playerDice = (int) (Math.random() * 6) + 1;
				int botDice = (int) (Math.random() * 6) + 1;
				
				EmbedBuilder eb = new EmbedBuilder();
				eb.setTitle("**Random Dice**");
				eb.clearFields();
				eb.addField("You", diceId[playerDice - 1], true);
				eb.addField("Me", diceId[botDice - 1], true);
				eb.setAuthor(e.getUser().getAsTag(), null, e.getUser().getAvatarUrl());
				eb.setFooter("Made by Codeverse", Bot.getDeveloperIcon());
				eb.setDescription(e.getUser().getAsMention() + " This is your dice!");
				eb.setTimestamp(e.getTimeCreated());
				if (playerDice > botDice) {
					eb.setColor(Color.GREEN);
					eb.addField("Result", "You won!", true);
					e.replyEmbeds(eb.build()).queue();
					logger.info("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ") ] Dice command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") [ Player won (" + playerDice + ":" + botDice + ") ]");
					Bot.updateLog("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ") ] Dice command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") [ Player won (" + playerDice + ":" + botDice + ") ]", "info");
				} else if (playerDice < botDice) {
					eb.setColor(Color.RED);
					eb.addField("Result", "You lost!", true);
					e.replyEmbeds(eb.build()).queue();
					logger.info("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ") ] Dice command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") [ Player lost (" + botDice + ":" + playerDice + ") ]");
					Bot.updateLog("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ") ] Dice command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") [ Player lost (" + botDice + ":" + playerDice + ") ]", "info");
				} else {
					Button button = Button.primary("roll-again", "🎲 Roll Again");
					eb.setColor(Color.YELLOW);
					eb.addField("Result", "Draw!", true);
					e.replyEmbeds(eb.build()).addActionRow(button).queue();
					logger.info("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ") ] Dice command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") [ Draw (" + playerDice + ":" + botDice + ") ]");
					Bot.updateLog("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ") ] Dice command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") [ Draw (" + playerDice + ":" + botDice + ") ]", "info");
				}
			}
		}
	}
	
	@Override
	public void onButtonInteraction(ButtonInteractionEvent e) {
		if (e.getComponentId().equals("roll-again")) {
			String userId = e.getMessage().getEmbeds().get(0).getDescription().split(" ")[0].replace("<@", "").replace(">", "");
			if (!e.getUser().getId().equals(userId)) {
				EmbedBuilder eb = new EmbedBuilder();
				eb.setTitle("**Random Dice**");
				eb.clearFields();
				eb.setAuthor(e.getUser().getAsTag(), null, e.getUser().getAvatarUrl());
				eb.setFooter("Made by Codeverse", Bot.getDeveloperIcon());
				eb.setDescription(e.getUser().getAsMention() + " You can't roll other people's dice!");
				eb.setTimestamp(e.getTimeCreated());
				eb.setColor(Color.RED);
				e.replyEmbeds(eb.build()).setEphemeral(true).queue();
				return;
			}
			
			int playerDice = (int) (Math.random() * 6) + 1;
			int botDice = (int) (Math.random() * 6) + 1;
			
			logger.info("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ") ] Dice command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") [ Roll Again ]");
			Bot.updateLog("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ") ] Dice command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") [ Roll Again ]", "info");
			
			EmbedBuilder eb = new EmbedBuilder();
			eb.clearFields();
			eb.addField("You", diceId[playerDice - 1], true);
			eb.addField("Me", diceId[botDice - 1], true);
			eb.setAuthor(e.getUser().getAsTag(), null, e.getUser().getAvatarUrl());
			eb.setFooter("Made by Codeverse", Bot.getDeveloperIcon());
			eb.setDescription(e.getUser().getAsMention() + " This is your dice!");
			eb.setTimestamp(e.getTimeCreated());
			if (playerDice > botDice) {
				eb.setColor(Color.GREEN);
				eb.addField("Result", "You won!", true);
				e.replyEmbeds(eb.build()).queue();
				logger.info("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ") ] Dice command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") [ Player won (" + playerDice + ":" + botDice + ") ] [ Roll Again ]");
				Bot.updateLog("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ") ] Dice command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") [ Player won (" + playerDice + ":" + botDice + ") ] [ Roll Again ]", "info");
			} else if (playerDice < botDice) {
				eb.setColor(Color.RED);
				eb.addField("Result", "You lost!", true);
				e.replyEmbeds(eb.build()).queue();
				logger.info("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ") ] Dice command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") [ Player lost (" + botDice + ":" + playerDice + ") ] [ Roll Again ]");
				Bot.updateLog("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ") ] Dice command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") [ Player lost (" + botDice + ":" + playerDice + ") ] [ Roll Again ]", "info");
			} else {
				Button button = Button.primary("roll-again", "🎲 Roll Again");
				eb.setColor(Color.YELLOW);
				eb.addField("Result", "Draw!", true);
				e.replyEmbeds(eb.build()).addActionRow(button).queue();
				logger.info("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ") ] Dice command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") [ Draw (" + playerDice + ":" + botDice + ") ] [ Roll Again ]");
				Bot.updateLog("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ") ] Dice command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") [ Draw (" + playerDice + ":" + botDice + ") ] [ Roll Again ]", "info");
			}
			Button button = Button.danger("roll-again", "🎲 Rolled Again").asDisabled();
			e.getMessage().editMessageEmbeds(e.getMessage().getEmbeds()).setActionRow(button).queue();
		}
	}
}

