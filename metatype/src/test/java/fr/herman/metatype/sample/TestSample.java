package fr.herman.metatype.sample;

import org.testng.Assert;
import org.testng.annotations.Test;
import fr.herman.metatype.model.method.Getter;
import fr.herman.metatype.model.method.Setter;
import fr.herman.metatype.sample.SampleMeta.CustomProperty;
import fr.herman.metatype.sample.SampleMeta.HelloProperty;

public class TestSample
{

    @Test
    @SuppressWarnings("cast")
    public void test(){
        HelloProperty hello = SampleMeta.hello;
        Assert.assertNotNull(hello);
        Assert.assertTrue(hello instanceof Getter);
        Assert.assertTrue(hello instanceof Setter);
        Assert.assertEquals("hello", hello.name());

        CustomProperty custom = SampleMeta.custom;
        Assert.assertNotNull(custom);
        Assert.assertTrue(custom instanceof Getter);
        Assert.assertTrue(custom instanceof Setter);
        Assert.assertEquals("custom", custom.name());
    }
}
