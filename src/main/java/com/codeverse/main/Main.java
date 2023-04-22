package com.codeverse.main;

import com.codeverse.commands.calculate.operations.Operations;
import com.codeverse.commands.draw.black_and_white.BlackAndWhite;
import com.codeverse.commands.guild.status.Status;
import com.codeverse.commands.manage.ban.Ban;
import com.codeverse.commands.manage.kick.Kick;
import com.codeverse.commands.manage.mute.Mute;
import com.codeverse.commands.debug.Debugger;
import com.codeverse.commands.message.assemble.Assemble;
import com.codeverse.commands.message.disassemble.Disassemble;
import com.codeverse.commands.utils.Test;
import com.codeverse.commands.utils.hanriver_temperature.HanriverTemperature;
import com.codeverse.listeners.guild.guild_join.GuildJoin;
import com.codeverse.commands.message.dm.Dm;
import com.codeverse.commands.message.clear.Clear;
import com.codeverse.commands.message.reverse.Reverse;
import com.codeverse.commands.game.random_dice.RandomDice;
import com.codeverse.commands.game.reaction_rate.ReactionRate;
import com.codeverse.commands.game.rock_scissor_paper.RockScissorsPaper;
import com.codeverse.commands.search.cat.Cat;
import com.codeverse.commands.draw.fake_message.FakeMessage;
import com.codeverse.commands.user.avatar.Avatar;
import com.codeverse.commands.search.emoji.Emoji;
import com.codeverse.commands.utils.image.emoji.EmojiToImage;
import com.codeverse.commands.utils.get_ip_by_domain.GetIpByDomain;
import com.codeverse.commands.promote.MyBlog;
import com.codeverse.commands.utils.ping.Ping;
import com.codeverse.listeners.projects.server_link.message.Message;
import com.codeverse.listeners.user.reply_mention.ReplyMention;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class Main extends ListenerAdapter {
	public static void main(String[] args) throws Exception {
		Logger jdaLogger = (Logger) LoggerFactory.getLogger("net.dv8tion.jda");
		Logger logger = (Logger) LoggerFactory.getLogger(Main.class);
		jdaLogger.setLevel(Level.OFF);
		
		JDA jda = JDABuilder.createDefault(Bot.getToken())
				.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
				.setBulkDeleteSplittingEnabled(false)
				.setStatus(OnlineStatus.DO_NOT_DISTURB)
				.setMemberCachePolicy(MemberCachePolicy.VOICE.or(MemberCachePolicy.OWNER))
				.setChunkingFilter(ChunkingFilter.NONE)
				.disableIntents(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGE_TYPING)
				.setLargeThreshold(50)
				.setAutoReconnect(true)
				
				.addEventListeners(
						new Ping(),
						new Message(),
						new FakeMessage(),
						new RandomDice(),
						new RockScissorsPaper(),
						new GetIpByDomain(),
						new Status(),
						new Ban(),
						new Mute(),
						new Kick(),
						new Debugger(),
						new MyBlog(),
						new Emoji(),
						new EmojiToImage(),
						new Avatar(),
						new Cat(),
						new GuildJoin(),
						new ReplyMention(),
						new ReactionRate(),
						new Reverse(),
						new Dm(),
						new Clear(),
						new BlackAndWhite(),
						new Operations(),
						new Disassemble(),
						new Assemble(),
						new HanriverTemperature(),
						new Test()
				)
				
				.setStatus(OnlineStatus.ONLINE)
				.enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_BANS, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_MESSAGE_TYPING)
				.build();
		
		jda.awaitReady();
		
		jda.updateCommands().addCommands(
				new CommandData[]{
						Commands.slash("draw", "Draw an image!")
								.addSubcommands(
										new SubcommandData("fake_message", "Draw an image!")
												.addOptions(
														new OptionData(OptionType.USER, "user", "Input user", true),
														new OptionData(OptionType.STRING, "text", "Input text", true)
												),
										new SubcommandData("black_and_white", "Draw an image to black and white!")
												.addOptions(
														new OptionData(OptionType.ATTACHMENT, "image", "Input image.", true)
												)
								),
						
						Commands.slash("game", "Game commands!")
								.addSubcommands(
										new SubcommandData("random_dice", "Roll the dice!"),
										new SubcommandData("rock_scissors_paper", "Rock Scissors Paper!"),
										new SubcommandData("reaction_rate", "Reaction rate!")
						),
						
						Commands.slash("utils", "Utils commands!")
								.addSubcommands(
										new SubcommandData("get_ip_by_domain", "Get IP by domain")
												.addOptions(
														new OptionData(OptionType.STRING, "domain", "Input domain", true)
												),
										new SubcommandData("ping", "Ping!"),
										new SubcommandData("hanriver_temperature", "Get Hanriver temperature!")
								),
						
						Commands.slash("guild", "Guild commands!")
								.addSubcommands(
										new SubcommandData("status", "Show guild status")
												.addOptions(
														new OptionData(OptionType.BOOLEAN, "details", "Show details", false)
												)
								),
						
						Commands.slash("manage", "Manage the server")
								.addSubcommands(
										new SubcommandData("ban", "Ban a user")
												.addOptions(
														new OptionData(OptionType.USER, "user", "Input user", true),
														new OptionData(OptionType.STRING, "reason", "Input reason", false)
												)
								).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS))
								.addSubcommands(
									new SubcommandData("kick", "Kick a user")
											.addOptions(
													new OptionData(OptionType.USER, "user", "Input user", true),
													new OptionData(OptionType.STRING, "reason", "Input reason", false)
											)
								).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.KICK_MEMBERS))
								.addSubcommands(
									new SubcommandData("mute", "Mute a user")
											.addOptions(
													new OptionData(OptionType.USER, "user", "Input user", true),
													new OptionData(OptionType.STRING, "time", "Input time", true),
													new OptionData(OptionType.STRING, "reason", "Input reason", false)
											)
								).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MODERATE_MEMBERS)),
						
						Commands.slash("promote", "Promote me!")
								.addSubcommands(
										new SubcommandData("my_blog", "Promote my blog!")
								),
						
						Commands.slash("search", "Search something.")
								.addSubcommands(
										new SubcommandData("emoji", "Search emoji.")
												.addOptions(
														new OptionData(OptionType.STRING, "name", "Input emoji name.", true)
												),
										new SubcommandData("cat", "Random cat.")
								),
						
						Commands.slash("user", "Commands about user.")
								.addSubcommands(
								new SubcommandData("avatar", "Get user avatar.")
										.addOptions(
												new OptionData(OptionType.USER, "user", "Input user.", false)
										)
						),
						
						Commands.slash("debug", "Debugging commands for owner.")
								.addSubcommands(
								new SubcommandData("leave_guild", "Leave guild.")
										.addOptions(
												new OptionData(OptionType.STRING, "guild_id", "Input guild id. (Default: current guild)", false)
										),
								new SubcommandData("show_guilds", "Show guilds."),
								new SubcommandData("create_invite_code", "Create invite code.")
										.addOptions(
												new OptionData(OptionType.STRING, "guild_id", "Input guild id. (Default: current guild)", false)
										),
								new SubcommandData("show_channels", "Show channels.")
										.addOptions(
												new OptionData(OptionType.STRING, "guild_id", "Input guild id. (Default: current guild)", false)
										),
								new SubcommandData("show_messages", "Show messages by channel.")
										.addOptions(
												new OptionData(OptionType.STRING, "guild_id", "Input guild id. (Default: current guild)", false),
												new OptionData(OptionType.STRING, "channel_id", "Input channel id. (Default: current channel)", false),
												new OptionData(OptionType.INTEGER, "limit", "Input limit. (Default: 10)", false)
										),
								new SubcommandData("show_owner", "Show owner.")
										.addOptions(
												new OptionData(OptionType.STRING, "guild_id", "Input guild id. (Default: current guild)", false)
										)
						),
						
						Commands.slash("message", "Message commands.")
								.addSubcommands(
										new SubcommandData("reverse", "Reverse message.")
												.addOptions(
														new OptionData(OptionType.STRING, "message", "Input message.", true)
												),
										new SubcommandData("dm", "Send DM to user.")
												.addOptions(
														new OptionData(OptionType.USER, "user", "Input user.", true),
														new OptionData(OptionType.STRING, "message", "Input message.", true)
												),
										new SubcommandData("clear", "Clear messages.")
												.addOptions(
														new OptionData(OptionType.INTEGER, "amount", "Input amount.", true)
												),
										new SubcommandData("disassemble", "Disassemble messages.")
												.addOptions(
														new OptionData(OptionType.STRING, "message", "Input message.", true)
												),
										new SubcommandData("assemble", "Assemble messages.")
												.addOptions(
														new OptionData(OptionType.STRING, "message", "Input message.", true)
												)
						),
						
						Commands.slash("calculate", "Calculate something.")
								.addSubcommands(
										new SubcommandData("operations", "Calculate four arithmetic operations.")
												.addOptions(
														new OptionData(OptionType.STRING, "query", "Input query.", false)
												)
						)
				}).queue();

		
		logger.info(jda.getSelfUser().getAsTag() + " [ 봇이 시작되었습니다. ]");
		Bot.updateLog(jda.getSelfUser().getAsTag() + " [ 봇이 시작되었습니다. ]", "info");
	}
}