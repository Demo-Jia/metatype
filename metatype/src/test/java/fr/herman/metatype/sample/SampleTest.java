package fr.herman.metatype.sample;

import org.testng.Assert;
import org.testng.annotations.Test;
import fr.herman.meta.SampleMetaType;
import fr.herman.meta.SampleMetaType.helloMetaProperty;
import fr.herman.metatype.model.method.HasGetter;
import fr.herman.metatype.model.method.HasSetter;

public class SampleTest {
    @Test
    public void test() {
        Sample sample = new Sample();
        sample.setHello("world");
        Assert.assertEquals("hello", SampleMetaType.hello.name());
        Assert.assertEquals(String.class, SampleMetaType.hello.type());
        Assert.assertEquals("world", helloMetaProperty.GETTER.getValue(sample));

        Assert.assertTrue(HasGetter.class.isAssignableFrom(SampleMetaType.whithoutSetter.getClass()));
        Assert.assertFalse(HasSetter.class.isAssignableFrom(SampleMetaType.whithoutSetter.getClass()));
    }
}
