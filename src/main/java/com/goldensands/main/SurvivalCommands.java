package com.goldensands.main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class SurvivalCommands implements Listener, CommandExecutor
{
    private Plugin plugin;
    public SurvivalCommands(Plugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings)
    {
        return false;
    }
}
