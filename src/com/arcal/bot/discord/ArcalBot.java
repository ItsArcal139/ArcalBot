/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

    public final BotConfiguration getGlobalConfig() {
        return Main.getGlobalConfig();
    }
    
    public ArcalBot(JDA jda, String[] args) {
        this.isRunning = true;
        this.jda = jda;
        File cfdir = new File("configs/");
        if(!cfdir.exists()) cfdir.mkdir();
    }
    
    public void setLogger(Logger logger) {
        this.logger = logger;
    }
    
    public void useShard(JDA jda) {
        this.guildMode = false;
        this.jda = jda;
    }
    
    public void setGuild(Guild g) {
        if(!Main.isUsingShard()) {
            this.guildMode = true;
            this.guildManagers.clear();
            this.guildManagers.put(g, new GuildManager(g));
        }
    }
    
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
    
    @Override
    public void onDisconnect(DisconnectEvent event) {
        if(event.getJDA().equals(jda)) {
            this.isReady = false;
        }
    }
    
    public void start() {
        Thread commandHandler = new Thread(() -> {
            while(this.isRunning) {
                if(pendingCommands.size() > 0) {
                    PendingCommand pc = pendingCommands.remove(0);
                    Thread t = new Thread(pc);
                    t.start();
                }
            }
        }, "Command Handler Loop");
        commandHandler.start();
    }
    
    public void shutdown() {
        this.isRunning = false;
        for(GuildManager manager : this.guildManagers.values()) {
            manager.getConfig().storeConfigs();
        }
    }
    
    public void pendCommand(CommandSender sender, String cmdLine, Message msg) {
        this.pendingCommands.add(new PendingCommand(this, sender, cmdLine, msg));
    }
    
    public GuildConfiguration getGuildConfig(Guild guild) {
        GuildManager manager = this.getGuildManager(guild);
        if(manager == null) {
            throw new NullPointerException("Should the GuildManager be null? Guild ID: " + guild.getId());
        }
        return manager.getConfig();
    }
    
    public GuildManager getGuildManager(Guild guild) {
        return this.guildManagers.get(guild);
    }
    
    public String getCommandPrefix(Guild guild) {
        return this.getGuildConfig(guild).getValue("cmd-prefix");
    }
    
    public void setCommandPrefix(Guild guild, String prefix) {
        this.getGuildConfig(guild).setValue("cmd-prefix", prefix);
    }
    
    public void setDumpExceptions(Guild guild, boolean flag) {
        this.getGuildConfig(guild).setValue("dump-exceptions", flag ? "true" : "false");
    }
    
    public boolean doesDumpExceptions(Guild guild) {
        return this.getGuildConfig(guild).getValue("dump-exceptions").toLowerCase().equals("true");
    }

    /**
     * Note: should only be called within API.
     * @param sender
     * @param cmdLine
     * @param msg
     * @return 
     */
    public CommandResult handleCommand(CommandSender sender, String cmdLine, Message msg) {
        ArcalBot bot = this;
        final Guild guild;
        if(msg != null) {
            guild = msg.getGuild();
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
            CommandResult result = Command.execute(sender, cmdName, bot, args, msg);
            return result;
        };

        FutureTask<CommandResult> task = new FutureTask<>(callable);
        new Thread(task).start();
        
        try {
            CommandResult result = task.get();
            logger.log(Level.INFO, "Executed with result => {0}", result.isSuccessed());

            if(msg != null) {
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
    
    private void buildEmbedByThrowable(EmbedBuilder eb, Throwable t, boolean doDump) {
        String errMsg = t.getMessage();
        eb.setTitle("Error occured");
        eb.setDescription(errMsg == null ? (doDump ? "I saw him. He was creepy." : "Unknown error! Try dump mode and do it again.") : errMsg);
        
        boolean shouldForceDump = t instanceof Error;
        if(shouldForceDump) {
            // That means we might met an unrecoverable fault.
            // (We even should not try to catch that according to Javadoc.)
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

    public Logger getLogger() {
        return logger;
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getJDA().equals(jda)) {
            ArcalBot bot = this;
            if (!event.isFromType(ChannelType.PRIVATE)) {
                Message msg = event.getMessage();
                String cmdLine = msg.getContentRaw();
                Guild guild = msg.getGuild();
                if(guild == null) {
                    // We abort DMs.
                    return;
                }

                if(this.guildMode && !guild.equals(this.guildManagers.keySet().iterator().next())) {
                    // In guild mode. Abort events from foreign guilds.
                    return;
                }

                String cmdPrefix = this.getGuildConfig(guild).getValue("cmd-prefix");
                boolean shouldHandle = cmdLine.startsWith(cmdPrefix);
                if (shouldHandle) {
                    msg.addReaction("🚥").queue((Void v) -> {
                        Thread t = new Thread(() -> {
                            CommandSender sender = new UserSender(msg.getAuthor());
                            if(msg.getMember() != null) sender = new MemberSender(msg.getMember());
                            // bot.handleCommand(sender, cmdLine.substring(cmdPrefix.length()), msg);
                            bot.pendCommand(sender, cmdLine.substring(cmdPrefix.length()), msg);
                        });
                        t.start();
                    });
                }
            }
        }
    }
    
    public JDA getJDA() {
        return this.jda;
    }
    
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
    
    @Override
    public String toString() {
        return logger.getName();
    }
}
