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

import com.arcal.bot.discord.commands.*;
import net.dv8tion.jda.core.entities.*;

/**
 *
 * @author Arcal
 */
public class UserSender implements CommandSender {
    private User user;
    private Message msg;
    
    public UserSender(User user, Message msg) {
        this.user = user;
        this.msg = msg;
    }
    
    @Override
    public String getName() {
        return user.getName();
    }
    
    public User getUser() {
        return this.user;
    }

    public Message getOriginMessage() {
        return this.msg;
    }
    
    @Override
    public void sendMessage(String msg) {
        user.openPrivateChannel().queue(pc -> {
            pc.sendMessage(msg).queue();
        });
    }

    @Override
    public void sendMessage(MessageEmbed msg) {
        user.openPrivateChannel().queue(pc -> {
            pc.sendMessage(msg).queue();
        });
    }
    
    public String getMention() {
        return "<@!" + user.getId() + ">";
    }
}
