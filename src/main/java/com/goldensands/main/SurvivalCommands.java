package com.goldensands.main;

import com.goldensands.config.ChestLocation;
import com.goldensands.config.Region;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
    private final GoldenSandsReborn plugin;
    String main = "goldensands";

    public SurvivalCommands(GoldenSandsReborn plugin)
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
                return true;
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
                return true;
            }
            else if(args[0].equals("list"))
            {
                return true;
            }
            else if(args[0].equals("help"))
            {
                return true;
            }
            else if(args[0].equals("removeloc"))
            {
                if(args.length == 5 && VarCheck.isInteger(args[2]) && VarCheck.isInteger(args[3])
                        && VarCheck.isInteger(args[4]))
                {
                    Location location = new Location(((Player)sender).getWorld(), Double.parseDouble(args[2]),
                            Double.parseDouble(args[3]), Double.parseDouble(args[4]));
                    Region region = plugin.getRegionConfig().getRegionbyName(args[1]);
                    if(region != null)
                    {
                        ChestLocation chestLocation = region.getLocation(location);
                        if(chestLocation != null)
                        {
                            region.removeLocation(chestLocation);
                        }
                        else
                        {
                            sender.sendMessage(ChatColor.RED + "This location is not contained within the region.");
                        }
                    }
                    else
                    {
                        sender.sendMessage(ChatColor.RED + "Invalid Region.");
                    }
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "Invalid syntax. Correct Syntax: /" + command.getName()
                            + " removeloc <region> <x> <y> <z>");
                }
                return true;
            }
            //player check
            else if (!(sender instanceof Player))
            {
                sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
                return true;
            }
            else if(args[0].equals("addloc"))
            {
                if(args.length == 4 && VarCheck.isInteger(args[2]) && VarCheck.isInteger(args[3]))
                {

                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "Invalid syntax. Correct Syntax: /" + command.getName()
                            + " addloc <region> <tier-start> <tier-end>");
                }
                return true;
            }
            else if(args[0].equals("addreward"))
            {
                if(args.length == 3 && VarCheck.isInteger(args[2]))
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
                            + " addreward <region> <tier>");
                }
                return true;
            }
        }
        else
        {
            sender.sendMessage(ChatColor.RED + command.getPermissionMessage());
            return true;
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
