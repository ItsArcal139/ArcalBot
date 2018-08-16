/*
 * @author Arcal
 */
package com.arcal.bot.discord.exception;

import com.arcal.bot.discord.*;
import com.arcal.bot.discord.commands.*;
import net.dv8tion.jda.core.entities.*;

/**
 * This error should be only thrown in {@link CommandCrash#execute(CommandSender, ArcalBot, String[], Message)}.
 * @author Arcal
 */

public class DebugCrashError extends ArcalBotError {
    public DebugCrashError() {
        super("This is a debug crash, aka manual crash.\n"
            + "If the bot has quit with a rich embed message popped, that means the ArcalBot has caught this error gracefully.");
    }
}
