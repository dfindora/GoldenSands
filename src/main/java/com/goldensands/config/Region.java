package com.goldensands.config;

import org.bukkit.Location;

import java.util.ArrayList;

public class Region
{
    private final String name;
    private final ArrayList<ChestLocation> locations;
    private final ArrayList<Tier> tiers;

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
            if((int)chestLocation.getLocation().getX() == (int)location.getX()
                    && (int)chestLocation.getLocation().getY() == (int)location.getY()
                    && (int)chestLocation.getLocation().getZ() == (int)location.getZ())
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

    public void addLocation(ChestLocation location)
    {
        locations.add(location);
    }

    public void removeLocation(ChestLocation location)
    {
        locations.remove(location);
    }
}
