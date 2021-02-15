package com.goldensands.main;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import com.goldensands.util.VarCheck;

public class SurvivalCommands implements Listener, CommandExecutor
{
    private Plugin plugin;
    String main = "goldensands";

    public SurvivalCommands(Plugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args)
    {
        //main command
        if(command.getName().toLowerCase().equals(main) && sender.hasPermission(command.getPermission()))
        {
            if(args[0].equals("create"))
            {
                if(args.length == 2)
                {

                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "Invalid syntax. Correct Syntax: /" + command.getName()
                            + " create <name>");
                }
            }
            else if(args[0].equals("reset"))
            {
                if(args.length == 2)
                {

                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "Invalid syntax. Correct Syntax: /" + command.getName()
                            + " reset <name>");
                }
            }
            else if(args[0].equals("list"))
            {

            }
            else if(args[0].equals("help"))
            {

            }
            //player check
            else if (!(sender instanceof Player))
            {
                sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
                return true;
            }
            else if(args[0].equals("addloc"))
            {
                if(args.length == 3 && VarCheck.isInteger(args[1]) && VarCheck.isInteger(args[2]))
                {

                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "Invalid syntax. Correct Syntax: /" + command.getName()
                            + " addloc <tier-start> <tier-end>");
                }
            }
            else if(args[0].equals("addreward"))
            {
                if(args.length == 2 && VarCheck.isInteger(args[1]))
                {
                    if(!hasEmptyInventory((Player)sender))
                    {

                    }
                    else
                    {
                        sender.sendMessage(ChatColor.RED + "Put items in your inventory to create a reward set!");
                    }
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "Invalid syntax. Correct Syntax: /" + command.getName()
                            + " addreward <tier>");
                }
            }
        }
        return false;
    }
    public boolean hasEmptyInventory(Player player)
    {
        for(ItemStack it : player.getInventory().getContents())
        {
            if(it != null) return false;
        }
        return true;
    }
}
