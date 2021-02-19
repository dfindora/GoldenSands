package com.goldensands.util;

import java.util.ArrayList;

public class FixedPage extends Page
{
    public FixedPage(ArrayList<String> entries, int pageLength)
    {
        super(entries, pageLength);
    }

    @Override
    ArrayList<String> buildPage(int pageNumber, ArrayList<String> page)
    {
        for(int i = pageNumber * (this.pageLength - 1); i < pageNumber * pageLength; i++)
        {
            page.add(entries.get(i));
        }
        return page;
    }

    @Override
    int getMaxPages()
    {
        return (int)Math.ceil((double)entries.size() / (double)pageLength);
    }
}
