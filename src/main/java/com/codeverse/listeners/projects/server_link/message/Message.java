package com.codeverse.listeners.projects.server_link.message;

import ch.qos.logback.classic.Logger;
import com.codeverse.main.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.FileUpload;
import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class Message extends ListenerAdapter {
	final String guildA = "1073102522769735730";
	final String guildB = "988101399407648838";
	final String channelA = "1085227532661567558";
	final String channelB = "1085171809860730890";
	
	Logger logger = (Logger) LoggerFactory.getLogger(Message.class);
	
	List<String> blackList = new ArrayList<>(List.of(new String[]{
			"881173339236868206"
	}));
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		User user = e.getAuthor();
		
		// Check user is bot
		if (user.isBot()) return;
		
		if (blackList.contains(user.getId())) {
			e.getMessage().delete();
			logger.warn("[ " + e.getGuild().getName() + "( " + e.getGuild().getId() + " )" + " ]" + "Linker command executed by " + user.getAsTag() + "( " + user.getId() + " )" + "but failed because of blacklisted user.");
			Bot.updateLog("[ " + e.getGuild().getName() + "( " + e.getGuild().getId() + " )" + " ]" + "Linker command executed by " + user.getAsTag() + "( " + user.getId() + " )" + "but failed because of blacklisted user.", "warn");
			return;
		}
		
		// Answer embeds
		EmbedBuilder eb = new EmbedBuilder();
		eb.setAuthor(user.getAsTag(), null, user.getAvatarUrl());
		eb.setThumbnail(e.getGuild().getIconUrl());
		eb.setDescription(e.getMessage().getContentRaw());
		eb.setTitle("**" + e.getMember().getUser().getId() + " 's question**");
		eb.setFooter(e.getGuild().getName(), e.getGuild().getIconUrl());
		eb.setColor(Color.CYAN);
		eb.setTimestamp(e.getMessage().getTimeCreated());
		
		// Buttons
		Button answerButton = Button.primary("answer", "Answer");
		Button solvedButton = Button.success("solved", "Mark as resolved");
		Button deleteButton = Button.danger("delete", "Delete");
		
		if (e.getMessage().getContentRaw().startsWith("!admin ") && (e.getAuthor().getId().equals("710448716804522106"))) return;
		
		try {
			if (e.getChannel().getId().equals(channelA)) {
				e.getMessage().delete().queue();
				if (e.getMessage().getAttachments().isEmpty()) {
					e.getJDA().getGuildById(guildB).getTextChannelById(channelB).sendMessageEmbeds(eb.build()).setActionRow(answerButton).queue(message -> {
						eb.addField("ID", message.getId(), false);
						e.getJDA().getGuildById(guildA).getTextChannelById(channelA).sendMessageEmbeds(eb.build()).setActionRow(answerButton, solvedButton, deleteButton).queue(msg -> {
							eb.clearFields();
							eb.addField("ID", msg.getId(), false);
							message.editMessageEmbeds(eb.build()).queue();
						});
					});
					
					logger.info("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() +  ")" + " ] " + e.getMember().getUser().getAsTag() + " (" + e.getMember().getUser().getId() + ") " +  " sent a message with the content: " + e.getMessage().getContentRaw());
					Bot.updateLog("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() +  ")" + " ] " + e.getMember().getUser().getAsTag() + " (" + e.getMember().getUser().getId() + ") " +  " sent a message with the content: " + e.getMessage().getContentRaw(), "info");
				} else {
					List<net.dv8tion.jda.api.entities.Message.Attachment> attachments = e.getMessage().getAttachments();
					List<FileUpload> files = new ArrayList<>();
					
					for (net.dv8tion.jda.api.entities.Message.Attachment attachment : attachments) {
						InputStream attachmentStream = attachment.retrieveInputStream().join();
						try {
							FileUpload upload = FileUpload.fromData(IOUtils.toByteArray(attachmentStream), attachment.getFileName());
							files.add(upload);
						} catch (IOException ex) {
							throw new RuntimeException(ex);
						}
					}
					
					MessageCreateAction messageAction = e.getJDA().getGuildById(guildB).getTextChannelById(channelB).sendMessageEmbeds(eb.build()).addFiles(files).setActionRow(answerButton);
					MessageCreateAction finalMessageAction = e.getJDA().getGuildById(guildA).getTextChannelById(channelA).sendMessageEmbeds(eb.build()).addFiles(files).setActionRow(answerButton, solvedButton, deleteButton);
					
					messageAction.queue(message -> {
						finalMessageAction.queue(msg -> {
							eb.addField("ID", message.getId(), false);
							msg.editMessageEmbeds(eb.build()).queue();
							eb.clearFields();
							eb.addField("ID", msg.getId(), false);
							message.editMessageEmbeds(eb.build()).queue();
						});
					});
					
					logger.info("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() +  ")" + " ] " + e.getMember().getUser().getAsTag() + " (" + e.getMember().getUser().getId() + ") " +  " sent a message with the content: " + e.getMessage().getContentRaw() + " and " + attachments.size() + " attachments.");
					Bot.updateLog("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() +  ")" + " ] " + e.getMember().getUser().getAsTag() + " (" + e.getMember().getUser().getId() + ") " +  " sent a message with the content: " + e.getMessage().getContentRaw() + " and " + attachments.size() + " attachments.", "info");
				}
			} else if (e.getChannel().getId().equals(channelB)) {
				e.getMessage().delete().queue();
				if (e.getMessage().getAttachments().isEmpty()) {
					e.getJDA().getGuildById(guildA).getTextChannelById(channelA).sendMessageEmbeds(eb.build()).setActionRow(answerButton).queue(message -> {
						eb.addField("ID", message.getId(), false);
						e.getJDA().getGuildById(guildB).getTextChannelById(channelB).sendMessageEmbeds(eb.build()).setActionRow(answerButton, solvedButton, deleteButton).queue(msg -> {
							eb.clearFields();
							eb.addField("ID", msg.getId(), false);
							message.editMessageEmbeds(eb.build()).queue();
						});
					});
					
					logger.info("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() +  ")" + " ] " + e.getMember().getUser().getAsTag() + " (" + e.getMember().getUser().getId() + ") " +  " sent a message with the content: " + e.getMessage().getContentRaw());
					Bot.updateLog("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() +  ")" + " ] " + e.getMember().getUser().getAsTag() + " (" + e.getMember().getUser().getId() + ") " +  " sent a message with the content: " + e.getMessage().getContentRaw(), "info");
				} else {
					List<net.dv8tion.jda.api.entities.Message.Attachment> attachments = e.getMessage().getAttachments();
					List<FileUpload> files = new ArrayList<>();
					
					for (net.dv8tion.jda.api.entities.Message.Attachment attachment : attachments) {
						InputStream attachmentStream = attachment.retrieveInputStream().join();
						try {
							FileUpload upload = FileUpload.fromData(IOUtils.toByteArray(attachmentStream), attachment.getFileName());
							files.add(upload);
						} catch (IOException ex) {
							throw new RuntimeException(ex);
						}
					}
					
					MessageCreateAction messageAction = e.getJDA().getGuildById(guildA).getTextChannelById(channelA).sendMessageEmbeds(eb.build()).addFiles(files).setActionRow(answerButton);
					MessageCreateAction finalMessageAction = e.getJDA().getGuildById(guildB).getTextChannelById(channelB).sendMessageEmbeds(eb.build()).addFiles(files).setActionRow(answerButton, solvedButton, deleteButton);
					
					messageAction.queue(message -> {
						finalMessageAction.queue(msg -> {
							eb.addField("ID", message.getId(), false);
							msg.editMessageEmbeds(eb.build()).queue();
							eb.clearFields();
							eb.addField("ID", msg.getId(), false);
							message.editMessageEmbeds(eb.build()).queue();
						});
					});
					
					logger.info("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() +  ")" + " ] " + e.getMember().getUser().getAsTag() + " (" + e.getMember().getUser().getId() + ") " +  " sent a message with the content: " + e.getMessage().getContentRaw() + " and " + attachments.size() + " attachments.");
					Bot.updateLog("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() +  ")" + " ] " + e.getMember().getUser().getAsTag() + " (" + e.getMember().getUser().getId() + ") " +  " sent a message with the content: " + e.getMessage().getContentRaw() + " and " + attachments.size() + " attachments.", "info");
				}
			}
		} catch (InsufficientPermissionException ex) {
			User owner = e.getGuild().getOwner().getUser();
			owner.openPrivateChannel().queue(channel -> {
				EmbedBuilder eb2 = new EmbedBuilder();
				eb2.setTitle("**I received the question, but I do not have permission to manage the message, so I was unable to delete it.**");
				eb2.setDescription("Please reset the permission.");
				eb2.addField("Question content", e.getMessage().getContentRaw(), false);
				eb2.addField("Questioner", e.getAuthor().getAsTag(), false);
				eb2.addField("Questioner ID", e.getAuthor().getId(), false);
				eb2.addField("Question channel", e.getChannel().getAsMention(), false);
				eb2.addField("Question channel ID", e.getChannel().getId(), false);
				eb2.addField("Question ID", e.getMessageId(), false);
				eb2.setFooter("Question received at");
				eb2.setColor(Color.RED);
				eb2.setTimestamp(e.getMessage().getTimeCreated());
				channel.sendMessage(owner.getAsMention()).addEmbeds(eb2.build()).queue();
			});
			logger.warn("I received the question, but I do not have permission to manage the message, so I was unable to delete it.");
			Bot.updateLog("I received the question, but I do not have permission to manage the message, so I was unable to delete it.", "warn");
		}
	}
	
	@Override
	public void onButtonInteraction(ButtonInteractionEvent e) {
		if (blackList.contains(e.getUser().getId())) {
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("**You are blacklisted.**");
			eb.setDescription("You are blacklisted from using this bot.");
			eb.setColor(Color.RED);
			
			e.replyEmbeds(eb.build()).setEphemeral(true).queue();
			logger.warn("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ")" + " ] " + "Linker " + e.getComponentId() + " button executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") " + "but failed because of blacklisted user.");
			Bot.updateLog("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ")" + " ] " + "Linker " + e.getComponentId() + " button executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") " + "but failed because of blacklisted user.", "warn");
			return;
		}
		
		if (e.getComponentId().equals("answer")) {
			TextInput answerContent = TextInput.create("answer-content", "Please enter your response.", TextInputStyle.PARAGRAPH)
					.setMinLength(1)
					.setRequired(true)
					.build();
			
			Modal answerModal = Modal.create("answer-modal", "Answer")
					.addActionRows(ActionRow.of(answerContent))
					.build();
			
			logger.info("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() +  ")" + " ] " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") " +  " pressed the answer button.");
			Bot.updateLog("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() +  ")" + " ] " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") " +  " pressed the answer button.", "info");
			
			e.replyModal(answerModal).queue();
		} else if (e.getComponentId().equals("solved")) {
			String userId = e.getMessage().getEmbeds().get(0).getTitle().split(" ")[0].replace("**", "");
			if (!userId.equals(e.getUser().getId())) {
				EmbedBuilder error = new EmbedBuilder();
				error.setAuthor(e.getUser().getAsTag(), null, e.getUser().getAvatarUrl());
				error.setTitle("**An error has occurred.**");
				error.setColor(Color.RED);
				error.setTimestamp(e.getMessage().getTimeCreated());
				error.setDescription("You are not the author of the message.");
				e.replyEmbeds(error.build()).setEphemeral(true).queue();
				logger.info("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() +  ")" + " ] " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") " +  " tried to mark a question as resolved but failed because he is not the author of the message.");
				Bot.updateLog("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() +  ")" + " ] " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") " +  " tried to mark a question as resolved but failed because he is not the author of the message.", "info");
				return;
			}
			
			Button solvedButton = Button.primary("solved", "Mark as resolved");
			Button answerButton = Button.primary("answer", "Answer");
			
			e.getMessage().editMessageEmbeds().setEmbeds(e.getMessage().getEmbeds()).setActionRow(solvedButton.asDisabled()).queue();
			String thereId = e.getMessage().getEmbeds().get(0).getFields().get(0).getValue();
			if (e.getChannel().getId().equals(channelA)) {
				e.getJDA().getGuildById(guildB).getTextChannelById(channelB).retrieveMessageById(thereId).queue(message -> {
					message.editMessageEmbeds().setEmbeds(message.getEmbeds()).setActionRow(answerButton.asDisabled()).queue();
				});
				logger.info("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() +  ")" + " ] " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") " +  " solved the question.");
				Bot.updateLog("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() +  ")" + " ] " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") " +  " solved the question.", "info");
			} else if (e.getChannel().getId().equals(channelB)) {
				e.getJDA().getGuildById(guildA).getTextChannelById(channelA).retrieveMessageById(thereId).queue(message -> {
					message.editMessageEmbeds().setEmbeds(message.getEmbeds()).setActionRow(answerButton.asDisabled()).queue();
				});
				logger.info("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() +  ")" + " ] " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") " +  " solved the question.");
				Bot.updateLog("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() +  ")" + " ] " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") " +  " solved the question.", "info");
			}
			
			EmbedBuilder eb = new EmbedBuilder();
			eb.setAuthor(e.getUser().getAsTag(), null, e.getUser().getAvatarUrl());
			eb.setTitle("**Success to solve.**");
			eb.setColor(Color.GREEN);
			eb.setTimestamp(e.getMessage().getTimeCreated());
			eb.setDescription("Successfully marked as solved.");
			e.replyEmbeds(eb.build()).setEphemeral(true).queue();
		} else if (e.getComponentId().equals("delete")) {
			String userId = e.getMessage().getEmbeds().get(0).getTitle().split(" ")[0].replace("**", "");
			if (userId.equals(e.getUser().getId())) {
				String id = e.getMessage().getEmbeds().get(0).getFields().get(0).getValue();
				if (e.getChannel().getId().equals(channelA)) {
					e.getJDA().getGuildById(guildB).getTextChannelById(channelB).retrieveMessageById(id).queue(message -> {
						message.delete().queue();
					});
					logger.info("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() +  ")" + " ] " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") " +  " deleted question.");
					Bot.updateLog("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() +  ")" + " ] " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") " +  " deleted question.", "info");
				} else if (e.getChannel().getId().equals(channelB)) {
					e.getJDA().getGuildById(guildA).getTextChannelById(channelA).retrieveMessageById(id).queue(message -> {
						message.delete().queue();
					});
					logger.info("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() +  ")" + " ] " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") " +  " deleted question.");
					Bot.updateLog("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() +  ")" + " ] " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") " +  " deleted question.", "info");
				}
				
				e.getMessage().delete().queue();
				
				EmbedBuilder eb = new EmbedBuilder();
				eb.setAuthor(e.getUser().getAsTag(), null, e.getUser().getAvatarUrl());
				eb.setTitle("**Success to delete.**");
				eb.setColor(Color.GREEN);
				eb.setTimestamp(e.getMessage().getTimeCreated());
				eb.setDescription("**Successfully deleted.**");
				e.replyEmbeds(eb.build()).setEphemeral(true).queue();
			} else {
				EmbedBuilder error = new EmbedBuilder();
				error.setAuthor(e.getUser().getAsTag(), null, e.getUser().getAvatarUrl());
				error.setTitle("**An error has occurred.**");
				error.setColor(Color.RED);
				error.setTimestamp(e.getMessage().getTimeCreated());
				error.setDescription("You are not the author of this message.");
				e.replyEmbeds(error.build()).setEphemeral(true).queue();
				logger.info("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() +  ")" + " ] " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") " +  " tried to delete a question, but he is not the author of this message.");
				Bot.updateLog("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() +  ")" + " ] " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") " +  " tried to delete a question, but he is not the author of this message.", "info");
			}
		}
	}
	
	@Override
	public void onModalInteraction(ModalInteractionEvent e) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setAuthor(e.getUser().getAsTag(), null, e.getUser().getAvatarUrl());
		eb.setTitle("**Answer**");
		eb.setThumbnail(e.getGuild().getIconUrl());
		eb.setFooter(e.getGuild().getName(), e.getGuild().getIconUrl());
		eb.setColor(Color.CYAN);
		eb.setTimestamp(e.getMessage().getTimeCreated());
		
		EmbedBuilder result = new EmbedBuilder();
		result.setAuthor(e.getUser().getAsTag(), null, e.getUser().getAvatarUrl());
		
		try {
			if (e.getModalId().equals("answer-modal")) {
				eb.setDescription(e.getValue("answer-content").getAsString());
				if (e.getChannel().getId().equals(channelA)) {
					String messageId = e.getMessage().getEmbeds().get(0).getFields().get(0).getValue();
					net.dv8tion.jda.api.entities.Message message = e.getJDA().getGuildById(guildB).getTextChannelById(channelB).retrieveMessageById(messageId).submit().join();
					String userMention = ("<@" + message.getEmbeds().get(0).getTitle().split(" ")[0] + ">").replace("**", "");
					message.reply(userMention).addEmbeds(eb.build()).queue();
					e.getMessage().reply(userMention).addEmbeds(eb.build()).queue();
					logger.info("[ " + e.getGuild().getName() + "  ( " + e.getGuild().getId() +  " )" + " ] " + e.getUser().getAsTag() + " ( " + e.getUser().getId() + ") " +  " has responded to the question.");
					Bot.updateLog("[ " + e.getGuild().getName() + "  ( " + e.getGuild().getId() +  " )" + " ] " + e.getUser().getAsTag() + " ( " + e.getUser().getId() + ") " +  " has responded to the question.", "info");
				} else if (e.getChannel().getId().equals(channelB)) {
					String messageId = e.getMessage().getEmbeds().get(0).getFields().get(0).getValue();
					net.dv8tion.jda.api.entities.Message message = e.getJDA().getGuildById(guildA).getTextChannelById(channelA).retrieveMessageById(messageId).submit().join();
					String userMention = ("<@" + message.getEmbeds().get(0).getTitle().split(" ")[0] + ">").replace("**", "");
					message.reply(userMention).addEmbeds(eb.build()).queue();
					e.getMessage().reply(userMention).addEmbeds(eb.build()).queue();
					logger.info("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() +  ")" + " ] " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") " +  " has responded to the question.");
					Bot.updateLog("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() +  ")" + " ] " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") " +  " has responded to the question.", "info");
				}
			}
			result.setTitle("**Successfully responded.**");
			result.setColor(Color.GREEN);
			result.setTimestamp(e.getMessage().getTimeCreated());
			result.setDescription("The response has been successfully completed.");
		} catch (NullPointerException exception) {
			result.setTitle("**Failed to respond.**");
			result.setColor(Color.RED);
			result.setTimestamp(e.getMessage().getTimeCreated());
			result.setDescription("An error has occured.\nPlease contact the developer for assistance.\n**Developer:** anddl#7777");
			result.setFooter("Error code: 404");
			logger.error("An error has occured.");
			Bot.updateLog("An error has occured.", "error");
			exception.printStackTrace();
		} catch (Exception exc) {
			logger.error("An error has occured.");
			Bot.updateLog("An error has occured.", "error");
			exc.printStackTrace();
		}
		e.replyEmbeds(result.build()).setEphemeral(true).queue();
	}
}