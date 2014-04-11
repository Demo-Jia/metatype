package fr.herman.metatype.sample;

import fr.herman.metatype.annotation.Bean;

@Bean
public class SampleChild extends Sample
{
    private String[] stringArray;

    public String[] getStringArray()
    {
        return stringArray;
    }

    public void setStringArray(String[] stringArray)
    {
        this.stringArray = stringArray;
    }
}
