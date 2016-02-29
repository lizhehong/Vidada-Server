package com.elderbyte.server.vidada.queries;

import com.elderbyte.code.CodeDomException;
import com.elderbyte.code.dom.expressions.BinaryOperatorExpression;
import com.elderbyte.code.dom.expressions.ExpressionNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by isnull on 29/02/16.
 */
public class TagExpressionBuilderTest {

    private TagExpressionBuilder expressionBuilder;

    @Before
    public void init(){
        expressionBuilder = new TagExpressionBuilder();
    }


    @Test
    public void basicExpressionTest(){

        ExpressionNode ast = expressionBuilder.build("foo & bar");


        Assert.assertTrue("The AST must not be null!", ast != null);
        Assert.assertEquals("The returned Node must be of specified type!",  ast.getClass(), BinaryOperatorExpression.class);

        BinaryOperatorExpression andExpression = (BinaryOperatorExpression)ast;
    }

    @Test(expected = CodeDomException.class)
    public void basicExpressionFailTest(){

        ExpressionNode ast = expressionBuilder.build("foo & | bar");
    }

    @Test
    public void repairSimpleListTest(){

        ExpressionNode ast = expressionBuilder.build("foo bar");
        // We expect that this (faulty) expression is automatically fixed for us.
        // So it should become foo & bar


        Assert.assertTrue("The AST must not be null!", ast != null);
        Assert.assertEquals("The returned Node must be of specified type!",  ast.getClass(), BinaryOperatorExpression.class);

        BinaryOperatorExpression andExpression = (BinaryOperatorExpression)ast;
    }

}
