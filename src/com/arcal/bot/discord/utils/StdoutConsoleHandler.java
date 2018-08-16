/*
 * @author Arcal
 */
package com.arcal.bot.discord.utils;

import java.io.*;
import java.util.logging.*;

/**
 *
 * @author Arcal
 */
public class StdoutConsoleHandler extends ConsoleHandler {
    @Override
    protected void setOutputStream(OutputStream o) {
        super.setOutputStream(System.out); // It's so called StdoutConsoleHandler.
    }
}
