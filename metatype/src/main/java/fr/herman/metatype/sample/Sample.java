package fr.herman.metatype.sample;

import java.util.Collection;
import java.util.Date;
import fr.herman.metatype.annotation.Bean;

@Bean
public class Sample
{
    private String       hello;
    private final String whithoutSetter = "noSetter";

    private int          truc;

    private Date         date;

    private Collection<Boolean> bools;

    public String getHello()
    {
        return hello;
    }

    public void setHello(String hello)
    {
        this.hello = hello;
    }

    public String getWhithoutSetter()
    {
        return whithoutSetter;
    }

    public int getTruc()
    {
        return truc;
    }

    public void setTruc(int truc)
    {
        this.truc = truc;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public Collection<Boolean> getBools()
    {
        return bools;
    }

    public void setBools(Collection<Boolean> bools)
    {
        this.bools = bools;
    }
}
