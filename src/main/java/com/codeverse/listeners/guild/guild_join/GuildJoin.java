package com.codeverse.listeners.guild.guild_join;

import ch.qos.logback.classic.Logger;
import com.codeverse.main.Bot;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.LoggerFactory;

public class GuildJoin extends ListenerAdapter {
	Logger logger = (Logger) LoggerFactory.getLogger(GuildJoin.class);
	
	@Override
	public void onGuildJoin(GuildJoinEvent e) {
		String log = "Joined server: " + e.getGuild().getName() + " (" + e.getGuild().getId() + ")";
		logger.info(log);
		Bot.updateLog(log, "info");
	}
}
