package com.goldensands.config;

import java.util.ArrayList;

public class Tier
{
    private final int number;
    private final ArrayList<RewardSet> rewards;

    public Tier(int number)
    {
        this.number = number;
        this.rewards = new ArrayList<>();
    }

    public int getNumber()
    {
        return number;
    }

    public ArrayList<RewardSet> getRewards()
    {
        return rewards;
    }

    public void addRewards(RewardSet rewardSet)
    {
        rewards.add(rewardSet);
    }
}
