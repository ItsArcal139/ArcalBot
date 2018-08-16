/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arcal.bot.discord.utils;

/**
 *
 * @author Arcal
 */
public class CommandResult {
    private boolean isSuccessed = false;
    private String message = null;
    private Type type = Type.Successed;
    private Throwable ex = null;
    
    public CommandResult(boolean successed, String message) {
        this.isSuccessed = successed;
        this.message = message;
    }
    
    public void setThrowable(Throwable ex) {
        this.ex = ex;
    }
    
    public Throwable getThrowable() {
        return this.ex;
    }
    
    public CommandResult(boolean successed) {
        this(successed, null);
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public void setSuccessed(boolean flag) {
        this.isSuccessed = flag;
    }
    
    public boolean isSuccessed() {
        return this.isSuccessed;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public void setResultType(Type type) {
        this.type = type;
    }
    
    public Type getResultType() {
        return this.type;
    }
    
    public enum Type {
        Successed, NotFound
    }
}
