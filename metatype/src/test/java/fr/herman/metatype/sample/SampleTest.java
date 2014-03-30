package fr.herman.metatype.sample;

import org.testng.Assert;
import org.testng.annotations.Test;

import fr.herman.meta.SampleMetaType;

public class SampleTest {
    @Test
    public void test() {
        Sample sample = new Sample();
        sample.hello = "world";
        Assert.assertEquals(sample.hello.getClass(),
                SampleMetaType.hello.type());
    }
}
