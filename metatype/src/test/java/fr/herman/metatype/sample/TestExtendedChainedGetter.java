package fr.herman.metatype.sample;

import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;
import fr.herman.metatype.model.method.Getter;
import fr.herman.metatype.utils.ExtendedChainedGetter;

public class TestExtendedChainedGetter
{

    @Test
    public void test()
    {
        SampleChild child = new SampleChild();
        ExtendedChainedGetter<SampleChild, SampleChild, Sample> ecg = ExtendedChainedGetter.from(SampleChildMeta.sample);

        Sample sample = new Sample();
        sample.setHello("world");
        child.setSample(sample);
        String value = ecg.to(SampleMeta.hello).getValue(child);
        AssertJUnit.assertEquals("world", value);

        Getter<SampleChild, String> getter = ecg.to(SampleChildMeta.childstring);
        String childString = getter.getValue(child);
        Assert.assertNull(childString);

        SampleChild child2 = new SampleChild();
        child2.setChildString("i'm child");
        child.setSample(child2);
        childString = getter.getValue(child);
        AssertJUnit.assertEquals("i'm child", childString);
    }
}
