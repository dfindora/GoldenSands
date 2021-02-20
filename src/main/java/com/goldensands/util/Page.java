package com.goldensands.util;

import org.bukkit.ChatColor;

import java.util.ArrayList;

/**
 * Utility class for creating commands that have multiple pages.
 */
public abstract class Page
{
    protected ArrayList<String> entries;
    protected int pageLength;
    protected int currentPage;

    public Page(ArrayList<String> entries, int pageLength)
    {
        this.entries = entries;
        this.pageLength = pageLength;
        currentPage = 1;
    }

    public ArrayList<String> previous()
    {
        ArrayList<String> page = new ArrayList<>();
        if(currentPage > 1)
        {
            currentPage--;
            page.add(ChatColor.AQUA + "Showing page " + currentPage + " of " + getMaxPages());
            buildPage(currentPage, page);
        }
        else
        {
            page.add(ChatColor.RED + "You are already on the first page.");
        }

        return page;
    }

    public ArrayList<String> next()
    {
        ArrayList<String> page = new ArrayList<>();
        if(currentPage < getMaxPages())
        {
            currentPage++;
            page.add(ChatColor.AQUA + "Showing page " + currentPage + " of " + getMaxPages());
            buildPage(currentPage, page);
        }
        else
        {
            page.add(ChatColor.RED + "There is no next page.");
        }

        return page;
    }

    public ArrayList<String> pageAt(int pageNumber)
    {
        ArrayList<String> page = new ArrayList<>();
        if(pageNumber <= getMaxPages())
        {
            currentPage = pageNumber;
            page.add(ChatColor.AQUA + "Showing page " + currentPage + " of " + getMaxPages());
            buildPage(currentPage, page);
        }
        else
        {
            page.add(ChatColor.RED + "That page does not exist.");
        }

        return page;
    }

    abstract void buildPage(int pageNumber, ArrayList<String> page);

    public abstract int getMaxPages();
}
