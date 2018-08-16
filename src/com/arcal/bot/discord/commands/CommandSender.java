/*
 * @author Arcal
 */
package com.arcal.bot.discord.commands;

import net.dv8tion.jda.core.entities.*;

/**
 *
 * @author Arcal
 */
public interface CommandSender {
    String getName();
    void sendMessage(String msg);
    void sendMessage(MessageEmbed msg);
}
