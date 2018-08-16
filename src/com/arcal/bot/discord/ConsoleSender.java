/*
 * @author Arcal
 */
package com.arcal.bot.discord;

import com.arcal.bot.discord.commands.CommandSender;
import java.util.logging.Logger;
import net.dv8tion.jda.core.entities.*;

/**
 *
 * @author Arcal
 */
public class ConsoleSender implements CommandSender {
    public String getName() {
        return "Console";
    }
    
    private static final ConsoleSender instance = new ConsoleSender();
    
    public static ConsoleSender getInstance() {
        return instance;
    }

    @Override
    public void sendMessage(String msg) {
        Logger logger = Logger.getLogger("ArcalBot");
        logger.info(msg);
    }

    @Override
    public void sendMessage(MessageEmbed msg) {
        this.sendMessage("# " + msg.getTitle());
        this.sendMessage("==");
        this.sendMessage(msg.getDescription());
        
        for(MessageEmbed.Field f : msg.getFields()) {
            this.sendMessage(f.getName()+": ");
            this.sendMessage(f.getValue());
            this.sendMessage("");
        }
    }
}
