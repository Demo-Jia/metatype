package fr.herman.metatype.processor;

public class WordUtils
{
    public static String capitalize(String word)
    {
        return Character.toUpperCase(word.charAt(0)) + word.substring(1);
    }

    public static String unCapitalize(String word)
    {
        return Character.toLowerCase(word.charAt(0)) + word.substring(1);
    }

}
