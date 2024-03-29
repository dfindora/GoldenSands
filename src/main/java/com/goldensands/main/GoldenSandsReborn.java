package com.goldensands.main;

import com.goldensands.config.ConfigParser;
import org.bukkit.plugin.java.JavaPlugin;

public class GoldenSandsReborn extends JavaPlugin
{
    //Main command handler.
    private final SurvivalCommands survivalCommands = new SurvivalCommands(this);
    private final ConfigParser configParser = new ConfigParser(this);

    @Override
    public void onEnable()
    {
        getCommand(survivalCommands.main).setExecutor(survivalCommands);
        configParser.initialize();
    }

    @Override
    public void onDisable()
    {

    }

    public ConfigParser getRegionConfig()
    {
        return configParser;
    }
}
