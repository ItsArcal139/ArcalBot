/*
 * @author Arcal
 */
package com.arcal.bot.discord.exception;

/**
 *
 * @author Arcal
 */
public class CommandNotFoundException extends ArcalBotException {
    public CommandNotFoundException() {
        super();
    }
    
    public CommandNotFoundException(String msg) {
        super(msg);
    }
}
