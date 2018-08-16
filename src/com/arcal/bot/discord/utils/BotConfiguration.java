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
package com.arcal.bot.discord.utils;

import java.util.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;

/**
 *
 * @author Arcal
 */
public class BotConfiguration {
    private String configFile = "bot.properties";
    private Properties prop = new Properties();
    private boolean initSuccessfully = true;
    
    public BotConfiguration() {
        try {
            File f = new File(configFile);
            if(!f.exists()) {
                f.createNewFile();
                this.storeDefaultConfigs();
            } else {
                this.applyDefaultConfigs();
                this.refresh();
            }
        } catch(IOException ex) {
            ex.printStackTrace();
            this.initSuccessfully = false;
        }
    }
    
    public void applyDefaultConfigs() {
        if(this.initSuccessfully) {
            prop.setProperty("api-token", "<token>");
            prop.setProperty("activity", "@ArcalBot | Now in Java!");
            prop.setProperty("instance-mode", "guild");
            prop.setProperty("shard-count", "10");
        }
    }
    
    public void storeDefaultConfigs() {
        if(this.initSuccessfully) {
            this.applyDefaultConfigs();
            this.storeConfigs();
        }
    }
    
    public void storeConfigs() {
        if(this.initSuccessfully) {
            try {
                Writer output = new FileWriter(this.configFile);
                prop.store(output, null);
                output.flush();
                output.close();
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public String getValue(String key, String defaultValue) {
        // this.refresh();
        return prop.getProperty(key, defaultValue);
    }
    
    public String getValue(String key) {
        // this.refresh();
        return prop.getProperty(key);
    }
    
    public void setValue(String key, String value) {
        prop.setProperty(key, value);
        this.storeConfigs();
    }
    
    public void refresh() {
        try {
            Reader input = new FileReader(this.configFile);
            prop.load(input);
            input.close();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
}
