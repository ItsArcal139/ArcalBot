/*
 * @author Arcal
 */
package com.arcal.bot.discord.commands;

import com.arcal.bot.discord.*;
import com.arcal.bot.discord.exception.*;
import net.dv8tion.jda.core.entities.*;

/**
 *
 * @author Arcal
 */
public class CommandExit extends Command {
    private boolean activated = false;
    private final Object lock = new Object();

    public CommandExit() {
        super("exit");
        this.flagCommandScope(Scope.Console);
    }
    
    @Override
    public void execute(CommandSender sender, ArcalBot bot, String[] args, Message msg) {
        this.checkSender(sender);
        synchronized(lock) {
            if(!activated) {
                this.activated = true;
                System.exit(0);
            } else {
                throw new RuntimeException("Has already activated once.");
            }
        }
    }
}
