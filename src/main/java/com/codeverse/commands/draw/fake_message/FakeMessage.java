package com.codeverse.commands.draw.fake_message;

import ch.qos.logback.classic.Logger;
import com.codeverse.main.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class FakeMessage extends ListenerAdapter {
	Logger logger = (Logger) LoggerFactory.getLogger(FakeMessage.class);
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
		if (e.getName().equals("draw")) {
			if (e.getSubcommandName().equals("fake_message")) {
				if (e.getGuild().getId().equals("988101399407648838")) {
					EmbedBuilder eb = new EmbedBuilder();
					eb.setTitle("Fake message");
					eb.setDescription("This command is disabled in this server.");
					eb.setColor(Color.RED);
					eb.setAuthor(e.getUser().getAsTag(), null, e.getUser().getAvatarUrl());
					eb.setFooter("Made by Codeverse", Bot.getDeveloperIcon());
					eb.setTimestamp(e.getTimeCreated());
					e.replyEmbeds(eb.build()).setEphemeral(true).queue();
				}
				
				User user = e.getOption("user").getAsUser();
				String text = e.getOption("text").getAsString();
				URL url = null;
				try {
					if (user.getAvatarUrl() != null) {
						url = new URL(user.getAvatarUrl());
					} else {
						url = new URL(user.getDefaultAvatarUrl());
					}
				} catch (MalformedURLException ex) {
					throw new RuntimeException(ex);
				}
				BufferedImage icon;
				try {
					icon = ImageIO.read(url);
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
				
				
				BufferedImage image = null;
				try {
					image = draw(icon, user, text, user.isBot());
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
				List<FileUpload> files = new ArrayList<>();
				FileUpload upload = FileUpload.fromData(getImageInputStream(image), "image.png");
				files.add(upload);
				e.replyFiles(files).queue();
				logger.info("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ") ] " + "Image command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") [ " + user.getAsTag() + " (" + user.getId() + "), " + text + " ]");
				Bot.updateLog("[ " + e.getGuild().getName() + " (" + e.getGuild().getId() + ") ] " + "Image command executed by " + e.getUser().getAsTag() + " (" + e.getUser().getId() + ") [ " + user.getAsTag() + " (" + user.getId() + "), " + text + " ]", "info");
			}
		}
	}
	public BufferedImage draw(BufferedImage icon, User user, String text, boolean isBot) throws IOException {
		String[] divided = divide20(text);
		int contentWidth = icon.createGraphics().getFontMetrics().stringWidth(divided[0]);
		int contentLength = divided.length;
		
		AffineTransform transform = AffineTransform.getScaleInstance(1, 1);
		
		int width = 128;
		int height = 128;
		
		int margin = 10;
		
		// 원형 마스크 이미지 생성하기
		BufferedImage mask = new BufferedImage(icon.getWidth(), icon.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = mask.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setTransform(transform);
		g2d.fill(new Ellipse2D.Float(0, 0, mask.getWidth(), mask.getHeight()));
		g2d.dispose();
		
		// 원형 마스크 이미지와 원본 이미지 합성하기
		BufferedImage masked = new BufferedImage(icon.getWidth(), icon.getHeight(), BufferedImage.TYPE_INT_ARGB);
		g2d = masked.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setTransform(transform);
		g2d.drawImage(icon, 0, 0, null);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_IN));
		g2d.drawImage(mask, 0, 0, null);
		g2d.dispose();
		
		// 원형으로 크롭하기
		int diameter = Math.min(masked.getWidth(), masked.getHeight());
		BufferedImage cropped = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
		g2d = cropped.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setTransform(transform);
		g2d.clip(new Ellipse2D.Float(0, 0, diameter, diameter));
		int x = (masked.getWidth() - diameter) / 2;
		int y = (masked.getHeight() - diameter) / 2;
		g2d.drawImage(masked, -x, -y, null);
		g2d.dispose();
		
		int textWidth = icon.createGraphics().getFontMetrics().stringWidth(user.getName());
		int nameLength = user.getName().length();
		int resultX = Math.max(nameLength, (textWidth + width)) + width * 2;
		BufferedImage image = new BufferedImage(resultX + margin + 60 + contentWidth + 30, height + margin + (contentLength * 30), BufferedImage.TYPE_INT_RGB);
		g2d = image.createGraphics();
		
		g2d.setColor(Color.decode("#313338"));
		
		
		g2d.fillRect(0, 0, resultX + margin + 60 + contentWidth + 30, height + margin + (contentLength * 30));
		
		g2d.drawImage(cropped, margin, margin, width, height, null);
		
		g2d.setColor(Color.WHITE);
		g2d.setFont(new Font("gg sans", Font.BOLD, 40));
		
		g2d.drawString(user.getName(), width + 20 + margin, 40 + margin);
		if (isBot) {
			BufferedImage bot = new BufferedImage(80, 50, BufferedImage.TYPE_INT_RGB);
			Graphics2D botG2d = bot.createGraphics();
			botG2d.setColor(Color.decode("#5a65ea"));
			botG2d.fillRect(0, 0, 80, 50);
			botG2d.setColor(Color.WHITE);
			botG2d.setFont(new Font("gg sans", Font.BOLD, 30));
			botG2d.drawString("BOT", 8, 35);
			botG2d.dispose();
			
			
			
			BufferedImage botMask = new BufferedImage(bot.getWidth(), bot.getHeight(), BufferedImage.TYPE_INT_ARGB);
			botG2d = botMask.createGraphics();
			botG2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			botG2d.fill(new RoundRectangle2D.Float(0, 0, botMask.getWidth(), botMask.getHeight(), botMask.getWidth(), botMask.getHeight()));
			botG2d.dispose();
			
			BufferedImage botMasked = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
			botG2d = botMasked.createGraphics();
			botG2d.drawImage(bot, 0, 0, null);
			botG2d.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_IN));
			botG2d.drawImage(bot, 0, 0, null);
			botG2d.dispose();
			
			int botDiameter = Math.min(botMasked.getWidth(), botMasked.getHeight());
			BufferedImage botCropped = new BufferedImage(botDiameter, botDiameter, BufferedImage.TYPE_INT_ARGB);
			botG2d = botCropped.createGraphics();
			botG2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			botG2d.clip(new RoundRectangle2D.Float(0, 0, botDiameter, botDiameter, botDiameter, botDiameter));
			int botX = (botMasked.getWidth() - botDiameter) / 2;
			int botY = (botMasked.getHeight() - botDiameter) / 2;
			botG2d.drawImage(botMasked, -botX, -botY, null);
			botG2d.dispose();
			
			g2d.drawImage(bot, width + 20 + margin + (int) (textWidth * 3.7), margin, null);
		}
		
		BufferedImage textImage = new BufferedImage((int) (contentWidth * 3.7) + 60, contentLength * 60, BufferedImage.TYPE_INT_RGB);
		Graphics2D textG2d = textImage.createGraphics();
		textG2d.setColor(Color.decode("#313338"));
		textG2d.fillRect(0, 0, (int) (contentWidth * 3.7) + 60, contentLength * 60);
		textG2d.setColor(Color.WHITE);
		textG2d.setFont(new Font("gg sans", Font.BOLD, 35));
		for (int i = 0; i < divided.length; i++) {
			textG2d.drawString(divided[i] + "\n\n", 10, 40 * (i + 1));
		}
		textG2d.dispose();
		
		g2d.drawImage(textImage, width + 10 + margin, 50 + margin, null);
		g2d.dispose();
		
		return image;
	}
	
	public InputStream getImageInputStream(BufferedImage image) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(image, "png", os);
			return new ByteArrayInputStream(os.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String[] divide20(String content) {
		int length = content.length();
		
		int count = length / 20;
		
		int remainder = length % 20;
		
		if (remainder != 0) {
			count++;
		}
		
		String[] divided = new String[count];
		
		for (int i = 0; i < count; i++) {
			int start = i * 20;
			int end = (i + 1) * 20;
			
			if (end > length) {
				end = length;
			}
			
			divided[i] = content.substring(start, end);
		}
		
		return divided;
	}
}
