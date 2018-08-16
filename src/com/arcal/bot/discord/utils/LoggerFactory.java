/*
 * @author Arcal
 */
package com.arcal.bot.discord.utils;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.logging.*;

/**
 *
 * @author Arcal
 */
public class LoggerFactory {
    private static LogFormatter formatter = new LogFormatter();
    private static StdoutConsoleHandler cHandler = new StdoutConsoleHandler();
    private static FileHandler fHandler = null;
    
    static {
        LogManager.getLogManager().reset();
        cHandler.setFormatter(formatter);
    }
    
    public static Logger make(String name) {
        Logger logger = Logger.getLogger(name);
        
        if(fHandler == null) {
            try {
                fHandler = new FileHandler("ArcalBot-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) +".log");
                fHandler.setFormatter(formatter);
            } catch(IOException ex) {
                // ;
            }
        }
        logger.removeHandler(cHandler); logger.addHandler(cHandler);
        logger.removeHandler(fHandler); logger.addHandler(fHandler);
        
        return logger;
    }
}
