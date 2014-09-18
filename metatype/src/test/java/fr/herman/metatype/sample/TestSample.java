package fr.herman.metatype.sample;

import org.testng.Assert;
import org.testng.annotations.Test;
import fr.herman.metatype.model.MetaPropertyGetterSetter;
import fr.herman.metatype.model.method.Getter;
import fr.herman.metatype.model.method.Setter;

public class TestSample
{

    @Test
    @SuppressWarnings("cast")
    public void test(){
        MetaPropertyGetterSetter<Sample, Sample, String> hello = SampleMeta.$.hello;
        Assert.assertNotNull(hello);
        Assert.assertTrue(hello instanceof Getter);
        Assert.assertTrue(hello instanceof Setter);
        Assert.assertEquals("hello", hello.name());

        MetaPropertyGetterSetter<Sample, Sample, ?> custom = SampleMeta.$.custom;
        Assert.assertNotNull(custom);
        Assert.assertTrue(custom instanceof Getter);
        Assert.assertTrue(custom instanceof Setter);
        Assert.assertEquals("custom", custom.name());
    }
}
