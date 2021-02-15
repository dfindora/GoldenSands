package com.goldensands.config;

import org.bukkit.Location;

import java.util.ArrayList;

public class ChestLocation
{
    private final Location location;
    ArrayList<Tier> tiers;

    public ChestLocation(Region region, Location location, int tierStart, int tierEnd)
    {
        this.location = location;
        this.tiers = new ArrayList<>();

        for(int i = tierStart; i < tierEnd; i++)
        {
            tiers.add(region.getTiers().get(i));
        }
    }

    public Location getLocation()
    {
        return location;
    }

    public ArrayList<Tier> getTiers()
    {
        return tiers;
    }
}
