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
 * The {@code MemberSender} indicates that this command sender is a
 * Discord user, but only guild members.
 * This class inherits {@link UserSender}, as members send commands via
 * Discord messages.
 * @author Arcal
 */
public class MemberSender extends UserSender {
    private Member user;
    
    public MemberSender(Member user, Message msg) {
        super(user.getUser(), msg);
        this.user = user;
    }
    
    @Override
    public String getName() {
        return user.getNickname();
    }
    
    /**
     * Get the guild member sent the command.
     * @return The corresponding guild member.
     */
    public Member getMember() {
        return user;
    }
    
    /**
     * Get the guild of the sender.
     * @return The guild of the member sender.
     */
    public Guild getGuild() {
        return user.getGuild();
    }
}
