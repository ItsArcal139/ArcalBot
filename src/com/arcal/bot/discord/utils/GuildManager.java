/*
 * @author Arcal
 */
package com.arcal.bot.discord.utils;

import com.arcal.bot.discord.commands.*;
import java.util.*;
import net.dv8tion.jda.core.entities.*;

/**
 *
 * @author Arcal
 */
public class GuildManager {
    private Guild guild;
    private GuildConfiguration config;
    
    public GuildManager(Guild guild) {
        this.guild = guild;
        this.config = new GuildConfiguration("configs/guild-" + guild.getId());
    }
    
    public GuildConfiguration getConfig() {
        return this.config;
    }
}
