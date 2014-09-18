package fr.herman.metatype.sample;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import java.util.Arrays;
import java.util.Collection;
import org.testng.annotations.Test;
import fr.herman.metatype.utils.Metas;

public class TestMetas
{
    @Test
    public void testCollect()
    {
        Sample sample = new Sample();
        sample.setHello("hello");
        SampleChild sampleChild = new SampleChild();
        sampleChild.setHello("world");
        Collection<String> collect = Metas.collect(Arrays.asList(sample, sampleChild), SampleMeta.$.hello);
        assertEquals(2, collect.size());
        assertTrue(collect.contains("hello"));
        assertTrue(collect.contains("world"));
    }

    @Test
    public void testDistinct()
    {
        Sample sample = new Sample();
        sample.setHello("hello");
        SampleChild sampleChild = new SampleChild();
        sampleChild.setHello("world");
        SampleChild sampleChild2 = new SampleChild();
        sampleChild2.setHello("hello");
        Collection<String> collect = Metas.distinct(Arrays.asList(sample, sampleChild, sampleChild2, sample), SampleMeta.$.hello);
        assertEquals(2, collect.size());
        assertTrue(collect.contains("hello"));
        assertTrue(collect.contains("world"));
    }

    @Test
    public void testDefaultValue()
    {
        Sample sample = new Sample();
        Metas.defaultValue(SampleMeta.$.hello, sample, "world");
        assertEquals("world", sample.getHello());
        Metas.defaultValue(SampleMeta.$.hello, sample, "you");
        assertEquals("world", sample.getHello());
    }
}
