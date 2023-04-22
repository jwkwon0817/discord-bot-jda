package com.codeverse.listeners.user.dm_checker;

import com.codeverse.main.Bot;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DmChecker extends ListenerAdapter {
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		
		String message = e.getMessage().getContentRaw();
		User user = e.getJDA().getUserById(Bot.getOwnerId());
		
		if (e.getMessage().getAttachments().size() > 0) {
			List<Message.Attachment> attachments = e.getMessage().getAttachments();
			List<FileUpload> files = new ArrayList<>();
			
			for (Message.Attachment attachment : attachments) {
				InputStream attachmentStream = attachment.retrieveInputStream().join();
				try {
					FileUpload upload = FileUpload.fromData(IOUtils.toByteArray(attachmentStream), attachment.getFileName());
					files.add(upload);
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}
			
			user.openPrivateChannel().queue((channel) -> {
				channel.sendMessage(message).addFiles(files).queue();
			});
		} else {
			user.openPrivateChannel().queue((channel) -> {
				channel.sendMessage(message).addFiles().queue();
			});
		}
	}
}
