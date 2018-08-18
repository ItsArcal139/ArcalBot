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
package com.arcal.bot.discord.commands;

import com.arcal.bot.discord.*;
import com.arcal.bot.discord.exception.*;
import com.arcal.bot.discord.utils.*;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.logging.Logger;
import javafx.scene.paint.Color;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.*;

/**
 *
 * @author Arcal
 */
public abstract class Command {
    private static Map<String, Command> registry = new HashMap<>();
    private String name = null;
    private List<String> aliases = new ArrayList<>();
    private String description = "A command.";
    
    private boolean isExperimental = false;
    private Scope cmdScope = Scope.All;
    
    public enum Scope {
        None, Console, User, Member, All;
    }
    
    protected Command(String name) {
        this.name = name;
    }
    
    protected final void flagCommandScope(Scope scope) {
        this.cmdScope = scope;
    }
    
    protected final void flagAsExperimental() {
        this.isExperimental = true;
    }
    
    public final boolean isExperimental() {
        return this.isExperimental;
    }

    protected final void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }
    
    public static Command getCommand(String name) {
        return registry.get(name);
    }

    /**
     * This method should be called from {@link Command#execute(CommandSender, ArcalBot, String[], Message)},
     * since it checks the sender that if he is allowed to execute this command.
     * @param sender The command sender.
     */
    protected final void checkSender(CommandSender sender) {
        switch(this.cmdScope) {
            case Console:
                if(!(sender instanceof ConsoleSender)) {
                    throw new ScopeException("This command is console-only.", Scope.Console);
                }
                break;
            case User:
                if(!(sender instanceof UserSender)) {
                    throw new ScopeException("This command is user-only.", Scope.User);
                }
                break;
            case Member:
                if(!(sender instanceof MemberSender)) {
                    throw new ScopeException("This command is in-server only.", Scope.Member);
                }
                break;
            case None:
                throw new ScopeException("This command is currently locked.", Scope.None);
        }
    }
    
    public abstract void execute(CommandSender sender, ArcalBot bot, String[] args);

    public static CommandResult execute(CommandSender sender, String name, ArcalBot bot, String[] args) {
        try {
            /*
            if(msg == null) {
                Logger.getLogger("ArcalBot").info("Work in progress!");
            } else {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Work in progress");
                eb.setDescription("The Java version of <@!"+bot.getJDA().getSelfUser().getId()+"> is still in development!");
                eb.setColor(0xff4414);
                eb.setAuthor("ArcalBot", null, bot.getJDA().getSelfUser().getAvatarUrl());
                msg.getChannel().sendMessage(eb.build()).queue();
            }
            */
            
            Command cmd = Command.getCommand(name);
            if(cmd != null) {
                cmd.execute(sender, bot, args);
            } else {
                if(sender instanceof ConsoleSender) {
                    bot.getLogger().severe("Command not found!");
                } else {
                    throw new CommandNotFoundException("Command `" + name + "` not found!");
                }
            }
            return new CommandResult(true);
        } catch (Throwable ex) {
            CommandResult r = new CommandResult(false);
            r.setThrowable(ex);
            return r;
        }
    }

    public final String getDescription() {
        return this.description;
    }

    protected final void setDescription(String desc) {
        this.description = desc;
    }
    
    static {
        registry.put("help", new CommandHelp());
        registry.put("exit", new CommandExit());
        registry.put("dump", new CommandDump());
        registry.put("crash", new CommandCrash());
        registry.put("react", new CommandReact());
    }
    
    public static Collection<Command> getRegisteredCommands() {
        return registry.values();
    }
}
