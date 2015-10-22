package com.elderbyte.code;

import com.elderbyte.code.dom.expressions.ExpressionNode;
import com.elderbyte.code.parser.*;
import com.elderbyte.common.ArgumentNullException;


/**
 * A generic parser for (Math, C, or Java) Style expressions.
 *
 * This parser works in three steps:
 *
 * 1. The scanner tokenizes the input string into Token Symbols
 * 2. The (Reverse Polish Notation) RPN Transformer transforms the token stream into RPN.
 * 3. The ASTGenerator takes the RPN Token stream and builds an AST from it.
 */
public class ExpressionParser implements IExpressionParser {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private final ExpressionScanner tokenizer;
    private final RPNTransformer rpnTransformer;
    private final ASTGenerator astGenerator;

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new ExpressionParser with the given operators
     * @param operatorSet
     */
    public ExpressionParser(OperatorSet operatorSet){

        if(operatorSet == null) throw new ArgumentNullException("operatorSet");

        tokenizer = new ExpressionScanner(operatorSet);
        rpnTransformer = new RPNTransformer(operatorSet);
        astGenerator = new ASTGenerator(operatorSet);
    }

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /**
     * Parses the given expression into an Abstract Syntax Tree (AST)
     *
     * @param code The code to parse. Must not be Null!
     * @return Returns the root node of the Expression AST
     * @exception CodeDomException Thrown if parsing of the code failed
     */
    @Override
    public ExpressionNode parseExpression(String code) {

        if(code == null) throw new ArgumentNullException("code");

        try {
            Iterable<Token> tokens = tokenizer.tokenize(code);

            Iterable<Token> rpn = rpnTransformer.toReversePolishNotation(tokens);

            return astGenerator.parse(rpn);
        }catch (CodeDomException e){
            throw new CodeDomException(String.format("Failed to parse '%s'", code), e);
        }
    }
}
