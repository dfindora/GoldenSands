package com.goldensands.config;

import org.bukkit.Location;

import java.util.ArrayList;

public class ChestLocation
{
    private final Region region;
    private final Location location;
    private final int tierStart;
    private final int tierEnd;

    public ChestLocation(Region region, Location location, int tierStart, int tierEnd)
    {
        this.region = region;
        this.location = location;
        this.tierStart = tierStart;
        this.tierEnd = tierEnd;
    }

    public Location getLocation()
    {
        return location;
    }

    public ArrayList<Tier> getTiers()
    {
        ArrayList<Tier> tiers = new ArrayList<>();
        for(int i = tierStart; i < tierEnd; i++)
        {
            tiers.add(region.getTiers().get(i));
        }
        return tiers;
    }
}
