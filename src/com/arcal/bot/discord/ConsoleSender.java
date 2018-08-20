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
package com.arcal.bot.discord;

import com.arcal.bot.discord.commands.CommandSender;
import java.util.logging.Logger;
import net.dv8tion.jda.core.entities.*;

/**
 * The {@code ConsoleSender} indicates that this command was sent directly
 * from the console.
 * @author Arcal
 */
public class ConsoleSender implements CommandSender {
    public String getName() {
        return "Console";
    }
    
    private static final ConsoleSender instance = new ConsoleSender();
    
    public static ConsoleSender getInstance() {
        return instance;
    }

    @Override
    public void sendMessage(String msg) {
        Logger logger = Logger.getLogger("ArcalBot");
        logger.info(msg);
    }

    /**
     * Send a rich embed message to the console. Although console doesn't directly
     * support embed messages, we are able to show those messages by 
     * translating them to plain texts.
     * @param msg The rich embed message.
     */
    @Override
    public void sendMessage(MessageEmbed msg) {
        this.sendMessage("# " + msg.getTitle());
        this.sendMessage("==");
        this.sendMessage(msg.getDescription());
        
        for(MessageEmbed.Field f : msg.getFields()) {
            this.sendMessage(f.getName()+": ");
            this.sendMessage(f.getValue());
            this.sendMessage("");
        }
    }
}
