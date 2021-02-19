package com.goldensands.util;

import java.util.ArrayList;

public class FixedPage extends Page
{
    public FixedPage(ArrayList<String> entries, int pageLength)
    {
        super(entries, pageLength);
    }

    @Override
    void buildPage(int pageNumber, ArrayList<String> page)
    {
        for(int i = this.pageLength * (pageNumber - 1); i < this.pageLength * pageNumber && i < entries.size(); i++)
        {
            page.add(entries.get(i));
        }
    }

    @Override
    int getMaxPages()
    {
        return (int)Math.ceil((double)entries.size() / (double)pageLength);
    }
}
