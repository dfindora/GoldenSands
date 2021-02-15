package com.goldensands.config;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class RewardSet
{
    ArrayList<ItemStack> items;

    public RewardSet(ArrayList<ItemStack> items)
    {
        this.items = items;
    }

    public ArrayList<ItemStack> getItems()
    {
        return items;
    }
}
