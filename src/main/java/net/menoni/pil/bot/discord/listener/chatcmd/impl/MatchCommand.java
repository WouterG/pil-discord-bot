package net.menoni.pil.bot.discord.listener.chatcmd.impl;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.menoni.pil.bot.discord.listener.ChatCommandListener;
import net.menoni.pil.bot.discord.listener.chatcmd.ChatCommand;
import net.menoni.pil.bot.jdbc.model.JdbcMatch;
import net.menoni.pil.bot.service.MatchService;
import org.springframework.context.ApplicationContext;

import java.util.*;

public class MatchCommand implements ChatCommand {

	@Override
	public Collection<String> names() {
		return List.of("match");
	}

	@Override
	public Collection<Permission> requiredPermissions() {
		return List.of(Permission.MANAGE_ROLES);
	}

	@Override
	public String shortHelpText() {
		return "Match management";
	}

	@Override
	public boolean canExecute(ApplicationContext applicationContext, GuildMessageChannelUnion channel, Member member, boolean silent) {
		return ChatCommandListener.requireBotCmdChannel(applicationContext, channel, silent);
	}

	@Override
	public boolean execute(ApplicationContext applicationContext, GuildMessageChannelUnion channel, Member member, Message message, String alias, String[] args) {
		if (args.length < 2) {
			sendHelp(channel, null);
			return true;
		}

		if (args[0].equalsIgnoreCase("round")) {
			this._exec_round(applicationContext, channel, member, message, alias, args);
		} else {
			sendHelp(channel, "Invalid option '" + args[0].toLowerCase() + "'");
		}

		return true;
	}

	@Override
	public Collection<String> help() {
		return List.of(
				"!match -- show help",
				"!match round <round-number> -- show status of matches for specified round"
		);
	}

	public void _exec_round(ApplicationContext applicationContext, GuildMessageChannelUnion channel, Member member, Message message, String alias, String[] args) {
		if (args.length < 2) {
			sendHelp(channel, "Round number input required");
			return;
		}

		int roundNum = -1;
		try {
			roundNum = Integer.parseInt(args[1]);
		} catch (NumberFormatException ex) {
			reply(channel, alias, "Invalid round-num input - expected number");
			return;
		}

		MatchService matchService = applicationContext.getBean(MatchService.class);
		List<JdbcMatch> matches = matchService.getMatchesForRound(roundNum);

		if (matches == null || matches.isEmpty()) {
			reply(channel, alias, "No matches found for round " + roundNum);
			return;
		}

		StringBuilder sb = new StringBuilder();

		List<JdbcMatch> unfinished = matches.stream().filter(m -> m.getWinTeamId() == null).toList();

		if (!unfinished.isEmpty()) {
			sb.append("## %d/%d unfinished matches\n".formatted(unfinished.size(), matches.size()));
			for (int i = 0; i < unfinished.size(); i++) {
				JdbcMatch m = unfinished.get(i);
				sb.append("- <#%s>\n".formatted(m.getMatchChannelId()));
				if (i >= 10) {
					sb.append("-# and %d more".formatted(unfinished.size() - i));
					break;
				}
			}
		} else {
			sb.append("## All %d matches are finished\n".formatted(matches.size()));
		}

		reply(channel, alias, "Info for round " + roundNum + "\n" + sb);
	}

}
