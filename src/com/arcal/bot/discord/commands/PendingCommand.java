/*
 * @author Arcal
 */
package com.arcal.bot.discord.commands;

import com.arcal.bot.discord.*;
import net.dv8tion.jda.core.entities.*;

/**
 * The {@code PendingCommand} class indicates the pending commands to be executed.
 * @author Arcal
 */
public class PendingCommand implements Runnable {
    private ArcalBot bot = null;
    private CommandSender sender = null;
    private String cmdLine = null;
    private Message msg = null;
    
    public PendingCommand(ArcalBot bot, CommandSender sender, String cmdLine, Message msg) {
        this.bot = bot;
        this.sender = sender;
        this.cmdLine = cmdLine;
        this.msg = msg;
    }
    
    @Override
    public void run() {
        bot.handleCommand(sender, cmdLine, msg);
    }
}
