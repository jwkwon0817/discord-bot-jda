package com.codeverse.listeners.user.reply_mention;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReplyMention extends ListenerAdapter {
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		if (e.getAuthor().isBot()) return;
		
		String[] randomReplies = {
				"**Nice to meet you!**",
				"**Hello!**",
				"**Hi!**",
				"**Hey!**",
				"**How are you?**",
				"**How are you doing?**",
				"**How are you doing today?**"
		};
		
		List<String> mentions = new ArrayList<>(Arrays.asList(randomReplies));
		
		if (e.getMessage().getMentions().isMentioned(e.getJDA().getSelfUser())) {
			int random = (int) (Math.random() * mentions.size());
			e.getMessage().reply(mentions.get(random)).queue();
		}
	}
}
