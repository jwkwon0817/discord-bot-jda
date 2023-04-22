package com.codeverse.commands.utils.hanriver_temperature;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.TimeFormat;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class HanriverTemperature extends ListenerAdapter {
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
		if (e.getName().equals("utils")) {
			if (e.getSubcommandName().equals("hanriver_temperature")) {
				try {
					String apiUrl = "http://hangang.dkserver.wo.tc";
					
					URL url = new URL(apiUrl);
					HttpURLConnection con = (HttpURLConnection) url.openConnection();
					con.setRequestMethod("GET");
					
					int responseCode = con.getResponseCode();
					
					if (responseCode == HttpURLConnection.HTTP_OK) {
						BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
						String inputLine;
						StringBuffer response = new StringBuffer();
						
						while ((inputLine = in.readLine()) != null) {
							response.append(inputLine);
						}
						
						in.close();
						
						JSONParser parser = new JSONParser();
						JSONObject jsonObject = (JSONObject) parser.parse(response.toString());
						
						String temperature = jsonObject.get("temp").toString();
						String time = jsonObject.get("time").toString();
						
						EmbedBuilder eb = new EmbedBuilder();
						eb.setTitle("**Hanriver Temperature**");
						eb.setDescription("The temperature of Hanriver is " + temperature + "℃");
						eb.addField("Time", time, false);
						eb.setColor(Color.CYAN);
						eb.setAuthor(e.getUser().getAsTag(), null, e.getUser().getAvatarUrl());
						eb.setFooter("Made by Codeverse", e.getJDA().getSelfUser().getAvatarUrl());
						
						e.replyEmbeds(eb.build()).queue();
					}
				} catch (Exception ex) {
					EmbedBuilder eb = new EmbedBuilder();
					eb.setTitle("**Error**");
					eb.setDescription("Something went wrong!");
					eb.setColor(Color.RED);
					eb.setAuthor(e.getUser().getAsTag(), null, e.getUser().getAvatarUrl());
					eb.setFooter("Made by Codeverse", e.getJDA().getSelfUser().getAvatarUrl());
					eb.addField("Error:", "```bash\n" + ex + "```", false);
					
					e.replyEmbeds(eb.build()).setEphemeral(true).queue();
				}
			}
		}
	}
}
