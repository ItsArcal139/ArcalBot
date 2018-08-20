/**
 * MIT License
 * 
 * Copyright (c) 2018 Arcal
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
    /**
     * Create a {@code CommandDump} instance. It switches the dump mode for the requesting guild.
     */
    public CommandDump() {
        super("dump");
    }
    
    /**
     * Switch the dump mode between on and off.
     */
    @Override
    public void execute(CommandSender sender, ArcalBot bot, String[] args) {
        this.checkSender(sender);
        Guild g = null;
        if(sender instanceof MemberSender) g = ((MemberSender)sender).getGuild();
        
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
        
        if(sender instanceof ConsoleSender) {
            Logger logger = bot.getLogger();
            logger.info("Set dump mode to " + flag);
        } else {
            UserSender s = (UserSender) sender;
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Dump mode " + (flag ? "" : "de") +"activated");
            eb.setDescription((flag ? "A" : "Dea") + "ctivated the dump mode.\nThat means if a command failed to execute, I will " + (flag ? "" : "**NOT** ") + "show you the details.");
            eb.setAuthor("ArcalBot", null, bot.getJDA().getSelfUser().getAvatarUrl());
            s.getOriginMessage().getChannel().sendMessage(eb.build()).queue();
        }
    }
}
