package com.codeverse.commands.message.disassemble;

import com.github.kimkevin.hangulparser.HangulParserException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.List;

import static com.github.kimkevin.hangulparser.HangulParser.disassemble;

public class Disassemble extends ListenerAdapter {
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
		if (e.getName().equals("message")) {
			if (e.getSubcommandName().equals("disassemble")) {
				String text = e.getOption("message").getAsString();
				
				try {
					List<String> words = disassemble(text);
					
					String word = words.toString().replace("[", "").replace("]", "").replace(", ", "");
					
					EmbedBuilder eb = new EmbedBuilder();
					eb.setTitle("**Disassembled Message**");
					eb.setDescription("Origin message: `" + text + "`");
					eb.setColor(Color.CYAN);
					eb.setAuthor(e.getUser().getAsTag(), null, e.getUser().getAvatarUrl());
					eb.setFooter("Made by Codeverse", e.getJDA().getSelfUser().getAvatarUrl());
					eb.addField("Disassembled message:", "`" + word + "`", false);
					
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
