package com.goldensands.main;

import com.goldensands.util.FixedPage;
import com.goldensands.util.Page;
import com.goldensands.config.ChestLocation;
import com.goldensands.config.Region;
import com.goldensands.config.RewardSet;
import com.goldensands.config.Tier;
import com.goldensands.util.DynamicPage;
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

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class SurvivalCommands implements Listener, CommandExecutor
{
    private final GoldenSandsReborn plugin;
    String main = "goldensands";
    HashMap<CommandSender, Page> senderPageHashMap = new HashMap<>();

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
                        int[] tierNums = new int[100];
                        for(int i = 0; i < 100; i++)
                        {
                            tierNums[i] = (int)(Math.random() * (chestLocation.getTiers().size()));
                        }

                        int[] rewardNums = new int[100];
                        for(int i = 0; i < 100; i++)
                        {
                            rewardNums[i] = (int)(Math.random() * (chestLocation.getTiers().get(tierNums[0]).getRewards().size()));
                        }
                        StringBuilder tnPrint = new StringBuilder("TierNums(" + chestLocation.getTiers().size() + "): [");
                        for(int tierNum : tierNums)
                        {
                            tnPrint.append(tierNum).append(", ");
                        }
                        tnPrint.append("]");
                        System.out.println(tnPrint.toString());

                        StringBuilder rnPrint = new StringBuilder("RewardNums(" + tierNums[0] + ", "
                                + chestLocation.getTiers().get(tierNums[0]).getRewards().size() + "): [");
                        for(int rewardNum : rewardNums)
                        {
                            rnPrint.append(rewardNum).append(", ");
                        }
                        rnPrint.append("]");
                        System.out.println(rnPrint.toString());

                        RewardSet chestInv = chestLocation.getTiers().get(tierNums[0]).getRewards().get(rewardNums[0]);
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
            //gs list - list all regions
            else if(args[0].equals("list"))
            {
                Page page = null;
                if(args.length == 1)
                {
                    //build list
                    ArrayList<String> regions = new ArrayList<>();
                    for (Region region : plugin.getRegionConfig().getRegions())
                    {
                        regions.add(ChatColor.GRAY + region.getName());
                    }
                    page = new FixedPage(regions, 5);
                }
                else
                {
                    sender.sendMessage(ChatColor.RED
                            + "Invalid Syntax. Correct Syntax: /gs list");
                }
                //gs list <region> - list tier range first, then all locations within a region
                if (args.length == 2)
                {
                    Region region = plugin.getRegionConfig().getRegionbyName(args[1]);
                    sender.sendMessage(ChatColor.YELLOW + "Locations for " + region.getName() + "tiers "
                            + region.getTiers().get(0).getNumber() + " to "
                            + region.getTiers().get(region.getTiers().size() - 1).getNumber());

                    //build list
                    ArrayList<String> locations = new ArrayList<>();
                    for (ChestLocation chestLocation : region.getLocations())
                    {
                        Location location = chestLocation.getLocation();
                        locations.add(ChatColor.YELLOW + "(" + location.getX() + ", " + location.getY() + ", "
                                + location.getZ() + ")");
                    }
                    page = new FixedPage(locations, 5);
                }
                else
                {
                    sender.sendMessage(ChatColor.RED
                            + "Invalid Syntax. Correct Syntax: /gs locationlist <region>");
                }
                //gs list <region> <tier> - list rewardsets
                if(args.length == 3 && VarCheck.isInteger(args[2]))
                {
                    //build list
                    Tier tier = plugin.getRegionConfig().getRegionbyName(args[1]).getTiers()
                            .get(Integer.parseInt(args[2]));
                    String seperator = "---%%%---";
                    ArrayList<String> messages = new ArrayList<>();
                    for(RewardSet rewardSet : tier.getRewards())
                    {
                        for(ItemStack item : rewardSet.getItems())
                        {
                            messages.add(ChatColor.AQUA + "Item " + ChatColor.GRAY + item.getTypeId() + ":"
                                    + item.getDurability() + ChatColor.AQUA + ", quantity: "
                                    + ChatColor.GRAY + item.getAmount());
                        }
                        messages.add(seperator);
                    }
                    messages.remove(messages.size() - 1);

                    page = new DynamicPage(messages, 0, seperator);
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "Invalid Syntax. Correct Syntax: " +
                            "/gs rewardslist <region> <tier>");
                }
                if(page != null)
                {
                    for(String pageString : page.pageAt(1))
                    {
                        sender.sendMessage(pageString);
                    }
                    if (senderPageHashMap.containsKey(sender))
                    {
                        senderPageHashMap.replace(sender, page);
                    } else
                    {
                        senderPageHashMap.put(sender, page);
                    }
                }
                return true;
            }
            else if(args[0].equals("page"))
            {
                if(senderPageHashMap.containsKey(sender))
                {
                    if(args[1].equals("previous"))
                    {
                        ArrayList<String> pageStrings = senderPageHashMap.get(sender).previous();
                        for(String pageString : pageStrings)
                        {
                            sender.sendMessage(pageString);
                        }
                    }
                    else if(args[1].equals("next"))
                    {
                        ArrayList<String> pageStrings = senderPageHashMap.get(sender).next();
                        for(String pageString : pageStrings)
                        {
                            sender.sendMessage(pageString);
                        }
                    }
                    else if(VarCheck.isInteger(args[1]))
                    {
                        ArrayList<String> pageStrings = senderPageHashMap.get(sender).pageAt(Integer.parseInt(args[1]));
                        for(String pageString : pageStrings)
                        {
                            sender.sendMessage(pageString);
                        }
                    }
                    else
                    {
                        sender.sendMessage(ChatColor.RED
                                + "Invalid Syntax. Correct Syntax: /gs page <previous|next|page number>");
                    }
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "You have not run a command to page through.");
                }
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
