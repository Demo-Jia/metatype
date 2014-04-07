package fr.herman.metatype.sample;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import org.testng.Assert;
import org.testng.annotations.Test;
import fr.herman.meta.SampleMetaType;
import fr.herman.meta.SampleMetaType.helloMetaProperty;
import fr.herman.metatype.model.method.HasGetter;
import fr.herman.metatype.model.method.HasSetter;
import fr.herman.metatype.utils.MetaUtils;

public class SampleTest {
    @Test
    public void test() {
        Sample sample = new Sample();
        sample.setHello("world");
        Assert.assertEquals("hello", SampleMetaType.hello.name());
        long time = System.nanoTime();
        Assert.assertEquals(String.class, SampleMetaType.hello.type());
        Assert.assertEquals("world", helloMetaProperty.GETTER.getValue(sample));

        Assert.assertTrue(HasGetter.class.isAssignableFrom(SampleMetaType.whithoutSetter.getClass()));
        Assert.assertFalse(HasSetter.class.isAssignableFrom(SampleMetaType.whithoutSetter.getClass()));

        Sample sample2 = new Sample();
        MetaUtils.applyTo(sample, sample2, SampleMetaType.hello, SampleMetaType.date);
        Assert.assertEquals(sample.getHello(), sample2.getHello());
        Assert.assertEquals(sample.getDate(), sample2.getDate());

        Collection<Sample> samples = Arrays.asList(sample, sample2);
        Iterator<String> it = MetaUtils.collect(samples, SampleMetaType.hello).iterator();
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals("world", it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals("world", it.next());
        Assert.assertFalse(it.hasNext());
        System.out.println(System.nanoTime() - time);
    }
}
