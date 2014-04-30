package fr.herman.metatype.sample;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import org.testng.Assert;
import org.testng.annotations.Test;
import fr.herman.metatype.utils.MetaUtils;

public class SampleTest
{
    @Test
    public void test()
    {
        Sample sample = new Sample();
        sample.setHello("world");
        Sample sample2 = new Sample();
        MetaUtils.applyTo(sample, sample2, SampleMeta.hello, SampleMeta.date);
        Assert.assertEquals(sample.getHello(), sample2.getHello());
        Assert.assertEquals(sample.getDate(), sample2.getDate());

        Collection<Sample> samples = Arrays.asList(sample, sample2);
        Iterator<String> it = MetaUtils.collect(samples, SampleMeta.hello).iterator();
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals("world", it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals("world", it.next());
        Assert.assertFalse(it.hasNext());

    }
}
