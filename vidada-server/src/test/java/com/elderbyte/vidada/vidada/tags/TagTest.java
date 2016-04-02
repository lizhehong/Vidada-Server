package com.elderbyte.vidada.vidada.tags;

import org.junit.Assert;
import org.junit.Test;

public class TagTest {

    @Test
    public void testBasic() {
        Tag t = Tag.buildTag("cool").get();
        Assert.assertEquals(t.getName(), "cool");
    }

    @Test
    public void testBasicCase() {
        Tag t = Tag.buildTag("CoOl").get();
        Assert.assertEquals(t.getName(), "cool");
    }

    @Test
    public void testBasicDots() {
        Tag t = Tag.buildTag("Bugs.Bunny").get();
        Assert.assertEquals(t.getName(), "bugs.bunny");
    }



    @Test
    public void testEquals() {
        Tag t1 = Tag.buildTag("cool").get();
        Tag t2 = Tag.buildTag("cool").get();
        Assert.assertEquals(t1, t2);
    }

    @Test
    public void testHashcode() {
        Tag t1 = Tag.buildTag("cool").get();
        Tag t2 = Tag.buildTag("cool").get();
        Assert.assertEquals(t1.hashCode(), t2.hashCode());
    }

    @Test
    public void testCompareTo() throws Exception {
        Tag t1 = Tag.buildTag("abc").get();
        Tag t2 = Tag.buildTag("xyz").get();
        Tag t3 = Tag.buildTag("phd").get();

        Assert.assertTrue(t1.compareTo(t2) < 0 );

        Assert.assertTrue(t2.compareTo(t3) > 0 );
    }
}
