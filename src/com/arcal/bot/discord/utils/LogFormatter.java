/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arcal.bot.discord.utils;

import java.text.*;
import java.util.Date;
import java.util.logging.*;

/**
 *
 * @author Arcal
 */
public class LogFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        String lvlName = record.getLevel().getName();
        String msg = record.getMessage();
        String name = record.getLoggerName();
        
        return String.format("%s [%s] %s - %s\n", new SimpleDateFormat("HH:mm:ss").format(new Date()), lvlName, name, MessageFormat.format(msg, record.getParameters()));
    }
}
