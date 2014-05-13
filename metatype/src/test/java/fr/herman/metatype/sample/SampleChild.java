package fr.herman.metatype.sample;

import fr.herman.metatype.annotation.Bean;

@Bean
public class SampleChild extends Sample
{
    private String[] stringArray;
    private int[]    intArray;
    private String   childString;

    private Sample   sample;

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

    public Sample getSample()
    {
        return sample;
    }

    public void setSample(Sample sample)
    {
        this.sample = sample;
    }

    public String getChildString()
    {
        return childString;
    }

    public void setChildString(String childString)
    {
        this.childString = childString;
    }
}
