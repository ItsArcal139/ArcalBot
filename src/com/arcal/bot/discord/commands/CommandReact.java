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
import java.util.concurrent.TimeUnit;
import java.util.logging.*;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.*;

/**
 *
 * @author Arcal
 */
public class CommandReact extends Command {
    /**
     * Create a {@code CommandReact} command. It reacts to the sent message
     * with the user-specified emoji or emote.
     */
    public CommandReact() {
        super("react");
        this.flagCommandScope(Scope.User);
        this.flagAsExperimental();
    }
    
    /**
     * Add react by the given emoji or emote. 
     */
    @Override
    public void execute(CommandSender sender, ArcalBot bot, String[] args) {
        this.checkSender(sender);
        Message msg = ((UserSender)sender).getOriginMessage();
        
        if(args.length > 0) {
            try {
                Emote emote = bot.getJDA().getEmoteById(args[0]);
                if(emote != null) {
                    msg.addReaction(emote).queueAfter(2, TimeUnit.SECONDS);
                } else {
                    throw new EmoteException("Cannot use emojis of guilds I did not joined!");
                }
            } catch(NumberFormatException ex) {
                try {
                    msg.addReaction(args[0]).complete();
                    msg.addReaction(args[0]).queueAfter(2, TimeUnit.SECONDS);
                } catch(Exception ex2) {
                    throw new EmoteException("Unknown emoji!", ex2);
                }
            }
        } else {
            throw new IllegalArgumentException("Please provide an emoji to react.");
        }
    }
}
