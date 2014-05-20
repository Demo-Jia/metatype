package fr.herman.metatype.sample;

import java.util.Collection;
import java.util.Date;
import fr.herman.metatype.annotation.MetaGetter;
import fr.herman.metatype.annotation.MetaSetter;

public class Sample
{
    private String       hello;
    private final String whithoutSetter = "noSetter";

    private int          truc;

    private Date         date;

    private Collection<Boolean> booleans;

    private Object              customized;

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

    @MetaGetter("custom")
    public Object getCustomized()
    {
        return customized;
    }

    @MetaSetter("custom")
    public void setCustomized(Object customized)
    {
        this.customized = customized;
    }
}
