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
import java.util.*;
import java.util.logging.*;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.*;

/**
 *
 * @author Arcal
 */
public class CommandHelp extends Command {
    /**
     * Create a {@code CommandHelp} instance. It sends command list to the sender.
     */
    public CommandHelp() {
        super("help");
    }
    
    /**
     * Send command list to the sender.
     */
    @Override
    public void execute(CommandSender sender, ArcalBot bot, String[] args) {
        this.checkSender(sender);
        Guild g = null;
        if(sender instanceof MemberSender) g = ((MemberSender)sender).getGuild();
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
        
        if(sender instanceof UserSender) {
            UserSender us = (UserSender) sender;
            us.getOriginMessage().getChannel().sendMessage(us.getMention() + ", check your DM! ;)").queue();
        }
    }
}
