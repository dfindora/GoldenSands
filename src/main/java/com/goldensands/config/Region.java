package com.goldensands.config;

import org.bukkit.Location;

import java.util.ArrayList;

public class Region
{
    private final String name;
    private final ArrayList<ChestLocation> locations;
    private final ArrayList<Tier> tiers;

    /**
     * Generates a blank Region.
     * @param name - name of the region.
     * @param maxTiers - maximum number of tiers. The region will generate empty tiers from 1 - maxTiers.
     */
    public Region(String name, int maxTiers)
    {
        this.name = name;
        this.locations = new ArrayList<>();
        this.tiers = new ArrayList<>();
        for (int i = 0; i < maxTiers; i++)
        {
            tiers.add(new Tier(i + 1));
        }
    }

    /**
     * Deserialization constructor.
     * @param name - name of the region.
     * @param locations - all the locations currently in the region.
     * @param tiers - all of the tiers with their respective RewardSets currently in the region.
     */
    public Region(String name, ArrayList<ChestLocation> locations, ArrayList<Tier> tiers)
    {
        this.name = name;
        this.locations = locations;
        this.tiers = tiers;
    }

    public String getName()
    {
        return name;
    }

    public ArrayList<ChestLocation> getLocations()
    {
        return locations;
    }

    public ChestLocation getLocation(Location location)
    {
        for(ChestLocation chestLocation : locations)
        {
            if(chestLocation.getLocation().getBlockX() == location.getBlockX()
                    && chestLocation.getLocation().getBlockY() == location.getBlockY()
                    && chestLocation.getLocation().getBlockZ() == location.getBlockZ())
            {
                return chestLocation;
            }
        }
        return null;
    }

    public ArrayList<Tier> getTiers()
    {
        return tiers;
    }

    public boolean areAllTiersEmpty()
    {
        for(Tier tier : tiers)
        {
            if(tier.getRewards().size() > 0)
            {
                return false;
            }
        }
        return true;
    }

    public void addLocation(ChestLocation location)
    {
        locations.add(location);
    }

    public void removeLocation(ChestLocation location)
    {
        locations.remove(location);
    }
}
