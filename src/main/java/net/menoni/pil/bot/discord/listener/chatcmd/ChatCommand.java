package net.menoni.pil.bot.discord.listener.chatcmd;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.menoni.jda.commons.util.JDAUtil;
import org.springframework.context.ApplicationContext;

import java.util.Collection;

public interface ChatCommand {

	Collection<String> names();

	Collection<Permission> requiredPermissions();

	boolean execute(ApplicationContext applicationContext, GuildMessageChannelUnion channel, Member member, Message message, String alias, String[] args);

	Collection<String> help();

	default boolean canExecute(ApplicationContext applicationContext, GuildMessageChannelUnion channel, Member member, Message message, String alias, String[] args) {
		return true;
	}

	default void reply(GuildMessageChannelUnion channel, String alias, String message) {
		try {
			JDAUtil.completableFutureQueue(channel.sendMessage("**%s**: %s".formatted(alias, message))).join();
		} catch (Throwable e) {
			String channelName = channel != null ? "#" + channel.getName() : "null-channel";
			System.err.println("failed to send message to %s:\n%s\n%s".formatted(
					channelName,
					message,
					e.getClass().getName() + ": " + e.getMessage()
			));
		}
	}

	default void sendHelp(GuildMessageChannelUnion channel, String errorText) {
		String name = names().stream().findFirst().orElse("?");
		if (errorText != null) {
			errorText = " (%s)".formatted(errorText);
		} else {
			errorText = "";
		}
		channel.sendMessage("**%s** help:%s\n%s".formatted(name, errorText, String.join("\n", help()))).queue();
	}

}
