package fr.herman.metatype.sample;

import static org.testng.AssertJUnit.assertEquals;
import org.testng.Assert;
import org.testng.annotations.Test;
import fr.herman.metatype.model.method.Getter;
import fr.herman.metatype.utils.ChainedGetter;

public class TestChainedGetter
{
    @Test
    public void test()
    {
        Sample sample = new Sample();
        sample.setHello("world");
        SampleChild child = new SampleChild();
        child.setSample(sample);
        String value = ChainedGetter.from(SampleChildMeta.sample).to(SampleMeta.hello).getValue(child);
        assertEquals("world", value);
    }

    @Test
    public void testNull()
    {
        SampleChild child = new SampleChild();
        Assert.assertNull(child.getSample());
        ChainedGetter<SampleChild, SampleChild, Sample> getter = ChainedGetter.from(SampleChildMeta.sample);
        Assert.assertNull(getter.getValue(child));
        Getter<SampleChild, String> getter2 = getter.to(SampleMeta.hello);
        Assert.assertNull(getter2.getValue(child));
    }
}
