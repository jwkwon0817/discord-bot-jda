package com.codeverse.commands.draw.black_and_white;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.utils.FileUpload;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BlackAndWhite extends ListenerAdapter {
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
		if (e.getName().equals("draw")) {
			if (e.getSubcommandName().equals("black_and_white")) {
				Message.Attachment attachment = e.getOption("image").getAsAttachment();
				
				// file size to mb
				double fileSize = attachment.getSize() / 1024.0 / 1024.0;
				if (fileSize > 64) {
					EmbedBuilder eb = new EmbedBuilder();
					eb.clear();
					
					eb.setTitle("**Error**");
					eb.setDescription("Error: File size too large");
					eb.addField("File size", String.format("%.2f", fileSize) + " MB", false);
					eb.addField("Limited file size", "64 MB", false);
					eb.setColor(Color.RED);
					e.replyEmbeds(eb.build()).setEphemeral(true).queue();
				}
				
				EmbedBuilder eb = new EmbedBuilder();
				eb.clear();
				
				eb.setTitle("**Black and white image**");
				eb.setColor(Color.YELLOW);
				eb.setDescription("Processing...");
				
				String fileName = attachment.getFileName();
				String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
				if (fileType.equals("jpg") || fileType.equals("jpeg") || fileType.equals("png")) {
					try {
						URL url = new URL(attachment.getUrl());
						BufferedImage image = ImageIO.read(url);
						// 이미지 처리 로직
						BufferedImage filteredImage = grayscale(image); // 흑백 필터 적용
						
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						ImageIO.write(filteredImage, fileType, baos);
						byte[] bytes = baos.toByteArray();
						InputStream is = new ByteArrayInputStream(bytes);
						
						FileUpload fileUpload = FileUpload.fromData(is, fileName + "." + fileType);
						
						e.replyFiles(fileUpload).queue();
					} catch (IOException ex) {
						eb.clear();
						
						eb.setTitle("**Error**");
						eb.setDescription("Error: " + ex.getMessage());
						eb.setColor(Color.RED);
						eb.addField("Error", ex.toString(), false);
						e.replyEmbeds(eb.build()).setEphemeral(true).queue();
					}
				} else {
					eb.clear();
					
					eb.setTitle("**Error**");
					eb.setDescription("Error: Invalid file type");
					eb.addField("File type", fileType, false);
					eb.addField("Supported file types", "jpg, jpeg, png", false);
					eb.setColor(Color.RED);
					e.replyEmbeds(eb.build()).setEphemeral(true).queue();
				}
			}
		}
	}
	
	private BufferedImage grayscale(BufferedImage image) {
		BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = result.createGraphics();
		graphics.drawImage(image, 0, 0, Color.WHITE, null);
		ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
		op.filter(image, result);
		return result;
	}
}
