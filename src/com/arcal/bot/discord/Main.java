/**
 * MIT License
 * 
 * Copyright (c) 2018 Arcal
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.arcal.bot.discord;

import com.arcal.bot.discord.commands.*;
import com.arcal.bot.discord.exception.*;
import com.arcal.bot.discord.utils.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.logging.*;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.*;
import net.dv8tion.jda.core.events.message.*;
import net.dv8tion.jda.core.hooks.*;
import javax.security.auth.login.*;

// JLine
import org.jline.terminal.*;
import org.jline.reader.*;
import org.jline.reader.impl.*;

/**
 *
 * @author Arcal
 */
public class Main {
    private static BotConfiguration config = null;
    public static Logger logger = null;
    private static InstanceMode mode = InstanceMode.Guild;
    
    private static boolean isRunning = false;
    private static List<JDA> jdaInstances = new ArrayList<>();
    private static List<ArcalBot> botInstances = new ArrayList<>();

    public static BotConfiguration getGlobalConfig() {
        return config;
    }
    
    private static String[] args;
    
    public static String[] getArgs() {
        return args;
    }
    
    public static void main(String[] args) {
        isRunning = true;
        Main.args = args;
        config = new BotConfiguration();
        config.refresh();
        logger = LoggerFactory.make("ArcalBot");
        Terminal terminal = null;
        LineReader lineReader = null;
        try {
            terminal = TerminalBuilder.terminal();
            lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .highlighter(new DefaultHighlighter())
                    .parser(new DefaultParser())
                    .build();
        } catch(IOException ex) {
            logger.severe("Failed to use JLine terminal!");
            System.exit(1);
        }
        
        String modeStr = config.getValue("instance-mode").toLowerCase();
        switch(modeStr) {
            case "guild":
                mode = InstanceMode.Guild;
                break;
            case "shard":
            case "sharding":
                mode = InstanceMode.Shard;
                break;
            default:
                logger.log(Level.WARNING, "Invalid mode \"{0}\"! Will set to default: {1}", new Object[] {modeStr, mode.name()});
                break;
        }
        
        try {
            String token = config.getValue("api-token");
            if(token.equals("<token>")) {
                logger.severe("You must set the Discord client token for ArcalBot.");
                System.exit(1);
            }
            
            JDABuilder builder = new JDABuilder(AccountType.BOT);
            builder.addEventListener(new GuildListener());
            
            try {
                builder.setToken(token);
            } catch(Exception ex) {
                // Does it throw exception?
                logger.severe("Invalid token!");
                System.exit(1);
            }
            
            if(mode == InstanceMode.Guild) {
                logger.log(Level.INFO, "Creating ArcalBot instances per guilds...");
                JDA jda = builder.buildBlocking();
                List<Guild> guilds = jda.getGuilds();
                for(int i=0; i<guilds.size(); i++) {
                    Guild g = guilds.get(i);
                    ArcalBot bot = new ArcalBot(jda, args);
                    Main.addInstance(jda, bot, g);
                }
                logger.info("Created " + guilds.size() + " instances of ArcalBot.");
                jdaInstances.add(jda);
            } else if(mode == InstanceMode.Shard) {
                logger.log(Level.INFO, "Creating ArcalBot instances using sharding...");
                int totalShards = 6;
                try {
                    totalShards = Integer.valueOf(config.getValue("shard-count"));
                } catch(NumberFormatException ex) {
                    // ;
                }
                
                for(int i=0; i<totalShards; i++) {
                    ArcalBot bot = new ArcalBot(null, args);
                    builder.addEventListener(bot);
                    botInstances.add(bot);
                }
                
                for(int i=0; i<totalShards; i++) {
                    JDA jda = builder.useSharding(i, totalShards).buildAsync();
                    ArcalBot bot = botInstances.get(i);
                    bot.useShard(jda);
                    bot.setLogger(LoggerFactory.make("ArcalBot-" + (i+1)));
                    jdaInstances.add(jda);
                    logger.log(Level.INFO, "Created ArcalBot instances per shard => {0}/{1}", new Object[] { i+1, totalShards });
                }
            }
                
            for(ArcalBot bot : botInstances) {
                bot.start();
            }
            
            Main.updateActivity();
            Thread activityThread = new Thread(() -> {
                boolean activated = false;
                while(isRunning) {
                    if(Calendar.getInstance().get(Calendar.SECOND) % 5 == 0) {
                        if(!activated) {
                            activated = true;
                            Main.updateActivity();
                        }
                    } else {
                        activated = false;
                    }
                }
            });
            activityThread.start();
            
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                Main.shutdown();
            }, "ArcalBot Shutdown Hook"));
        } catch(LoginException | InterruptedException ex) {
            logger.log(Level.SEVERE, "Failed to login!");
        }
        
        // Prompt
        if(lineReader != null) {
            while(true) {
                String line = null;
                try {
                    line = lineReader.readLine("> ");
                    
                    Main.executeCommand(ConsoleSender.getInstance(), line);
                } catch(UserInterruptException ex) {
                    // ;
                } catch(EndOfFileException ex) {
                    // ;
                }
            }
        }
    }
    
    public static ArcalBot removeInstanceByGuild(Guild g) {
        if(!Main.isUsingShard()) {
            if(g == null) {
                throw new IllegalArgumentException("The given guild cannot be null.");
            }

            ArcalBot bot = null;
            for(ArcalBot inst : botInstances) {
                if(g.getId().equals(inst.getGuild().getId())) {
                    bot = inst;
                    break;
                }
            }
            
            if(bot != null) {
                bot.shutdown();
                botInstances.remove(bot);
            }
            return bot;
        }
        return null;
    }
    
    public static void addInstance(JDA jda, ArcalBot bot, Guild g) {
        jda.addEventListener(bot);
        bot.setGuild(g);
        bot.setLogger(LoggerFactory.make("ArcalBot-" + (botInstances.size()+1)));
        botInstances.add(bot);
    }
    
    private static void updateActivity() {
        for(JDA jda : jdaInstances) {
            jda.getPresence().setGame(Game.playing("@ArcalBot | Shard " + (Main.getIndexOfInstance(jda) + 1) + "/" + Main.getInstanceCount()));
        }
    }
    
    public static boolean isUsingShard() {
        return mode == InstanceMode.Shard;
    }
    
    public static boolean executeCommand(CommandSender sender, String command) {
        for(ArcalBot bot : Main.botInstances) {
            bot.pendCommand(ConsoleSender.getInstance(), command);
        }
        return true;
    }
    
    public static int getIndexOfInstance(JDA jda) {
        return jdaInstances.indexOf(jda);
    }
    
    public static int getInstanceCount() {
        return jdaInstances.size();
    }
    
    public static void shutdown() {
        isRunning = false;
        Logger logger = LoggerFactory.make(Thread.currentThread().getName());
        logger.log(Level.INFO, "Shutting down ArcalBot instances...");
        for(ArcalBot bot : botInstances) {
            bot.shutdown();
        }
        
        for(JDA jda : jdaInstances) {
            jda.shutdown();
        }
        config.storeConfigs();
        logger.log(Level.INFO, "Good bye");
    }
}
