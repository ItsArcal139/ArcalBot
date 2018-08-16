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
public class CommandCrash extends Command {
    public CommandCrash() {
        super("crash");
        this.flagCommandScope(Scope.None);
    }

    @Override
    public void execute(CommandSender sender, ArcalBot bot, String[] args, Message msg) {
        this.checkSender(sender);
        throw new DebugCrashError();
    }
}
