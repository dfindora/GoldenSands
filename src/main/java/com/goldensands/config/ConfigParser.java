package com.goldensands.config;

import com.goldensands.main.GoldenSandsReborn;
import com.google.gson.*;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Objects;

public class ConfigParser
{
    private final GoldenSandsReborn plugin;
    private final ArrayList<Region> regions;
    private final String dirString;

    public ConfigParser(GoldenSandsReborn plugin)
    {
        this.plugin = plugin;
        this.regions = new ArrayList<>();
        this.dirString = plugin.getDataFolder() + File.separator + "regions";
    }

    public void initialize()
    {
        File dir = new File(dirString);
        if(!dir.exists())
        {
            dir.mkdir();
        }
        readRegionsFromFile();
    }

    public void readRegionsFromFile()
    {
        JsonDeserializer<Region> deserializer = (json, typeOfT, context) ->
        {
            JsonObject jsonRegion = json.getAsJsonObject();
            JsonArray jsonChestLocations = jsonRegion.getAsJsonArray("locations");
            ArrayList<ChestLocation> chestLocations = new ArrayList<>();
            for(JsonElement jsonLocation : jsonChestLocations)
            {
                ChestLocation chestLocation
                        = new ChestLocation(new Location(
                                plugin.getServer().getWorld(((JsonObject)jsonLocation).get("world").getAsString()),
                                ((JsonObject)jsonLocation).get("x").getAsDouble(),
                                ((JsonObject)jsonLocation).get("y").getAsDouble(),
                                ((JsonObject)jsonLocation).get("z").getAsDouble()),
                        ((JsonObject)jsonLocation).get("tierStart").getAsInt(),
                        ((JsonObject)jsonLocation).get("tierEnd").getAsInt());
                chestLocations.add(chestLocation);
            }

            JsonArray jsonTiers = jsonRegion.getAsJsonArray("tiers");
            ArrayList<Tier> tiers = new ArrayList<>();
            for(JsonElement jsonTier : jsonTiers)
            {
                JsonArray jsonRewardSets = jsonTier.getAsJsonObject().get("rewards").getAsJsonArray();
                ArrayList<RewardSet> rewardSets = new ArrayList<>();
                for(JsonElement jsonRewardSet : jsonRewardSets)
                {
                    JsonArray jsonItems = jsonRewardSet.getAsJsonArray();
                    ArrayList<ItemStack> items = new ArrayList<>();
                    for(JsonElement jsonItem : jsonItems)
                    {
                        items.add(new ItemStack(jsonItem.getAsJsonObject().get("type").getAsInt(),
                                jsonItem.getAsJsonObject().get("amount").getAsInt(),
                                jsonItem.getAsJsonObject().get("durability").getAsShort()));
                    }
                    rewardSets.add(new RewardSet(items));
                }
                tiers.add(new Tier(jsonTier.getAsJsonObject().get("number").getAsInt(), rewardSets));
            }

            Region region = new Region(jsonRegion.get("name").getAsString(), chestLocations, tiers);
            for(ChestLocation chestLocation : region.getLocations())
            {
                chestLocation.setRegion(region);
            }
            return region;
        };
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Region.class, deserializer);
        Gson gson = gsonBuilder.create();
        File dir = new File(dirString);
        try
        {
            for (File fileEntry : Objects.requireNonNull(dir.listFiles()))
            {
                FileReader fr = new FileReader(fileEntry);
                regions.add(gson.fromJson(fr, Region.class));
                fr.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void writeRegionsToFile()
    {
        JsonSerializer<Region> serializer = (src, typeOfSrc, context) ->
        {
            JsonObject jsonRegion = new JsonObject();

            jsonRegion.addProperty("name", src.getName());
            JsonArray jsonChestLocations = new JsonArray();
            for(ChestLocation location : src.getLocations())
            {
                JsonObject jsonChestLocation = new JsonObject();
                jsonChestLocation.addProperty("world", location.getLocation().getWorld().getName());
                jsonChestLocation.addProperty("x", location.getLocation().getX());
                jsonChestLocation.addProperty("y", location.getLocation().getY());
                jsonChestLocation.addProperty("z", location.getLocation().getZ());
                jsonChestLocation.addProperty("tierStart", location.getTiers().get(0).getNumber());
                jsonChestLocation.addProperty("tierEnd",
                        location.getTiers().get(location.getTiers().size() - 1).getNumber());
                jsonChestLocations.add(jsonChestLocation);
            }
            jsonRegion.add("locations", jsonChestLocations);

            JsonArray jsonTiers = new JsonArray();
            for(Tier tier : src.getTiers())
            {
                JsonObject jsonTier = new JsonObject();
                jsonTier.addProperty("number", tier.getNumber());
                JsonArray jsonRewardSets = new JsonArray();
                for(RewardSet rewardSet : tier.getRewards())
                {
                    JsonArray jsonItems = new JsonArray();
                    for(ItemStack item : rewardSet.getItems())
                    {
                        JsonObject jsonItem = new JsonObject();
                        jsonItem.addProperty("type", item.getTypeId());
                        jsonItem.addProperty("durability", item.getDurability());
                        jsonItem.addProperty("amount", item.getAmount());
                        jsonItems.add(jsonItem);
                    }
                    jsonRewardSets.add(jsonItems);
                }
                jsonTier.add("rewards", jsonRewardSets);
                jsonTiers.add(jsonTier);
            }
            jsonRegion.add("tiers", jsonTiers);

            return jsonRegion;
        };
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Region.class, serializer);
        Gson gson = gsonBuilder.create();
        String regionName = "";
        File dir = new File(dirString);
        try
        {
            for(Region region : regions)
            {
                regionName = region.getName();
                FileWriter fw = new FileWriter(dir + File.separator + regionName  + ".json");
                gson.toJson(region, fw);
                fw.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.err.println("File path: " + dir + File.separator + regionName  + ".json");
        }
    }

    public boolean deleteRegionFile(String regionName)
    {
        Region region = getRegionbyName(regionName);
        regions.remove(region);
        try
        {
            File file = new File(dirString + File.separator + regionName  + ".json");
            return Files.deleteIfExists(file.toPath());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
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
        assert true : "region " + name + " not found";
        return null;
    }
}