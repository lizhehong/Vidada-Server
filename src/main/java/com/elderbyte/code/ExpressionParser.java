package com.elderbyte.code;

import com.elderbyte.code.dom.DomNode;
import com.elderbyte.code.dom.expressions.ExpressionNode;
import com.elderbyte.code.parser.*;


/**
 * A generic parser for (Math, C, or Java) Style expressions.
 *
 *
 *
 */
public class ExpressionParser implements IExpressionParser {

    private final ExpressionScanner tokenizer;
    private final RPNTransformer rpnTransformer;
    private final ASTGenerator astGenerator;

    /**
     * Creates a new ExpressionParser
     * @param operatorSet
     */
    public ExpressionParser(OperatorSet operatorSet){

        tokenizer = new ExpressionScanner(operatorSet);
        rpnTransformer = new RPNTransformer(operatorSet);
        astGenerator = new ASTGenerator(operatorSet);
    }


    @Override
    public ExpressionNode parseExpression(String code) {

        Iterable<Token> tokens = tokenizer.tokenize(code);
        //System.out.println("Tokens: " + tokens);

        Iterable<Token> rpn = rpnTransformer.toReversePolishNotation(tokens);
        //System.out.println("RPN: " + rpn);

        return astGenerator.parse(rpn);
    }
}
