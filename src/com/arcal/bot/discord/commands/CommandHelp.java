/*
 * @author Arcal
 */
package com.arcal.bot.discord.commands;

import com.arcal.bot.discord.*;
import com.arcal.bot.discord.exception.*;
import java.util.*;
import java.util.logging.*;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.*;

/**
 *
 * @author Arcal
 */
public class CommandHelp extends Command {
    public CommandHelp() {
        super("help");
    }
    
    @Override
    public void execute(CommandSender sender, ArcalBot bot, String[] args, Message msg) {
        this.checkSender(sender);
        Guild g = msg.getGuild();
        Collection<Command> cmds = Command.getRegisteredCommands();
        
        String link = "https://www.arcal.cf/bot/help";
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("ArcalBot", null, bot.getJDA().getSelfUser().getAvatarUrl());
        eb.setColor(0xff4414);
        eb.setTitle("Command list", link);

        String desc = "There are some common commands below.\n";
        desc += "**Play:** `" + bot.getCommandPrefix(g) + "play`";
        eb.setDescription(desc);

        eb.addField("Need more info?", "See [here](" + link + ") for more commands.", true);
        sender.sendMessage(eb.build());
        
        if(msg != null) {
            UserSender us = (UserSender) sender;
            msg.getChannel().sendMessage(us.getMention() + ", check your DM! ;)").queue();
        }
    }
}
