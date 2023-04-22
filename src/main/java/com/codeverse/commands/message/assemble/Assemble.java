package com.codeverse.commands.message.assemble;

import com.github.kimkevin.hangulparser.HangulParserException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.Collections;
import java.util.List;

import static com.github.kimkevin.hangulparser.HangulParser.assemble;
import static com.github.kimkevin.hangulparser.HangulParser.disassemble;

public class Assemble extends ListenerAdapter {
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
		if (e.getName().equals("message")) {
			if (e.getSubcommandName().equals("assemble")) {
				String text = e.getOption("message").getAsString();
				
				text = text.replace(" ", "").replace(",", "");
				
				List<String> words = List.of(text.split(""));
				
				try {
					String word = assemble(words);
					
					EmbedBuilder eb = new EmbedBuilder();
					eb.setTitle("**Assembled Message**");
					eb.setDescription("Origin message: `" + text + "`");
					eb.setColor(Color.CYAN);
					eb.setAuthor(e.getUser().getAsTag(), null, e.getUser().getAvatarUrl());
					eb.setFooter("Made by Codeverse", e.getJDA().getSelfUser().getAvatarUrl());
					eb.addField("Assembled message:", "`" + word + "`", false);
					
					e.replyEmbeds(eb.build()).queue();
				} catch (HangulParserException ex) {
					throw new RuntimeException(ex);
				} catch (Exception ex) {
					EmbedBuilder eb = new EmbedBuilder();
					eb.setTitle("**Error**");
					eb.setDescription("Something went wrong!");
					eb.setColor(Color.RED);
					eb.setAuthor(e.getUser().getAsTag(), null, e.getUser().getAvatarUrl());
					eb.setFooter("Made by Codeverse", e.getJDA().getSelfUser().getAvatarUrl());
					
					e.replyEmbeds(eb.build()).setEphemeral(true).queue();
				}
			}
		}
	}
}
