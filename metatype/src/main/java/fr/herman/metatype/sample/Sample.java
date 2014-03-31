package fr.herman.metatype.sample;

import fr.herman.metatype.annotation.Bean;

@Bean
public class Sample {
    private String hello;

    public String getHello()
    {
        return hello;
    }

    public void setHello(String hello)
    {
        this.hello = hello;

    }
}
