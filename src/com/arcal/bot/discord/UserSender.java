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
public class UserSender implements CommandSender {
    private User user;
    
    public UserSender(User user) {
        this.user = user;
    }
    
    public String getName() {
        return user.getName();
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
