/*
 * @author Arcal
 */
package com.arcal.bot.discord.exception;

/**
 *
 * @author Arcal
 */
public abstract class ArcalBotException extends RuntimeException {
    protected ArcalBotException() {
        super();
    }
    
    protected ArcalBotException(String msg) {
        super(msg);
    }

    protected ArcalBotException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
    protected ArcalBotException(Throwable cause) {
        super(cause);
    }
    
    protected ArcalBotException(String msg, Throwable cause, boolean canSuppress, boolean writableStackTrace) {
        super(msg, cause, canSuppress, writableStackTrace);
    }
}
