/*
 * @author Arcal
 */
package com.arcal.bot.discord.commands;

import com.arcal.bot.discord.*;
import com.arcal.bot.discord.exception.*;
import java.util.logging.*;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.*;

/**
 *
 * @author Arcal
 */
public class CommandDump extends Command {
    public CommandDump() {
        super("dump");
    }
    
    @Override
    public void execute(CommandSender sender, ArcalBot bot, String[] args, Message msg) {
        this.checkSender(sender);
        Guild g = msg.getGuild();
        boolean flag = !bot.doesDumpExceptions(g);
        if(args.length > 0) {
            String arg = args[0].toLowerCase();
            if(arg.equals("true") || arg.equals("false")) {
                flag = arg.equals("true");
            } else if(arg.equals("on") || arg.equals("off")) {
                flag = arg.equals("on");
            } else {
                throw new IllegalArgumentException("Must be either true/false or on/off.");
            }
        }
        bot.setDumpExceptions(g, flag);
        
        if(msg == null) {
            Logger logger = bot.getLogger();
            logger.info("Set dump mode to " + flag);
        } else {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Dump mode " + (flag ? "" : "de") +"activated");
            eb.setDescription((flag ? "A" : "Dea") + "ctivated the dump mode.\nThat means if a command failed to execute, I will " + (flag ? "" : "**NOT** ") + "show you the details.");
            eb.setAuthor("ArcalBot", null, bot.getJDA().getSelfUser().getAvatarUrl());
            msg.getChannel().sendMessage(eb.build()).queue();
        }
    }
}
