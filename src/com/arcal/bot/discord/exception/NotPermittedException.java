/*
 * @author Arcal
 */
package com.arcal.bot.discord.exception;

/**
 *
 * @author Arcal
 */
public class NotPermittedException extends ArcalBotException {
    public NotPermittedException() {
        super();
    }
    
    public NotPermittedException(String msg) {
        super(msg);
    }
}
