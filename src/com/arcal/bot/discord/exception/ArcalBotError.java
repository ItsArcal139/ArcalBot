/*
 * @author Arcal
 */
package com.arcal.bot.discord.exception;

/**
 * The {@code ArcalBotError} class indicates the unrecoverable faults thrown by the ArcalBot own,
 * not other API.
 * 
 * @author Arcal
 */
public class ArcalBotError extends Error {
    protected ArcalBotError() {
        super();
    }
    
    protected ArcalBotError(String msg) {
        super(msg);
    }

    protected ArcalBotError(String msg, Throwable cause) {
        super(msg, cause);
    }
    
    protected ArcalBotError(Throwable cause) {
        super(cause);
    }
    
    protected ArcalBotError(String msg, Throwable cause, boolean canSuppress, boolean writableStackTrace) {
        super(msg, cause, canSuppress, writableStackTrace);
    }
}
