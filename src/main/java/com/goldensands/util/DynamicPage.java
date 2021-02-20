package com.goldensands.util;

import java.util.ArrayList;

/**
 * Instead of creating pages based on a fixed page length, this instead uses seperators to determine how long a page
 * should be. This allows the user to create pages of varying lengths.
 */
public class DynamicPage extends Page
{
    private String seperator;
    public DynamicPage(ArrayList<String> entries, int pageLength, String seperator)
    {
        super(entries, pageLength);
        this.seperator = seperator;
    }

    @Override
    void buildPage(int pageNumber, ArrayList<String> page)
    {
        //find the start of the page
        int startIndex = 0;
        if(pageNumber > 1)
        {
            int count = 1;
            for(int i = 0; i < entries.size(); i++)
            {
                if(entries.get(i).equals(seperator))
                {
                    count++;
                }
                if(count == pageNumber)
                {
                    startIndex = i + 1;
                    break;
                }
            }
        }

        //build the page
        for(int i = startIndex; i < entries.size() && !entries.get(i).equals(seperator); i++)
        {
            page.add(entries.get(i));
        }
    }

    @Override
    public int getMaxPages()
    {
        int numEntries = 0;
        for(String entry : entries)
        {
            if(entry.equals(seperator))
            {
                numEntries++;
            }
        }
        return numEntries;
    }
}
