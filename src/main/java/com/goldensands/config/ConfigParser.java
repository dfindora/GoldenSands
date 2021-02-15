package com.goldensands.config;

import java.util.ArrayList;

public class ConfigParser
{
    private final ArrayList<Region> regions;

    public ConfigParser()
    {
        this.regions = new ArrayList<>();
    }

    public ArrayList<Region> getRegions()
    {
        return regions;
    }

    public Region getRegionbyName(String name)
    {
        for(Region region : regions)
        {
            if(region.getName().equals(name))
            {
                return region;
            }
        }
        return null;
    }
}
