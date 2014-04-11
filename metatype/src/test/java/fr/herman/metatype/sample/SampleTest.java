package fr.herman.metatype.sample;

import org.testng.annotations.Test;

public class SampleTest
{
    @Test
    public void test()
    {
        // Sample sample = new Sample();
        // sample.setHello("world");
        // QSample meta = QSample.sample;
        // Assert.assertEquals("sample.hello", meta.hello.toString());
        // System.nanoTime();
        // Assert.assertEquals(String.class, meta.hello.getType());
        // Getter<Sample, String> getter = BeanHelper.getter(meta, meta.hello);
        // Assert.assertEquals("world", getter.getValue(sample));

        // Sample sample2 = new Sample();
        // MetaUtils.applyTo(sample, sample2, SampleMetaType.hello, SampleMetaType.date);
        // Assert.assertEquals(sample.getHello(), sample2.getHello());
        // Assert.assertEquals(sample.getDate(), sample2.getDate());
        //
        // Collection<Sample> samples = Arrays.asList(sample, sample2);
        // Iterator<String> it = MetaUtils.collect(samples, SampleMetaType.hello).iterator();
        // Assert.assertTrue(it.hasNext());
        // Assert.assertEquals("world", it.next());
        // Assert.assertTrue(it.hasNext());
        // Assert.assertEquals("world", it.next());
        // Assert.assertFalse(it.hasNext());
        // System.out.println(System.nanoTime() - time);
    }

    @Test
    public void testQuery()
    {
        // Sample sample = new Sample();
        // sample.setHello("world");
        // Getter<Sample, String> getter = BeanHelper.getter(QSample.sample, QSample.sample.hello);
        // Getter<Sample, String> getter2 = BeanHelper.getter(QSample.sample, QSample.sample.hello);
        // BeanHelper.getter(QSample.sample, QSample.sample.date);
        // BeanHelper.getter(QSample.sample, QSample.sample.booleans);
        // BeanHelper.getter(QSample.sample, QSample.sample.truc);
        // BeanHelper.getter(QSample.sample, QSample.sample.whithoutSetter);
        // long time = System.currentTimeMillis();
        // for (int i = 0; i < 100_000_000; i++)
        // {
        // Assert.assertEquals("world", sample.getHello());
        // }
        // System.err.println(System.currentTimeMillis() - time);
        // time = System.currentTimeMillis();
        // for (int i = 0; i < 100_000_000; i++)
        // {
        // Assert.assertEquals("world", getter.getValue(sample));
        // }
        // System.err.println(System.currentTimeMillis() - time);
        // time = System.currentTimeMillis();
        // for (int i = 0; i < 100_000_000; i++)
        // {
        // Assert.assertEquals("world", sample.getHello());
        // }
        // System.err.println(System.currentTimeMillis() - time);
        // time = System.currentTimeMillis();
        // for (int i = 0; i < 100_000_000; i++)
        // {
        // Assert.assertEquals("world", getter2.getValue(sample));
        // }
        // System.err.println(System.currentTimeMillis() - time);
    }
}
