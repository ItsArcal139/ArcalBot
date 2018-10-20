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
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.logging.*;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.*;
import net.dv8tion.jda.core.events.message.*;
import net.dv8tion.jda.core.hooks.*;

/**
 *
 * @author Arcal
 */
public class ArcalBot extends ListenerAdapter {
    private JDA jda = null;
    private Logger logger = Main.logger;
    private Map<Guild, GuildManager> guildManagers = new HashMap<>();
    private List<PendingCommand> pendingCommands = new ArrayList<>();
    private boolean isRunning = false;
    private boolean guildMode = false;
    
    private boolean isReady = false;
    private boolean isStarted = false;

    /**
     * Get the global bot configuration.
     * The call is equivalent to {@link Main#getGlobalConfig()}.
     * @return The global bot configuration.
     * @see Main#getGlobalConfig() 
     */
    public final BotConfiguration getGlobalConfig() {
        return Main.getGlobalConfig();
    }
    
    /**
     * Create an ArcalBot instance, with a given JDA session.
     * @param jda The JDA session. Can be null only when using shards.
     * @throws IllegalStateException when {@code jda} is null and is not using shards.
     */
    public ArcalBot(JDA jda) throws IllegalStateException {
        if(!Main.isUsingShard() && jda == null) {
            throw new IllegalStateException("The JDA session cannot be null.");
        }
        this.isRunning = true;
        this.jda = jda;
        File cfdir = new File("configs/");
        if(!cfdir.exists()) cfdir.mkdir();
    }
    
    /**
     * Specify the logger of this ArcalBot instance.
     * @param logger A specified logger.
     */
    public void setLogger(Logger logger) {
        this.logger = logger;
    }
    
    /**
     * Make ArcalBot prepared to use shards.
     * @param jda The JDA session.
     * @throws IllegalStateException when it's called after {@link ArcalBot#start()}.
     */
    public void useShard(JDA jda) throws IllegalStateException {
        if(this.isStarted) {
            throw new IllegalStateException("Cannot run useShard() after start()");
        }
        this.guildMode = false;
        this.jda = jda;
    }
    
    /**
     * Set the handing guild of this ArcalBot instance.
     * @param g The guild to be handled.
     */
    public void setGuild(Guild g) {
        if(!Main.isUsingShard()) {
            this.guildMode = true;
            this.guildManagers.clear();
            this.guildManagers.put(g, new GuildManager(g));
        }
    }
    
    /**
     * Called when the JDA was ready and only if was built asynchronously.
     * @param event The ready event.
     */
    @Override
    public void onReady(ReadyEvent event) {
        if(event.getJDA().equals(jda)) {
            this.isReady = true;
            logger.log(Level.INFO, "The instance is ready.");
            if(!this.guildMode) {
                List<Guild> guilds = jda.getGuilds();
                for(Guild guild : guilds) {
                    // logger.log(Level.INFO, "This ArcalBot instance is handling for guild => {0}", guild.getName());
                    GuildManager manager = new GuildManager(guild);
                    this.guildManagers.put(guild, manager);
                }
            }
        }
    }
    
    /**
     * Called when the JDA was disconnected from the Discord API.
     * @param event The disconnect event.
     */
    @Override
    public void onDisconnect(DisconnectEvent event) {
        if(event.getJDA().equals(jda)) {
            this.isReady = false;
        }
    }
    
    /**
     * Start the ArcalBot.
     */
    public void start() {
        Thread commandHandler = new Thread(() -> {
            while(this.isRunning) {
                // A workaround for the handler not handling the new pending commands.
                Thread.yield();
                if(pendingCommands.size() > 0) {
                    PendingCommand pc = pendingCommands.remove(0);
                    Thread t = new Thread(pc);
                    t.start();
                }
            }
        }, "Command Handler Loop");
        commandHandler.start();
        this.isStarted = true;
    }
    
    /**
     * Get whether this instance was started.
     * @return true if it was started.
     */
    public boolean isStarted() {
        return this.isStarted;
    }
    
    /**
     * Get whether this instance is running. Mainly used by while loops.
     * @return true if it is running.
     */
    public boolean isRunning() {
        return this.isRunning;
    }
    
    /**
     * Shutdown the ArcalBot instance.
     */
    public void shutdown() {
        this.isRunning = false;
        for(GuildManager manager : this.guildManagers.values()) {
            manager.getConfig().storeConfigs();
        }
    }
    
    /**
     * Queue a command to the pending list.
     * @param sender The command sender.
     * @param cmdLine The command line to be pended.
     */
    public void pendCommand(CommandSender sender, String cmdLine) {
        this.pendingCommands.add(new PendingCommand(this, sender, cmdLine));
    }
    
    /**
     * Get the guild-wide configuration for a guild.
     * @param guild The specified guild.
     * @return The corresponding configuration.
     */
    public GuildConfiguration getGuildConfig(Guild guild) {
        GuildManager manager = this.getGuildManager(guild);
        if(manager == null) {
            throw new NullPointerException("Should the GuildManager be null? Guild ID: " + guild.getId());
        }
        return manager.getConfig();
    }
    
    /**
     * Get the guild manager handling the specified guild from this instance.
     * @param guild The handled guild.
     * @return The corresponding guild manager.
     */
    public GuildManager getGuildManager(Guild guild) {
        return this.guildManagers.get(guild);
    }
    
    /**
     * Get the command prefix for a specified guild.
     * @param guild A specified guild.
     * @return The corresponding command prefix.
     */
    public String getCommandPrefix(Guild guild) {
        return this.getGuildConfig(guild).getValue("cmd-prefix");
    }
    
    /**
     * Set the command prefix to a specified one for a guild.
     * @param guild A specified guild.
     * @param prefix The desired prefix.
     */
    public void setCommandPrefix(Guild guild, String prefix) {
        this.getGuildConfig(guild).setValue("cmd-prefix", prefix);
    }
    
    /**
     * Set whether ArcalBot should dump error details for the specified guild.
     * @param guild The specified guild.
     * @param flag true if it should dump details for the guild.
     */
    public void setDumpExceptions(Guild guild, boolean flag) {
        this.getGuildConfig(guild).setValue("dump-exceptions", flag ? "true" : "false");
    }
    
    /**
     * Get whether ArcalBot should dump error details for the specified guild.
     * @param guild The specified guild.
     * @return true if it should dump details for the guild.
     */
    public boolean doesDumpExceptions(Guild guild) {
        return this.getGuildConfig(guild).getValue("dump-exceptions").toLowerCase().equals("true");
    }

    /**
     * Handle a command line.
     * Note: should only be called within API.
     * 
     * @param sender The command sender.
     * @param cmdLine The command line.
     * @return A command result, which indicates whether it was successful.
     */
    public CommandResult handleCommand(CommandSender sender, String cmdLine) {
        ArcalBot bot = this;
        final Guild guild;
        if(sender instanceof MemberSender) {
            guild = ((MemberSender) sender).getMember().getGuild();
        } else {
            guild = null;
        }
        
        Callable<CommandResult> callable = () -> {
            String[] splits = cmdLine.split(" ");
            String cmdName = splits[0];
            
            String[] args = new String[splits.length - 1];
            for (int i = 1; i < splits.length; i++) {
                args[i - 1] = splits[i];
            }
            
            logger.log(Level.INFO, "Start executing commmand => {0}", cmdLine);
            CommandResult result = Command.execute(sender, cmdName, bot, args);
            return result;
        };

        FutureTask<CommandResult> task = new FutureTask<>(callable);
        new Thread(task).start();
        
        try {
            CommandResult result = task.get();
            logger.log(Level.INFO, "Executed with result => {0}", result.isSuccessed());

            if(sender instanceof UserSender) {
                Message msg = ((UserSender) sender).getOriginMessage();
                
                if (result.isSuccessed()) {
                    msg.clearReactions().queue((Void v2) -> {
                        msg.addReaction("👌").queue();
                    });
                } else {
                    msg.clearReactions().queue((Void v2) -> {
                        msg.addReaction("🛑").queue();
                        Throwable thrown = result.getThrowable();
                        if(thrown != null) {
                            String errMsg = thrown.getMessage();
                            EmbedBuilder eb = new EmbedBuilder();
                            this.buildEmbedByThrowable(eb, thrown, this.doesDumpExceptions(guild));
                            msg.getChannel().sendMessage(eb.build()).queue(v -> {
                                if(thrown instanceof Error) {
                                    try {
                                        Thread.sleep(500);
                                    } catch(InterruptedException ex) {
                                        // Ignore it, the bot is going to quit.
                                    }
                                    System.exit(1);
                                }
                            });
                        } else {
                            if(this.doesDumpExceptions(guild)) {
                                EmbedBuilder eb = new EmbedBuilder();
                                eb.setTitle("Command failed without exception!");
                                eb.setDescription("When `CommandResult.isSuccess` is set to `false`, a `Throwable` should be also attached to debug easier.");
                                eb.setColor(0xff4414);
                                eb.setAuthor("ArcalBot", null, bot.getJDA().getSelfUser().getAvatarUrl());
                                msg.getChannel().sendMessage(eb.build()).queue();
                            }
                        }
                    });
                }
            } else {
                if(result.isSuccessed()) {
                } else {
                    Throwable t = result.getThrowable();
                    if(this.doesDumpExceptions(guild)) {
                        // That means we are gonna dump our faulty error!
                        if(t != null) {
                            t.printStackTrace();
                        } else {
                            logger.severe("Command failed without exceptions!");
                        }
                    }
                }
            }
            return result;
        } catch(InterruptedException | ExecutionException ex) {
            return new CommandResult(false);
        }
    }
    
    /**
     * Build a rich-embed message from a {@link Throwable}.
     * @param eb An {@link EmbedBuilder} instance for more customizations.
     * @param t The occurred error.
     * @param doDump Whether it should dump the details of the {@code Throwable}.
     */
    private void buildEmbedByThrowable(EmbedBuilder eb, Throwable t, boolean doDump) {
        String errMsg = t.getMessage();
        eb.setTitle("Error occured");
        eb.setDescription(errMsg == null ? (doDump ? "I saw him. He was creepy." : "Unknown error! Try dump mode and do it again.") : errMsg);
        
        // Since Errors should not really be caught and handled safely, if
        // we caught them, we should force-dump it and shutdown the ArcalBot
        // immediately.
        boolean shouldForceDump = t instanceof Error;
        if(shouldForceDump) {
            // Random quotes. Makes it looks not-so-serious.
            List<String> quotes = Arrays.asList(
                    "I blame Arcal.",
                    "Ah! Gotcha, ArcalBotty.",
                    "That's all right. I can buy you a teddy bear!",
                    "Arcal, did you set the TNT right there?",
                    "Who threw that grenade?",
                    "Ouch, that really hurts me."
            );
            String quote = quotes.get(((int)Math.floor(Math.random() * quotes.size())));
            eb.setTitle("Oh, snap!");
            eb.setDescription(quote);
            
            eb.addField("What happened?", "The bot has went into an unrecoverable trouble. "
                    + "That means the evil guy detailed below killed the bot. So sad :(\n"
                    + "Contact <@217238973246865408> quick with a screenshot of this message! For saving the bot.\n ", false);
            eb.addField("The murderer's message", t.getMessage(), false);
        }
        
        if(doDump || shouldForceDump) {
            eb.addField("Exception", "`" + t.getClass().getName() + "`", false);

            String stackTrace = "";
            StackTraceElement[] sts = t.getStackTrace();
            for(int i=0; i < Math.min(sts.length, 5); i++) {
                StackTraceElement st = sts[i];
                if(i > 0) stackTrace += "\n";
                stackTrace += "`@ " + st.getClassName() + "." + st.getMethodName() + "():" + (st.isNativeMethod() ? "(Native)" : st.getLineNumber()) + "`";
            }

            eb.addField("Stack Trace", stackTrace, false);
        }
        
        eb.setColor(0xff4414);
        eb.setAuthor("ArcalBot", null, this.getJDA().getSelfUser().getAvatarUrl());
    }

    /**
     * Get the attached logger of this instance.
     * @return The logger attached to this instance.
     */
    public Logger getLogger() {
        return logger;
    }
    
    /**
     * Called when the JDA session attached to this ArcalBot
     * received an message from Discord.
     * @param event The event passed from Discord API.
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getJDA().equals(jda)) {
            ArcalBot bot = this;
            if (!event.isFromType(ChannelType.PRIVATE)) {
                // We abort DMs.
                Message msg = event.getMessage();
                String cmdLine = msg.getContentRaw();
                Guild guild = msg.getGuild();

                if(this.guildMode && !guild.equals(this.guildManagers.keySet().iterator().next())) {
                    // In guild mode. Abort events from foreign guilds.
                    return;
                }

                String cmdPrefix = this.getGuildConfig(guild).getValue("cmd-prefix");
                boolean shouldHandle = cmdLine.startsWith(cmdPrefix);
                if (shouldHandle) {
                    msg.addReaction("🚥").queue((Void v) -> {
                        CommandSender sender = new UserSender(msg.getAuthor(), msg);
                        if(msg.getMember() != null) sender = new MemberSender(msg.getMember(), msg);
                        bot.pendCommand(sender, cmdLine.substring(cmdPrefix.length()));
                    });
                }
            }
        }
    }
    
    /**
     * Get the JDA session.
     * @return The JDA session.
     */
    public JDA getJDA() {
        return this.jda;
    }
    
    /**
     * Get the guild this instance is handling.
     * @return The guild this ArcalBot is handling.
     */
    public Guild getGuild() {
        Set<Guild> gs = this.guildManagers.keySet();
        if(gs.size() > 1) {
            throw new IllegalStateException("There are more than 1 guild handled by this instance.");
        }
        if(gs.isEmpty()) {
            return null;
        }
        return gs.iterator().next();
    }
    
    /**
     * Get the proper name for this ArcalBot instance.
     * @return The name for this instance.
     */
    @Override
    public String toString() {
        return logger.getName();
    }
}
