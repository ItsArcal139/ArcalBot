/*
 * @author Arcal
 */
package com.arcal.bot.discord.exception;

import com.arcal.bot.discord.commands.*;

/**
 *
 * @author Arcal
 */
public class ScopeException extends ArcalBotException {
    public ScopeException() {
        super();
    }
    
    public ScopeException(String msg) {
        super(msg);
    }
    
    private Command.Scope scope = Command.Scope.None;
    
    public ScopeException(Command.Scope scope) {
        this();
        this.scope = scope;
    }
    
    public ScopeException(String msg, Command.Scope scope) {
        this(msg);
        this.scope = scope;
    }
    
    public Command.Scope getRequiredScope() {
        return this.scope;
    }
}
