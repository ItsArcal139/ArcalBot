/*
 * @author Arcal
 */
package com.arcal.bot.discord;

import com.arcal.bot.discord.commands.*;
import net.dv8tion.jda.core.entities.*;

/**
 *
 * @author Arcal
 */
public class MemberSender extends UserSender {
    private Member user;
    
    public MemberSender(Member user) {
        super(user.getUser());
        this.user = user;
    }
    
    @Override
    public String getName() {
        return user.getNickname();
    }
}
