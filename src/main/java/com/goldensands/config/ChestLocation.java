package com.goldensands.config;

import org.bukkit.Location;

import java.util.ArrayList;

public class ChestLocation
{
    private Region region;
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

    /**
     * Deserialization Constructor. Use this if the region is not already created.
     * @param location - the Location of the chest in the world.
     * @param tierStart - the minimum reward tier for this ChestLocation.
     * @param tierEnd - the maximum reward tier for this ChestLocation.
     */
    public ChestLocation(Location location, int tierStart, int tierEnd)
    {
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

    /**
     * used to set the region when the region isn't already set by the constructor. This occurs when deserializing a
     * Region.
     * @param region - The region that this ChestLocation is a component of.
     */
    public void setRegion(Region region)
    {
        this.region = region;
    }
}
