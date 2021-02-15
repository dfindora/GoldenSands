package com.goldensands.config;

import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

public class ConfigParser
{
    private final ArrayList<Region> regions;
    private final String dirString = "regions" + File.separator;
    private final Gson gson = new Gson();

    public ConfigParser()
    {
        this.regions = new ArrayList<>();
    }

    public void initialize()
    {
        readRegionsFromFile();
    }

    public void readRegionsFromFile()
    {
        File dir = new File(dirString);
        try
        {
            for (File fileEntry : Objects.requireNonNull(dir.listFiles()))
            {
                regions.add(gson.fromJson(new FileReader(fileEntry), Region.class));
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public void writeRegionsToFile()
    {
        File dir = new File(dirString);
        try
        {
            for(Region region : regions)
            {
                gson.toJson(region, new FileWriter(dir + region.getName() + ".json"));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public ArrayList<Region> getRegions()
    {
        return regions;
    }

    public void addRegion(Region region)
    {
        regions.add(region);
        writeRegionsToFile();
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
