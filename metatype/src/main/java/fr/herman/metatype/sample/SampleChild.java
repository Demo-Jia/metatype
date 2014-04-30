package fr.herman.metatype.sample;

import fr.herman.metatype.annotation.Bean;

@Bean
public class SampleChild extends Sample
{
    private String[] stringArray;
    private int[]    intArray;

    public int[] getIntArray()
    {
        return intArray;
    }

    public void setIntArray(int[] intArray)
    {
        this.intArray = intArray;
    }

    public String[] getStringArray()
    {
        return stringArray;
    }

    public void setStringArray(String[] stringArray)
    {
        this.stringArray = stringArray;
    }
}
