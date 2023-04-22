package com.codeverse.commands.search.cat;

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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Cat extends ListenerAdapter {
	Logger logger = (Logger) LoggerFactory.getLogger(Cat.class);
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
		if (e.getName().equals("search")) {
			if (e.getSubcommandName().equals("cat")) {
				try {
					// TheCatAPI 엔드포인트 URL 생성
					URL url = new URL("https://api.thecatapi.com/v1/images/search");
					
					// HTTP GET 요청 설정
					HttpURLConnection con = (HttpURLConnection) url.openConnection();
					con.setRequestMethod("GET");
					con.setRequestProperty("x-api-key", Bot.getAPIKey("cat"));
					
					// 응답 읽기
					Scanner scanner = new Scanner(con.getInputStream());
					StringBuilder response = new StringBuilder();
					while (scanner.hasNextLine()) {
						response.append(scanner.nextLine());
					}
					scanner.close();
					
					
					JSONParser parser = new JSONParser();
					JSONArray jsonArray = (JSONArray) parser.parse(response.toString());
					JSONObject jsonObject = (JSONObject) jsonArray.get(0);
					String imageUrl = (String) jsonObject.get("url");
					
					// Embed 객체 생성
					EmbedBuilder eb = new EmbedBuilder();
					eb.setTitle("**Here is your cat image**");
					eb.setImage(imageUrl);
					eb.setFooter("Made by Codeverse", Bot.getDeveloperIcon());
					eb.setTimestamp(e.getTimeCreated());
					eb.setColor(Color.CYAN);
					
					e.replyEmbeds(eb.build()).queue();
					
					String log = "[ " + e.getGuild().getName() + " ] " + e.getMember().getUser().getAsTag() + " (" + e.getMember().getId() + ") " + "used /cat command";
					logger.info(log);
					Bot.updateLog(log, "info");
				} catch (IOException ex) {
					String log = "[ " + e.getGuild().getName() + " ] " + e.getMember().getUser().getAsTag() + " (" + e.getMember().getId() + ") " + "used /cat command";
					logger.error(log, ex);
					Bot.updateLog(log, "error");
				} catch (Exception ex) {
					EmbedBuilder eb = new EmbedBuilder();
					eb.setTitle("**Error**");
					eb.setDescription("An error occurred while executing the command. Please try again later.");
					eb.setFooter("Made by Codeverse", Bot.getDeveloperIcon());
					eb.setTimestamp(e.getTimeCreated());
					eb.setColor(Color.RED);
					
					e.replyEmbeds(eb.build()).setEphemeral(true).queue();
					
					String log = "[ " + e.getGuild().getName() + " ] " + e.getMember().getUser().getAsTag() + " (" + e.getMember().getId() + ") " + "used /cat command";
					logger.error(log, ex);
					Bot.updateLog(log, "error");
				}
			}
		}
	}
}
