package com.goldensands.main;

import com.goldensands.util.FixedPage;
import com.goldensands.util.Page;
import com.goldensands.config.ChestLocation;
import com.goldensands.config.Region;
import com.goldensands.config.RewardSet;
import com.goldensands.config.Tier;
import com.goldensands.util.DynamicPage;
import com.google.gson.internal.$Gson$Preconditions;
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
            //gs addregion <name> <max-tiers>
            if(args[0].equals("addregion"))
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
                            + " addregion <name> <max-tiers>");
                }
                return true;
            }
            //gs removeregion <region>
            if(args[0].equals("removeregion"))
            {
                boolean deleted = plugin.getRegionConfig().deleteRegionFile(args[1]);
                if(deleted)
                {
                    sender.sendMessage(ChatColor.GREEN + "Region " + args[1] + " successfully deleted.");
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "Region " + args[1] + " was not successfully deleted.");
                }
            }
            //gs reset <region>
            else if(args[0].equals("reset"))
            {
                if(args.length == 2)
                {
                    Region region = plugin.getRegionConfig().getRegionbyName(args[1]);
                    if(region != null && !region.areAllTiersEmpty())
                    {
                        if(region.getLocations().size() > 0)
                        {
                            for (ChestLocation chestLocation : region.getLocations())
                            {
                                Location location = chestLocation.getLocation();
                                location.getBlock().setType(Material.CHEST);
                                Chest chest = (Chest) location.getBlock().getState();
                                if (!hasEmptyInventory(chest))
                                {
                                    chest.getInventory().clear();
                                }

                                Tier tier = null;
                                int tierNum = -1;
                                while (tierNum < 0 || tier.getRewards().size() == 0)
                                {
                                    tierNum = (int) (Math.random() * (chestLocation.getTiers().size()));
                                    tier = chestLocation.getTiers().get(tierNum);
                                }

                                int rewardNum = (int) (Math.random() * (tier.getRewards().size()));
                                RewardSet chestInv = tier.getRewards().get(rewardNum);
                                for (ItemStack item : chestInv.getItems())
                                {
                                    chest.getInventory().addItem(item);
                                }
                            }
                        }
                        else
                        {
                            sender.sendMessage(ChatColor.RED + "Region " + region.getName() + " does not have any"
                            + " locations set, so there is nothing to reset.");
                        }
                    }
                    else if(region != null && region.areAllTiersEmpty())
                    {
                        sender.sendMessage(ChatColor.RED + "Region " + region.getName() + " does not have any "
                                + "rewards set for any tiers, so there is nothing to reset.");
                    }
                    else
                    {
                        sender.sendMessage(ChatColor.RED + "region " + args[1] + " not found.");
                    }
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "Invalid syntax. Correct Syntax: /" + command.getName()
                            + " reset <region>");
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
                    if(regions.size() > 0)
                    {
                        page = new FixedPage(regions, 5);
                    }
                    else
                    {
                        sender.sendMessage(ChatColor.RED + "No regions have been created yet.");
                    }
                }
                //gs list <region> - list tier range first, then all locations within a region
                else if (args.length == 2)
                {
                    Region region = plugin.getRegionConfig().getRegionbyName(args[1]);
                    if(region != null)
                    {
                        sender.sendMessage(ChatColor.YELLOW + "Locations for " + region.getName() + " tiers "
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
                        sender.sendMessage(ChatColor.RED + "region " + args[1] + " not found.");
                    }
                }
                //gs list <region> <tier> - list rewardsets
                else if(args.length == 3 && VarCheck.isInteger(args[2]))
                {
                    //build list
                    Region region = plugin.getRegionConfig().getRegionbyName(args[1]);
                    if(region != null)
                    {
                        if(Integer.parseInt(args[2]) > 0 && Integer.parseInt(args[2]) <= region.getTiers().size())
                        {
                            Tier tier = region.getTiers().get(Integer.parseInt(args[2]));
                            String seperator = "---%%%---";
                            ArrayList<String> messages = new ArrayList<>();
                            for (RewardSet rewardSet : tier.getRewards())
                            {
                                for (ItemStack item : rewardSet.getItems())
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
                            sender.sendMessage(ChatColor.RED + "The tier provided is not within this region's tier "
                                    + "range. The tier must be between 1 and " + region.getTiers().size() + ".");
                        }
                    }
                    else
                    {
                        sender.sendMessage(ChatColor.RED + "region " + args[1] + " not found.");
                    }
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "Invalid Syntax. Correct Syntax: " +
                            "/gs list <region> <tier>");
                }
                //print and add to map of command senders
                if(page != null)
                {
                    sender.sendMessage(ChatColor.AQUA + "Showing page " + 1 + " of " + page.getMaxPages());
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
                            sender.sendMessage(ChatColor.GREEN + "Removed location ("
                                    + chestLocation.getLocation().getBlockX() + ", "
                                    + chestLocation.getLocation().getBlockY() + ", "
                                    + chestLocation.getLocation().getBlockZ() + ") from region "
                                    + region.getName() + ".");
                        }
                        else
                        {
                            sender.sendMessage(ChatColor.RED + "the location ("
                                    + chestLocation.getLocation().getBlockX() + ", "
                                    + chestLocation.getLocation().getBlockY() + ", "
                                    + chestLocation.getLocation().getBlockZ() + ") is not contained within the region "
                                    + region.getName() + ".");
                        }
                    }
                    else
                    {
                        sender.sendMessage(ChatColor.RED + "region " + args[1] + " not found.");
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
            //gs addloc <region> <tier-start> <tier-end>
            else if(args[0].equals("addloc"))
            {
                if(args.length == 4 && VarCheck.isInteger(args[2]) && VarCheck.isInteger(args[3]))
                {
                    Region region = plugin.getRegionConfig().getRegionbyName(args[1]);
                    if(region != null)
                    {
                        if(Integer.parseInt(args[2]) >= 1 && Integer.parseInt(args[3])
                                <= region.getTiers().get(region.getTiers().size() - 1).getNumber())
                        {
                            Location location = ((Player) sender).getLocation();
                            ChestLocation chestLocation = new ChestLocation(plugin.getRegionConfig().getRegionbyName(args[1]),
                                    location, Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                            region.addLocation(chestLocation);
                            plugin.getRegionConfig().writeRegionsToFile();
                            sender.sendMessage(ChatColor.GREEN + "added location ("
                                    + chestLocation.getLocation().getBlockX() + ", "
                                    + chestLocation.getLocation().getBlockY() + ", "
                                    + chestLocation.getLocation().getBlockZ() + ") to region "
                                    + region.getName() + ".");
                        }
                        else if(Integer.parseInt(args[2]) < 1)
                        {
                            sender.sendMessage(ChatColor.RED + "the minimum tier must be at least 1.");
                        }
                        else
                        {
                            sender.sendMessage(ChatColor.RED + "the maximum tier number cannot be greater than the "
                                    + "maximum tier for this region, which is"
                                    + region.getTiers().get(region.getTiers().size() - 1).getNumber() + ".");
                        }
                    }
                    else
                    {
                        sender.sendMessage(ChatColor.RED + "region " + args[1] + " not found.");
                    }
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "Invalid syntax. Correct Syntax: /" + command.getName()
                            + " addloc <region> <tier-start> <tier-end>");
                }
                return true;
            }
            //gs removereward <region> <tier> <index>
            else if(args[0].equals("removereward"))
            {
                if(args.length == 4 && VarCheck.isInteger(args[2]) && VarCheck.isInteger(args[3]))
                {
                    Region region = plugin.getRegionConfig().getRegionbyName(args[1]);
                    if(region != null)
                    {
                        if(Integer.parseInt(args[2]) <= region.getTiers().size())
                        {
                            Tier tier = region.getTiers().get(Integer.parseInt(args[2]) - 1);
                            if(Integer.parseInt(args[3]) <= tier.getRewards().size())
                            {
                                tier.getRewards().remove(Integer.parseInt(args[3]) - 1);
                                sender.sendMessage(ChatColor.GREEN + "removed the reward set from tier "
                                        + tier.getNumber() + " in region " + region.getName() + ".");
                            }
                            else
                            {
                                sender.sendMessage(ChatColor.RED + "The reward index exceeded the number of rewards" +
                                        "for this tier. This tier contains " + tier.getRewards().size() + " rewards.");
                            }
                        }
                        else
                        {
                            sender.sendMessage(ChatColor.RED + "tier " + args[2] + " does not exist. This region's" +
                                    "max tier is " + region.getTiers().get(region.getTiers().size() - 1).getNumber() + ".");
                        }
                    }
                    else
                    {
                        sender.sendMessage(ChatColor.RED + "region " + args[1] + " not found.");
                    }
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "Invalid syntax. Correct Syntax: /" + command.getName()
                            + " removereward <region> <tier> <index>");
                }

                return true;
            }
            //gs addreward <region> <tier>
            else if(args[0].equals("addreward"))
            {
                if(args.length == 3 && VarCheck.isInteger(args[2]))
                {
                    if(!hasEmptyInventory((Player)sender))
                    {
                        Region region = plugin.getRegionConfig().getRegionbyName(args[1]);
                        if(region != null)
                        {
                            if(Integer.parseInt(args[2]) <= region.getTiers().size())
                            {
                                Tier tier = region.getTiers().get(Integer.parseInt(args[2]) - 1);
                                ArrayList<ItemStack> items = new ArrayList<>();
                                for (ItemStack item : ((Player) sender).getInventory().getContents())
                                {
                                    if (item != null)
                                    {
                                        items.add(item);
                                    }
                                }
                                RewardSet rewardSet = new RewardSet(items);

                                tier.addRewards(rewardSet);
                                plugin.getRegionConfig().writeRegionsToFile();
                                sender.sendMessage(ChatColor.GREEN + "added the reward set from tier "
                                        + tier.getNumber() + " in region " + region.getName() + ".");
                            }
                            else
                            {
                                sender.sendMessage(ChatColor.RED + "tier " + args[2] + " does not exist. This "
                                        + "region's max tier is "
                                        + region.getTiers().get(region.getTiers().size() - 1).getNumber() + ".");
                            }
                        }
                        else
                        {
                            sender.sendMessage(ChatColor.RED + "region " + args[1] + " not found.");
                        }
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
