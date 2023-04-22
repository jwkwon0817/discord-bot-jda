package com.codeverse.commands.game.reaction_rate;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class ReactionRate extends ListenerAdapter {
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
		if (e.getName().equals("game")) {
			if (e.getSubcommandName().equals("reaction_rate")) {
				if (true) {
					e.reply("This command is currently disabled.").setEphemeral(true).queue();
					return;
				}
				
				try {
					int N = 5;
					Button[][] buttons = new Button[N][N];
					
					for (int i = 0; i < N; i++) {
						for (int j = 0; j < N; j++) {
							buttons[i][j] = Button.secondary("reaction_rate_" + i + "_" + j, "Don't click me.").asDisabled();
						}
					}
					
					e.reply("Click green buttons as fast as you can after 5 seconds.!")
							.addActionRow(buttons[0])
							.addActionRow(buttons[1])
							.addActionRow(buttons[2])
							.addActionRow(buttons[3])
							.addActionRow(buttons[4])
							.queue(message -> {
								for (int i = 0; i < N; i++) {
									try {
										Thread.sleep(1000);
										message.editOriginal("Click green buttons as fast as you can after " + (N - i - 1) + " seconds.!")
												.queue();
									} catch (InterruptedException ex) {
										throw new RuntimeException(ex);
									}
								}
								
								boolean isFinished = false;
								int randomX = (int) (Math.random() * N);
								int randomY = (int) (Math.random() * N);
								
								for (int i = 0; i < N; i++) {
									for (int j = 0; j < N; j++) {
										if (i == randomX && j == randomY && !isFinished) {
											buttons[i][j] = Button.success("reaction_rate_" + i + "_" + j, "Click me!");
											isFinished = true;
										} else {
											buttons[i][j] = Button.secondary("reaction_rate_" + i + "_" + j, "Don't click me.");
										}
									}
								}
								
								Button[] result = new Button[N * N];
								for (int i = 0; i < N; i++) {
									System.arraycopy(buttons[i], 0, result, i * N, N);
								}
							});
				} catch (Exception ex) {
					e.reply("An error occurred: " + ex.getMessage()).queue();
				}
			}
		}
	}
}
