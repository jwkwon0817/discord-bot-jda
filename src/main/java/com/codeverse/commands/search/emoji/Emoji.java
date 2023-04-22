package com.codeverse.commands.search.emoji;

import ch.qos.logback.classic.Logger;
import com.codeverse.main.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Emoji extends ListenerAdapter {
	Logger logger = (Logger) LoggerFactory.getLogger(Emoji.class);
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
		final String API_KEY = Bot.getAPIKey("emoji");
		
		if (e.getName().equals("search")) {
			if (e.getSubcommandName().equals("emoji")) {
				String emojiName = e.getOption("name").getAsString();
				
				try {
					URL url = new URL("https://emoji-api.com/emojis?search=" + emojiName + "&access_key=" + API_KEY);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					
					InputStream inputStream = connection.getInputStream();
					String response = new String(inputStream.readAllBytes(), "UTF-8");
					
					JSONParser parser = new JSONParser();
					
					JSONArray jsonArray = (JSONArray) parser.parse(response);
					if (jsonArray == null) {
						EmbedBuilder embedBuilder = new EmbedBuilder();
						embedBuilder.setTitle("**Cannot found any emoji**");
						embedBuilder.setDescription("Cannot found any emoji with name " + emojiName + "`\n\nIf you used other language, please use English to search.`");
						embedBuilder.setFooter("Made by Codeverse", Bot.getDeveloperIcon());
						embedBuilder.setTimestamp(e.getTimeCreated());
						embedBuilder.setColor(Color.RED);
						e.replyEmbeds(embedBuilder.build()).queue();
						return;
					}
					
					List<String> emojis = new ArrayList<>();
					for (Object o : jsonArray) {
						JSONObject jsonObject = (JSONObject) o;
						emojis.add((String) jsonObject.get("character"));
					}
					
					EmbedBuilder eb = new EmbedBuilder();
					eb.setTitle("**Emoji Search**");
					eb.setDescription("Emoji search result for `" + emojiName + "`");
					eb.setFooter("Made by Codeverse", Bot.getDeveloperIcon());
					eb.setTimestamp(e.getTimeCreated());
					eb.setColor(Color.GREEN);
					eb.addField("Emoji", Arrays.toString(emojis.toArray()).replace("[", "").replace("]", ""), false);
					
					e.replyEmbeds(eb.build()).queue();
				} catch (IOException ex) {
					String log = "[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ") ] Emoji search command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") [ " + emojiName + " ], but failed because of an IOException.";
					logger.error(log);
					Bot.updateLog(log, "error");
				} catch (ParseException ex) {
					String log = "[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ") ] Emoji search command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") [ " + emojiName + " ], but failed because of a ParseException.";
					logger.error(log);
					Bot.updateLog(log, "error");
				}
			}
		}
	}
}
