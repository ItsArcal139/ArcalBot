/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
public class GuildConfiguration {
    private String configFile = "bot.properties";
    private Properties prop = new Properties();
    private boolean initSuccessfully = true;
    
    public GuildConfiguration(String configName) {
        this.configFile = configName+".properties";
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
            prop.setProperty("cmd-prefix", "%");
            prop.setProperty("dj-role-name", "DJ");
            prop.setProperty("locale", "en-us");
            prop.setProperty("dump-exceptions", "false");
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
