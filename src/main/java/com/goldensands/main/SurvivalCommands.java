package com.goldensands.main;

import com.goldensands.config.ChestLocation;
import com.goldensands.config.Region;
import com.goldensands.config.RewardSet;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import com.goldensands.util.VarCheck;

import java.util.ArrayList;

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
                if(args.length == 3 && VarCheck.isInteger(args[2]))
                {
                    Region region = new Region(args[1], Integer.parseInt(args[2]));
                    plugin.getRegionConfig().addRegion(region);
                    sender.sendMessage(ChatColor.GREEN + "Created a region by the name " + args[1] + ".");
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "Invalid syntax. Correct Syntax: /" + command.getName()
                            + " create <name> <max-tiers>");
                }
                return true;
            }
            else if(args[0].equals("reset"))
            {
                if(args.length == 2)
                {
                    Region region = plugin.getRegionConfig().getRegionbyName(args[1]);
                    for(ChestLocation chestLocation : region.getLocations())
                    {
                        Location location = chestLocation.getLocation();
                        location.getBlock().setType(Material.CHEST);
                        Chest chest = (Chest)location.getBlock().getState();
                        if(!hasEmptyInventory(chest))
                        {
                            chest.getInventory().clear();
                        }
                        int tierNum = (int)(Math.random() * (chestLocation.getTiers().size()));
                        int rewardNum = (int)(Math.random() * (chestLocation.getTiers().get(tierNum).getRewards().size()));
                        RewardSet chestInv = chestLocation.getTiers().get(tierNum).getRewards().get(rewardNum);
                        for(ItemStack item : chestInv.getItems())
                        {
                            chest.getInventory().addItem(item);
                        }
                    }
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
                            plugin.getRegionConfig().writeRegionsToFile();
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
                    Location location = ((Player)sender).getLocation();
                    ChestLocation chestLocation = new ChestLocation(plugin.getRegionConfig().getRegionbyName(args[1]),
                            location, Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                    plugin.getRegionConfig().getRegionbyName(args[1]).addLocation(chestLocation);
                    plugin.getRegionConfig().writeRegionsToFile();
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
                        ArrayList<ItemStack> items = new ArrayList<>();
                        for(ItemStack item : ((Player)sender).getInventory().getContents())
                        {
                            if(item != null)
                            {
                                items.add(item);
                            }
                        }
                        RewardSet rewardSet = new RewardSet(items);

                        plugin.getRegionConfig().getRegionbyName(args[1]).getTiers()
                                .get(Integer.parseInt(args[2]) - 1).addRewards(rewardSet);
                        plugin.getRegionConfig().writeRegionsToFile();
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
    public boolean hasEmptyInventory(InventoryHolder inventory)
    {
        for(ItemStack it : inventory.getInventory().getContents())
        {
            if(it != null) return false;
        }
        return true;
    }
}
