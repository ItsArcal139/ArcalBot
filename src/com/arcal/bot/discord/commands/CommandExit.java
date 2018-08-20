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
import net.dv8tion.jda.core.entities.*;

/**
 *
 * @author Arcal
 */
public class CommandExit extends Command {
    private boolean activated = false;
    private final Object lock = new Object();

    /**
     * Create a {@code CommandExit} instance. After it was executed, the bot will exit.
     */
    public CommandExit() {
        super("exit");
        this.flagCommandScope(Scope.Console);
    }
    
    /**
     * Shutdown the bot from the console.
     */
    @Override
    public void execute(CommandSender sender, ArcalBot bot, String[] args) {
        this.checkSender(sender);
        // Use a lock so this command wouldn't be executed simultaneously.
        synchronized(lock) {
            // Trust me, there wouldn't be a deadlock.
            if(!activated) {
                this.activated = true;
                System.exit(0);
            } else {
                throw new RuntimeException("Has already activated once.");
            }
        }
    }
}
