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
    
    /**
     * The {@code Scope} indicates the usable range of a command.
     */
    public enum Scope {
        None, Console, User, Member, All;
    }
    
    /**
     * Create a command instance.
     * @param name The command name.
     */
    protected Command(String name) {
        this.name = name;
    }
    
    /**
     * Indicate that this command is in the specified scope.
     * @param scope The specified scope.
     */
    protected final void flagCommandScope(Scope scope) {
        this.cmdScope = scope;
    }
    
    /**
     * Flag this command as experimental.
     */
    protected final void flagAsExperimental() {
        this.isExperimental = true;
    }
    
    /**
     * Get whether this command is experimental.
     * @return true if this command is experimental.
     */
    public final boolean isExperimental() {
        return this.isExperimental;
    }

    /**
     * Get the available aliases of this command.
     * @param aliases The available alias list.
     */
    protected final void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }
    
    /**
     * Get the command instance by it's name.
     * @param name The name of the target command.
     * @return The corresponding command, or null if not found.
     */
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
    
    /**
     * Execute the command.
     * @param sender The command sender.
     * @param bot The calling {@link ArcalBot} instance.
     * @param args The command arguments.
     */
    public abstract void execute(CommandSender sender, ArcalBot bot, String[] args);

    /**
     * Execute a command.
     * @param sender The command sender.
     * @param name The name of the command.
     * @param bot The calling {@link ArcalBot} instance.
     * @param args The command arguments.
     * @return The command result indicates if it was executed successfully.
     */
    public static CommandResult execute(CommandSender sender, String name, ArcalBot bot, String[] args) {
        try {            
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

    /**
     * Get the description of the command.
     * @return The description of the command.
     */
    public final String getDescription() {
        return this.description;
    }

    /**
     * Set the description of the command.
     * @param desc The desired description of the command.
     */
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
    
    /**
     * Get a list of the registered commands.
     * @return The registered command list.
     */
    public static Collection<Command> getRegisteredCommands() {
        return registry.values();
    }
}
