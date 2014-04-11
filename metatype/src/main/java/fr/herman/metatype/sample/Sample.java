package fr.herman.metatype.sample;

import java.util.Collection;
import java.util.Date;
import com.mysema.query.annotations.QueryEntity;
import fr.herman.metatype.annotation.Bean;

@Bean
@QueryEntity
public class Sample
{
    private String       hello;
    private final String whithoutSetter = "noSetter";

    private int          truc;

    private Date         date;

    private Collection<Boolean> booleans;

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

    public Collection<Boolean> getBooleans()
    {
        return booleans;
    }

    public void setBooleans(Collection<Boolean> bools)
    {
        booleans = bools;
    }
}
