package com.goldensands.util;

public class VarCheck
{
    /**
     * Checks if a string is parsable as an integer.
     *
     * @param str - string to be parsed
     * @return - True if the string is parsable as an integer.
     */
    public static boolean isInteger(String str)
    {
        if (str == null)
        {
            return false;
        }
        //string is empty
        int length = str.length();
        if (length == 0)
        {
            return false;
        }
        int i = 0;
        //string is just a negative sign
        if (str.charAt(0) == '-')
        {
            if (length == 1)
            {
                return false;
            }
            i = 1;
        }
        //check if any character is not a number
        for (; i < length; i++)
        {
            char c = str.charAt(i);
            if (c < '0' || c > '9')
            {
                return false;
            }
        }
        return true;
    }
}
