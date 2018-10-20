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
 * The main entry class of the ArcalBot executable.
 * @author Arcal
 */
public class Main {
    private static BotConfiguration config = null;
    public static Logger logger = null;
    
    // The default instance mode for ArcalBot.
    // It is used when building ArcalBot instances.
    private static InstanceMode mode = InstanceMode.Guild;
    
    private static boolean isRunning = false;
    private static List<JDA> jdaInstances = new ArrayList<>();
    private static List<ArcalBot> botInstances = new ArrayList<>();
    
    private static final String VERSION = "0.1";
    
    /**
     * Get the version of this release.
     * @return The version of this release.
     * @since ArcalBot v0.1
     */
    public static String getVersion() {
        return Main.VERSION;
    }

    /**
     * Get the global bot configuration.
     * @return The global bot configuration.
     */
    public static BotConfiguration getGlobalConfig() {
        return config;
    }
    
    private static String[] args;
    
    /**
     * Get the command line arguments passed to the executable.
     * @return The command line arguments.
     */
    public static String[] getArgs() {
        return args;
    }
    
    /**
     * The entry method of the ArcalBot executable.
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        isRunning = true;
        Main.args = args;
        config = new BotConfiguration();
        config.refresh();
        logger = LoggerFactory.make("ArcalBot");
        
        // Here, we try using the JLine terminal.
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
        
        // We check which instance mode we are going to use.
        String modeStr = config.getValue("instance-mode").toLowerCase();
        switch(modeStr) {
            case "guild":
                // ArcalBot and JDA instances are created for each guilds.
                mode = InstanceMode.Guild;
                break;
            case "shard":
            case "sharding":
                // ArcalBot and JDA instances are created for each shards.
                mode = InstanceMode.Shard;
                break;
            default:
                // Unknown mode. Set to the default mode we gave before.
                logger.log(Level.WARNING, "Invalid mode \"{0}\"! Will set to default: {1}", new Object[] {modeStr, mode.name()});
                break;
        }
        
        // First try-catch, to catch Discord API exceptions.
        try {
            String token = config.getValue("api-token");
            // Simply validates the token.
            // At least cannot be the default "<token>" one in the demo.
            if(token.equals("<token>")) {
                // The token was not given. Quit because a token is required.
                logger.severe("You must set the Discord client token for ArcalBot.");
                System.exit(1);
            }
            
            // Since we may have a valid token, we may now start building JDA instances.
            JDABuilder builder = new JDABuilder(AccountType.BOT);
            builder.addEventListener(new GuildListener());
            
            // Nice try. Not tested if it works.
            try {
                builder.setToken(token);
            } catch(Exception ex) {
                // Does it throw exception?
                logger.severe("Invalid token!");
                System.exit(1);
            }
            
            // Check the instance mode.
            if(mode == InstanceMode.Guild) {
                logger.log(Level.INFO, "Creating ArcalBot instances per guilds...");
                JDA jda = builder.buildBlocking();
                List<Guild> guilds = jda.getGuilds();
                for(int i=0; i<guilds.size(); i++) {
                    Guild g = guilds.get(i);
                    ArcalBot bot = new ArcalBot(jda);
                    Main.addInstance(jda, bot, g);
                }
                logger.log(Level.INFO, "Created {0} instances of ArcalBot.", guilds.size());
                jdaInstances.add(jda);
            } else if(mode == InstanceMode.Shard) {
                // Since it is a new mode for ArcalBot, the code we have written before has
                // to be optimized for sharding mode.
                
                // For example, ArcalBot instances cannot
                // use JDAs in sharding mode because those ArcalBot has to be registered to
                // JDA builder before the builder may build JDA instances.
                
                // For this, we have moved some code from the constructor to a new method,
                // ArcalBot.start(). That would be a workaround for this situation.
                
                logger.log(Level.INFO, "Creating ArcalBot instances using sharding...");
                int totalShards = 6;
                try {
                    totalShards = Integer.valueOf(config.getValue("shard-count"));
                } catch(NumberFormatException ex) {
                    // Invalid integer. The total shards count will have the default value kept.
                }
                
                // Adding an ArcalBot instance for each shards. 
                for(int i=0; i<totalShards; i++) {
                    ArcalBot bot = new ArcalBot(null);
                    builder.addEventListener(bot);
                    botInstances.add(bot);
                }
                
                // We may start building sharding JDA sessions now.
                for(int i=0; i<totalShards; i++) {
                    JDA jda = builder.useSharding(i, totalShards).buildAsync();
                    
                    // Assign JDA to ArcalBot.
                    ArcalBot bot = botInstances.get(i);
                    bot.useShard(jda);
                    bot.setLogger(LoggerFactory.make("ArcalBot-" + (i+1)));
                    jdaInstances.add(jda);
                    logger.log(Level.INFO, "Created ArcalBot instances per shard => {0}/{1}", new Object[] { i+1, totalShards });
                }
            }
            
            // We may start those instances now.
            for(ArcalBot bot : botInstances) {
                bot.start();
            }
            
            // Update the activity text, then do it every 5 seconds.
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
            
            // Add the shutdown hook to the runtime.
            // It will be called when using System.exit(), but it will NOT be
            // called when it receives a SIGKILL signal.
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                Main.shutdown();
            }, "ArcalBot Shutdown Hook"));
        } catch(LoginException | InterruptedException ex) {
            logger.log(Level.SEVERE, "Failed to login!");
        }
        
        // Start accepting console input.
        if(lineReader != null) {
            while(true) {
                String line = null;
                try {
                    line = lineReader.readLine("> ");
                    
                    Main.executeCommand(ConsoleSender.getInstance(), line);
                } catch(UserInterruptException | EndOfFileException ex) {
                    // ;
                }
            }
        }
    }
    
    /**
     * Find the ArcalBot which handles the given guild, and remove it.
     * Only works if {@link Main#isUsingShard()} returns false.
     * @param g The guild which the target ArcalBot handles.
     * @return The removed ArcalBot instance or null if the instance was not found.
     */
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
    
    /**
     * Register an ArcalBot instance.
     * @param jda The JDA session.
     * @param bot The ArcalBot to be registered.
     * @param g The guild which the ArcalBot will be handling.
     */
    public static void addInstance(JDA jda, ArcalBot bot, Guild g) {
        jda.addEventListener(bot);
        bot.setGuild(g);
        bot.setLogger(LoggerFactory.make("ArcalBot-" + (botInstances.size()+1)));
        botInstances.add(bot);
    }
    
    /**
     * Updates the Discord activity of ArcalBot.
     */
    private static void updateActivity() {
        for(JDA jda : jdaInstances) {
            jda.getPresence().setGame(Game.playing("@ArcalBot | Shard " + (Main.getIndexOfInstance(jda) + 1) + "/" + Main.getInstanceCount()));
        }
    }
    
    /**
     * Get whether the ArcalBot is using shards.
     * @return true if the ArcalBot is using shards.
     */
    public static boolean isUsingShard() {
        return mode == InstanceMode.Shard;
    }
    
    /**
     * Executes commands from the given command line and sender.
     * @param sender The command sender.
     * @param command The command line.
     * @return true if the command was executed successfully.
     */
    public static boolean executeCommand(CommandSender sender, String command) {
        logger.info("Pending command => " + command);
        for(ArcalBot bot : Main.botInstances) {
            bot.pendCommand(ConsoleSender.getInstance(), command);
        }
        return true;
    }
    
    /**
     * Get the index of the given JDA in the JDA instances list.
     * @param jda The JDA instance to find the index of.
     * @return The index of the {@code jda} in the JDA instances list.
     */
    public static int getIndexOfInstance(JDA jda) {
        return jdaInstances.indexOf(jda);
    }
    
    /**
     * Get the count of registered JDA instances.
     * @return The size of JDA instances list.
     */
    public static int getInstanceCount() {
        return jdaInstances.size();
    }
    
    /**
     * Shutdown the registered ArcalBot instances.
     * Should only be called when exiting.
     */
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
