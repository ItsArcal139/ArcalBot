/*
 * @author Arcal
 */
package com.arcal.bot.discord.utils;

import com.arcal.bot.discord.*;
import java.util.logging.*;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.*;
import net.dv8tion.jda.core.events.guild.*;
import net.dv8tion.jda.core.events.guild.member.*;
import net.dv8tion.jda.core.hooks.*;

/**
 *
 * @author Arcal
 */
public class GuildListener extends ListenerAdapter {
    private Logger logger = LoggerFactory.make("GuildListener");
    
    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        logger.info("The bot has joined a new guild, sending a welcome message.");
        Guild g = event.getGuild();
        JDA jda = event.getJDA();
        
        g.getOwner().getUser().openPrivateChannel().queue(pc -> {
            EmbedBuilder eb = new EmbedBuilder();
            SelfUser botUser = jda.getSelfUser();
            
            eb.setAuthor("ArcalBot", null, botUser.getAvatarUrl());
            eb.setTitle("Hi, there!");
            eb.setDescription("Thank you for using " + botUser.getAsMention() + " ! Click [here](https://www.arcal.cf/bot/commmands) to see available commands!");
            pc.sendMessage(eb.build()).queue();
        });
        
        if(!Main.isUsingShard()) {
            ArcalBot bot = new ArcalBot(jda, Main.getArgs());
            logger.info("Creating an instance and booting it.");
            Main.addInstance(jda, bot, g);
        }
    }
    
    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        if(!Main.isUsingShard()) {
            Guild g = event.getGuild();
            logger.info("The bot has left a guild, shutting down the bot and removing it.");
            ArcalBot bot = Main.removeInstanceByGuild(g);
            logger.log(Level.INFO, "The bot {0} has been removed.", bot.toString());
        }
    }
}
