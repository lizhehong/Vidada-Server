package com.elderbyte.vidada.tags;

import com.elderbyte.vidada.tags.relations.TagRelation;
import com.elderbyte.vidada.tags.relations.TagRelationDefinition;
import com.elderbyte.vidada.tags.relations.TagRelationOperator;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class TagRelationDefinitionTest {


    /**
     *
     * */
    public static void main(String[] args)throws Exception{
        new TagRelationDefinitionTest().testGetAllRelatedTags();
    }

    @Test
    public void testEqualityRelated() throws Exception {
        Tag car = Tag.buildTag("car").get();
        Tag auto = Tag.buildTag("automobile").get();

        TagRelationDefinition definition = new TagRelationDefinition();
        definition.addRelation(new TagRelation(car, TagRelationOperator.Equal, auto));

        Set<Tag> tags =  definition.getAllRelatedTags(car);
        Assert.assertEquals("Expecting bot car and automobile to be returned", tags.size(), 2);

        Assert.assertTrue(tags.contains(car));
        Assert.assertTrue(tags.contains(auto));
    }

    @Test
    public void testDuplicates() throws Exception {
        Tag car = Tag.buildTag("car").get();
        Tag auto = Tag.buildTag("automobile").get();
        Tag auto2 = Tag.buildTag("automobile").get();

        TagRelationDefinition definition = new TagRelationDefinition();
        definition.addRelation(new TagRelation(car, TagRelationOperator.Equal, auto));
        definition.addRelation(new TagRelation(car, TagRelationOperator.Equal, auto2));


        Set<Tag> tags =  definition.getAllRelatedTags(car);
        Assert.assertEquals("Expecting bot car and automobile to be returned", tags.size(), 2);

        Assert.assertTrue(tags.contains(car));
        Assert.assertTrue(tags.contains(auto));
    }

    @Test
    public void testParentSimple() throws Exception {
        Tag car = Tag.buildTag("car").get();
        Tag bmw = Tag.buildTag("bmw").get();
        Tag audi = Tag.buildTag("audi").get();

        TagRelationDefinition definition = new TagRelationDefinition();
        definition.addRelation(new TagRelation(car, TagRelationOperator.IsParentOf, bmw));
        definition.addRelation(new TagRelation(car, TagRelationOperator.IsParentOf, audi));

        Set<Tag> cars =  definition.getAllRelatedTags(car);

        Assert.assertTrue(cars.contains(car));
        Assert.assertTrue(cars.contains(bmw));
        Assert.assertTrue(cars.contains(audi));

        Set<Tag> cars2 =  definition.getAllRelatedTags(bmw);

        Assert.assertTrue(cars.contains(bmw));
        Assert.assertFalse(cars2.contains(car));
        Assert.assertFalse(cars2.contains(audi));
        // definition.
    }

    @Test
    public void testParentSimpleRemoved() throws Exception {
        Tag car = Tag.buildTag("car").get();
        Tag bmw = Tag.buildTag("bmw").get();
        Tag audi = Tag.buildTag("audi").get();

        TagRelationDefinition definition = new TagRelationDefinition();
        definition.addRelation(new TagRelation(car, TagRelationOperator.IsParentOf, bmw));
        definition.addRelation(new TagRelation(car, TagRelationOperator.IsParentOf, audi));

        // Test
        Set<Tag> cars =  definition.getAllRelatedTags(car);
        Assert.assertTrue(cars.contains(car));
        Assert.assertTrue(cars.contains(bmw));
        Assert.assertTrue(cars.contains(audi));

        // remove root
        definition.removeRelation(new TagRelation(car, TagRelationOperator.IsParentOf, bmw));

        // Test again
        Set<Tag> cars2 =  definition.getAllRelatedTags(car);
        Assert.assertTrue(cars2.contains(car));
        Assert.assertTrue(cars2.contains(audi));
        Assert.assertFalse(cars2.contains(bmw));
    }

    @Test
    public void testGetAllRelatedTags() throws Exception {


        Tag object = Tag.buildTag("object").get();
        Tag category = Tag.buildTag("category").get();
        Tag action = Tag.buildTag("action").get();
        Tag kaboom = Tag.buildTag("kaboom").get();
        Tag comedy = Tag.buildTag("comedy").get();
        Tag fruit = Tag.buildTag("fruit").get();
        Tag vegetable = Tag.buildTag("vegetable").get();
        Tag veggi = Tag.buildTag("veggi").get();
        Tag apple = Tag.buildTag("apple").get();

        TagRelationDefinition definition = new TagRelationDefinition();


        definition.addRelation(new TagRelation(action, TagRelationOperator.Equal, kaboom));
        definition.addRelation(new TagRelation(object, TagRelationOperator.IsParentOf, category));

        definition.addRelation(new TagRelation(category, TagRelationOperator.IsParentOf, action));
        definition.addRelation(new TagRelation(category, TagRelationOperator.IsParentOf, comedy));

        definition.addRelation(new TagRelation(object, TagRelationOperator.IsParentOf, fruit));
        definition.addRelation(new TagRelation(object, TagRelationOperator.IsParentOf, action));

        definition.addRelation(new TagRelation(fruit, TagRelationOperator.IsParentOf, apple));
        definition.addRelation(new TagRelation(fruit, TagRelationOperator.Equal, vegetable));

        definition.addRelation(new TagRelation(vegetable, TagRelationOperator.IsParentOf, veggi));

        //definition.addRelation(new TagRelation(Tag.create("object"), TagRelationOperator.IsParentOf, Tag.create("category")));
        //definition.addRelation(new TagRelation(action, TagRelationOperator.Equal, kaboom));
        //definition.addRelation(new TagRelation(object, TagRelationOperator.IsParentOf, action));

        // TODO Assert-Tests?
        System.out.println(System.lineSeparator()+definition.toTreeString());
    }


}
