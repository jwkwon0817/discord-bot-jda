package com.codeverse.commands.calculate.operations;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Operations extends ListenerAdapter {
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
		if (e.getName().equals("calculate")) {
			if (e.getSubcommandName().equals("operations")) {
				
				if (e.getOption("query") != null) {
					String query = e.getOption("query").getAsString();
					String result = calculate(query);
					
					EmbedBuilder eb = new EmbedBuilder();
					eb.clear();
					
					eb.setTitle("**Calculator**");
					eb.setDescription("Query: " + query);
					eb.addField("Result", result, false);
					
					eb.setColor(Color.CYAN);
					e.replyEmbeds(eb.build()).queue();
				} else {
					
					Button one = Button.secondary("cal-one", "1");
					Button two = Button.secondary("cal-two", "2");
					Button three = Button.secondary("cal-three", "3");
					Button four = Button.secondary("cal-four", "4");
					Button five = Button.secondary("cal-five", "5");
					Button six = Button.secondary("cal-six", "6");
					Button seven = Button.secondary("cal-seven", "7");
					Button eight = Button.secondary("cal-eight", "8");
					Button nine = Button.secondary("cal-nine", "9");
					
					// zero buttons
					Button twoZero = Button.secondary("cal-two-zero", "00");
					Button zero = Button.secondary("cal-zero", "0");
					
					Button dot = Button.secondary("cal-dot", ".");
					Button equal = Button.success("cal-equal", "=");
					Button exit = Button.secondary("cal-exit", "Exit");
					
					Button reset = Button.danger("cal-reset", "C");
					
					Button delete = Button.danger("cal-delete", "⌫");
					
					Button sum = Button.success("cal-sum", "+");
					Button subtract = Button.success("cal-subtract", "-");
					Button multiply = Button.success("cal-multiply", "×");
					Button divide = Button.success("cal-divide", "÷");
					
					Button[][] buttons = {
							{one, two, three, sum},
							{four, five, six, subtract},
							{seven, eight, nine, multiply},
							{twoZero, zero, dot, divide},
							{equal, exit, reset, delete}
					};
					
					e.reply("**" + e.getUser().getAsMention() + "님의 계산기**\n" + "```\n```").setActionRow(buttons[0]).addActionRow(buttons[1]).addActionRow(buttons[2]).addActionRow(buttons[3]).addActionRow(buttons[4]).queue();
				}
			}
		}
	}
	
	@Override
	public void onButtonInteraction(ButtonInteractionEvent e) {
		String currentMessage = e.getMessage().getContentRaw();
		
		List<String> componentIds = new ArrayList<>(Arrays.stream(new String[] {
				"cal-one", "cal-two", "cal-three", "cal-four", "cal-five", "cal-six", "cal-seven", "cal-eight", "cal-nine",
				"cal-two-zero", "cal-zero", "cal-dot", "cal-equal", "cal-exit", "cal-reset", "cal-delete", "cal-sum", "cal-subtract", "cal-multiply", "cal-divide"
		}).toList());
		
		if (!componentIds.contains(e.getComponentId())) {
			return;
		}
		
		String userId = e.getMessage().getContentRaw().split("님의")[0].replace("**", "").replace("<@", "").replace(">", "").replace("!", "");
		
		if (!userId.equals(e.getUser().getId())) {
			if (!e.getComponentId().equals("cal-really-exit")) {
				EmbedBuilder embedBuilder = new EmbedBuilder();
				embedBuilder.setColor(Color.RED);
				embedBuilder.setTitle("**Error**");
				embedBuilder.setDescription("You can't use other's calculator.");
				
				e.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
			}
		}
		
		String user = e.getMessage().getContentRaw().split("\n")[0];
		
		switch (e.getComponentId()) {
			case "cal-one" -> {
				e.editMessage(addString(currentMessage, "1")).queue();
			}
			
			case "cal-two" -> {
				e.editMessage(addString(currentMessage, "2")).queue();
			}
			
			case "cal-three" -> {
				e.editMessage(addString(currentMessage, "3")).queue();
			}
			
			case "cal-four" -> {
				e.editMessage(addString(currentMessage, "4")).queue();
			}
			
			case "cal-five" -> {
				e.editMessage(addString(currentMessage, "5")).queue();
			}
			
			case "cal-six" -> {
				e.editMessage(addString(currentMessage, "6")).queue();
			}
			
			case "cal-seven" -> {
				e.editMessage(addString(currentMessage, "7")).queue();
			}
			
			case "cal-eight" -> {
				e.editMessage(addString(currentMessage, "8")).queue();
			}
			
			case "cal-nine" -> {
				e.editMessage(addString(currentMessage, "9")).queue();
			}
			
			case "cal-two-zero" -> {
				e.editMessage(addString(currentMessage, "00")).queue();
			}
			
			case "cal-zero" -> {
				e.editMessage(addString(currentMessage, "0")).queue();
			}
			
			case "cal-dot" -> {
				EmbedBuilder embedBuilder = new EmbedBuilder();
				embedBuilder.setColor(Color.RED);
				embedBuilder.setTitle("**Error**");
				embedBuilder.setDescription("You can't use dot now.");
				
				String message = e.getMessage().getContentRaw().replace(user, "").replace("```", "").replace("\n", "");
				String lastChar = message.substring(message.length() - 1);
				
				String[] nums = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
				List<String> numsList = Arrays.asList(nums);
				
				if (numsList.contains(lastChar)) {
					e.editMessage(addString(currentMessage, ".")).queue();
				} else {
					e.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
				}
			}
			
			case "cal-equal" -> {
				try {
					String message = e.getMessage().getContentRaw().replace(user, "").replace("```", "").replace("\n", "");
					String query = e.getMessage().getContentRaw().replace(user, "").replace("```", "").replace("\n", "");
					
					String result = calculate(query);
					
					int resultInt = Integer.parseInt(result);
					
					if (resultInt == Integer.MAX_VALUE) {
						result = "MAX VALUE";
					}
					
					Button resultButton = Button.success("cal-result", result).asDisabled();
					
					e.editMessage(user + "\n```\n" + message + " = " + result + "```").setActionRow(resultButton).queue();
				} catch (Exception exception) {
					EmbedBuilder embed = new EmbedBuilder();
					embed.setColor(Color.RED);
					embed.setTitle("**Error**");
					embed.setDescription("Invalid input");
					embed.addField("Input", currentMessage, false);
					
					e.replyEmbeds(embed.build()).setEphemeral(true).queue();
				}
			}
			
			case "cal-exit" -> {
				e.getMessage().delete().queue();
			}
			
			case "cal-reset" -> {
				e.editMessage(user + "\n```\n```").queue();
			}
			
			case "cal-delete" -> {
				String message = e.getMessage().getContentRaw().replace(user, "").replace("```", "").replace("\n", "");
				
				String lastChar = message.substring(message.length() - 1);
				
				if (lastChar.equals(" ")) {
					e.editMessage(user + "\n```\n" + message.substring(0, message.length() - 3) + "```").queue();
				} else {
					e.editMessage(user + "\n```\n" + message.substring(0, message.length() - 1) + "```").queue();
				}
			}
			
			case "cal-sum" -> {
				String message = e.getMessage().getContentRaw().replace(user, "").replace("```", "").replace("\n", "");
				
				String lastChar = message.substring(message.length() - 1);
				
				if (lastChar.equals(" ")) return;
				
				e.editMessage(addString(currentMessage, " + ")).queue();
			}
			
			case "cal-subtract" -> {
				String message = e.getMessage().getContentRaw().replace(user, "").replace("```", "").replace("\n", "");
				
				String lastChar = message.substring(message.length() - 1);
				
				if (lastChar.equals(" ")) return;
				
				e.editMessage(addString(currentMessage, " - ")).queue();
			}
			
			case "cal-multiply" -> {
				String message = e.getMessage().getContentRaw().replace(user, "").replace("```", "").replace("\n", "");
				
				String lastChar = message.substring(message.length() - 1);
				
				if (lastChar.equals(" ")) return;
				
				e.editMessage(addString(currentMessage, " × ")).queue();
			}
			
			case "cal-divide" -> {
				String message = e.getMessage().getContentRaw().replace(user, "").replace("```", "").replace("\n", "");
				
				String lastChar = message.substring(message.length() - 1);
				
				if (lastChar.equals(" ")) return;
				
				e.editMessage(addString(currentMessage, " ÷ ")).queue();
			}
		}
	}
	
	public static String addString(String currentMessage, String addMessage) {
		String string = currentMessage.split("\n")[0];
		
		String result = currentMessage.replace(string, "").replace("```", "").replace("\n", "");
		result = "```\n" + result + addMessage + "\n```";
		
		return string + "\n" + result;
	}
	
	public static void applyOp(Stack<Double> nums, Stack<Character> ops) {
		double b = nums.pop();
		double a = nums.pop();
		char op = ops.pop();
		switch (op) {
			case '+' -> nums.push(a + b);
			case '-' -> nums.push(a - b);
			case '*' -> nums.push(a * b);
			case '/' -> nums.push(a / b);
		}
	}
	
	public static int getPriority(char op) {
		return switch (op) {
			case '*', '/' -> 2;
			case '+', '-' -> 1;
			default -> 0;
		};
	}
	
	public static String calculate(String query) {
		query = query.replace(" ", "").replace("×", "*").replace("÷", "/");
		
		Stack<Double> nums = new Stack<>();
		Stack<Character> ops = new Stack<>();
		
		int i = 0;
		while (i < query.length()) {
			if (query.charAt(i) == ' ') {
				i++;
				continue;
			}
			if (Character.isDigit(query.charAt(i)) || query.charAt(i) == '.') {
				StringBuilder numStr = new StringBuilder();
				while (i < query.length() && (Character.isDigit(query.charAt(i)) || query.charAt(i) == '.')) {
					numStr.append(query.charAt(i));
					i++;
				}
				double num = Double.parseDouble(numStr.toString());
				nums.push(num);
			} else {
				while (!ops.empty() && getPriority(ops.peek()) >= getPriority(query.charAt(i))) {
					applyOp(nums, ops);
				}
				ops.push(query.charAt(i));
				i++;
			}
		}
		
		
		while (!ops.empty()) {
			applyOp(nums, ops);
		}
		
		double result = nums.pop();
		
		if (result % 1.0 == 0) {
			return String.valueOf((int) result);
		}
		
		String operation = String.valueOf(result).split("\\.")[1];
		
		double returnValue = result;
		
		if (operation.length() > 3) {
			returnValue = Double.parseDouble(String.format("%.3f", result));
		}
		
		if ((int) returnValue == Integer.MAX_VALUE) {
			return "MAX_VALUE";
		}
		
		return String.valueOf(returnValue);
	}
}
