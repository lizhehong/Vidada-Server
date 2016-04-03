package com.elderbyte.vidada.tags;

import com.elderbyte.vidada.tags.relations.TagRelation;
import com.elderbyte.vidada.tags.relations.TagRelationOperator;
import org.junit.Assert;
import org.junit.Test;

public class TagRelationTest {

    @Test
    public void testEquals() throws Exception {

        TagRelation relation1 = new TagRelation(
                Tag.buildTag("automobile").get(),
                TagRelationOperator.Equal,
                Tag.buildTag("car").get());

        TagRelation relation2 = new TagRelation(
            Tag.buildTag("automobile").get(),
                TagRelationOperator.Equal,
            Tag.buildTag("car").get());

        Assert.assertEquals(relation1, relation2);
    }

    @Test
    public void testEqualsNot() throws Exception {
        TagRelation relation1 = new TagRelation(
                Tag.buildTag("automobile").get(),
                TagRelationOperator.IsParentOf,
                Tag.buildTag("car").get());

        TagRelation relation2 = new TagRelation(
                Tag.buildTag("automobile").get(),
                TagRelationOperator.Equal,
                Tag.buildTag("car").get());

        Assert.assertNotEquals(relation1, relation2);
    }

    @Test
    public void testEqualsNot2() throws Exception {
        TagRelation relation1 = new TagRelation(
                Tag.buildTag("automobile").get(),
                TagRelationOperator.IsParentOf,
                Tag.buildTag("blub").get());

        TagRelation relation2 = new TagRelation(
                Tag.buildTag("automobile").get(),
                TagRelationOperator.IsParentOf,
                Tag.buildTag("car").get());

        Assert.assertNotEquals(relation1, relation2);
    }
}
