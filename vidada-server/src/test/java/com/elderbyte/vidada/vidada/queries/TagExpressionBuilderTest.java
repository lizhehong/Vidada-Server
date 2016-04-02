package com.elderbyte.vidada.vidada.queries;

import com.elderbyte.code.CodeDomException;
import com.elderbyte.code.dom.expressions.BinaryOperatorExpression;
import com.elderbyte.code.dom.expressions.ExpressionNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class TagExpressionBuilderTest {

    private TagExpressionBuilder expressionBuilder;

    @Before
    public void init(){
        expressionBuilder = TagExpressionBuilder.create();
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

        ExpressionNode ast = expressionBuilder.enableExpressionRepair().build("foo bar");
        // We expect that this (faulty) expression is automatically fixed for us.
        // So it should become foo & bar


        Assert.assertTrue("The AST must not be null!", ast != null);
        Assert.assertEquals("The returned Node must be of specified type!",  ast.getClass(), BinaryOperatorExpression.class);

        BinaryOperatorExpression andExpression = (BinaryOperatorExpression)ast;
    }

    @Test
    public void repairComplexListTest(){

        ExpressionNode ast = expressionBuilder.enableExpressionRepair().build("(aaa | bbb) & foo bar");
        // We expect that this (faulty) expression is automatically fixed for us.
        // So it should become (aaa | bbb) & foo & bar


        Assert.assertTrue("The AST must not be null!", ast != null);
        Assert.assertEquals("The returned Node must be of specified type!",  ast.getClass(), BinaryOperatorExpression.class);

        BinaryOperatorExpression andExpression = (BinaryOperatorExpression)ast;
    }

}
