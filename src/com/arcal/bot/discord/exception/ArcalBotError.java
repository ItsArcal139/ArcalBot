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
package com.arcal.bot.discord.exception;

/**
 * The {@code ArcalBotError} class indicates the unrecoverable faults thrown by the ArcalBot own,
 * not other API.
 * 
 * @author Arcal
 */
public class ArcalBotError extends Error {
    protected ArcalBotError() {
        super();
    }
    
    protected ArcalBotError(String msg) {
        super(msg);
    }

    protected ArcalBotError(String msg, Throwable cause) {
        super(msg, cause);
    }
    
    protected ArcalBotError(Throwable cause) {
        super(cause);
    }
    
    protected ArcalBotError(String msg, Throwable cause, boolean canSuppress, boolean writableStackTrace) {
        super(msg, cause, canSuppress, writableStackTrace);
    }
}
