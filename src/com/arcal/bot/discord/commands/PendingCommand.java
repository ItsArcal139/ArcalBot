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
import net.dv8tion.jda.core.entities.*;

/**
 * The {@code PendingCommand} class indicates the pending commands to be executed.
 * @author Arcal
 */
public class PendingCommand implements Runnable {
    private ArcalBot bot = null;
    private CommandSender sender = null;
    private String cmdLine = null;
    
    public PendingCommand(ArcalBot bot, CommandSender sender, String cmdLine) {
        this.bot = bot;
        this.sender = sender;
        this.cmdLine = cmdLine;
    }
    
    @Override
    public void run() {
        bot.handleCommand(sender, cmdLine);
    }
}
