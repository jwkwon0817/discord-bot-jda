package com.codeverse.commands.utils.image.emoji;

import ch.qos.logback.classic.Logger;
import com.codeverse.main.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmojiToImage extends ListenerAdapter {
	Logger logger = (Logger) org.slf4j.LoggerFactory.getLogger(EmojiToImage.class);
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		if (e.getGuild().getId().equals("988101399407648838")) return;
		
		Message message = e.getMessage();
		String messageRaw = message.getContentRaw().replace(" ", "");
		
		String imageUrl = "";
		
		//if (!e.getAuthor().getId().equals("710448716804522106")) return;
		
		try {
			int count = 0;
			Pattern pattern = Pattern.compile(":?(:\\w+):?");
			Matcher matcher = pattern.matcher(messageRaw);
			while (matcher.find()) {
				count++;
			}
			
			boolean isFormatCorrect = messageRaw.matches("<:[^:]+:[0-9]+>");
			boolean isGif = messageRaw.matches("<a:[^:]+:[0-9]+>");
			boolean isCorrect = messageRaw.matches("^:.:$\n");
			if (!isFormatCorrect && !isCorrect && !isGif) return;
			
			if (e.getMessage().getAttachments().size() > 0) return;
			
			if (count == 1) {
				String[] emojiDecomposition = messageRaw.split(":");
				
				try {
					if (messageRaw.contains("<a") && messageRaw.contains(">")) {
						imageUrl = "https://cdn.discordapp.com/emojis/" + emojiDecomposition[2].replace(">", "") + ".gif";
					} else {
						imageUrl = "https://cdn.discordapp.com/emojis/" + emojiDecomposition[2].replace(">", "") + ".png";
					}
				} catch (Exception ex) {
					return;
				}
				
				EmbedBuilder eb = new EmbedBuilder();
				eb.setAuthor(e.getAuthor().getAsTag(), null, e.getAuthor().getAvatarUrl());
				eb.setImage(imageUrl);
				eb.setColor(Color.CYAN);
				
				e.getMessage().delete().queue();
				e.getChannel().sendMessageEmbeds(eb.build()).queue();
				
				String log = "EmojiToImage command executed by " + e.getAuthor().getAsTag() + " (" + e.getAuthor().getId() + ") in " + e.getGuild().getName() + " (" + e.getGuild().getId() + ")";
				logger.info(log);
				Bot.updateLog(log, "info");
			}
		} catch (Exception ex) {
			String log = "EmojiToImage command executed by " + e.getAuthor().getAsTag() + " (" + e.getAuthor().getId() + ") in " + e.getGuild().getName() + " (" + e.getGuild().getId() + ") failed" + ex.getMessage();
			logger.error(log);
			Bot.updateLog(log, "error");
		}
	}
}
