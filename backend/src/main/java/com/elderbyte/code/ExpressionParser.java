package com.elderbyte.code;

import com.elderbyte.code.dom.expressions.ExpressionNode;
import com.elderbyte.code.parser.*;
import com.elderbyte.common.ArgumentNullException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;


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

    private final ExpressionScanner scanner;
    private final RPNTransformer rpnTransformer;
    private final ASTGenerator astGenerator;

    private Function<Stream<Token>, Stream<Token>> tokenTransformer;

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

        this(
            new ExpressionScanner(operatorSet),
            new RPNTransformer(operatorSet),
            new ASTGenerator(operatorSet));
    }

    /**
     * Creates a new ExpressionParser with the given operators
     * @param operatorSet
     */
    public ExpressionParser(OperatorSet operatorSet, Pattern wordMatcher){
        this(
            new ExpressionScanner(operatorSet, wordMatcher),
            new RPNTransformer(operatorSet),
            new ASTGenerator(operatorSet));
    }

    /**
     * Creates a new ExpressionParser
     */
    public ExpressionParser(ExpressionScanner scanner, RPNTransformer rpnTransformer, ASTGenerator astGenerator){

        if(scanner == null) throw new ArgumentNullException("scanner");
        if(rpnTransformer == null) throw new ArgumentNullException("rpnTransformer");
        if(astGenerator == null) throw new ArgumentNullException("astGenerator");

        this.scanner = scanner;
        this.rpnTransformer = rpnTransformer;
        this.astGenerator = astGenerator;
    }

    /***************************************************************************
     *                                                                         *
     * Public Properties                                                       *
     *                                                                         *
     **************************************************************************/

    /**
     * Gets the scanner used to tokenize a input string.
     * @return
     */
    public ExpressionScanner getScanner(){
        return scanner;
    }


    /**
     * Provides an entry point to modify the parsed token stream before it is being further
     * processed.
     * @param tokenTransformer
     */
    public void setTokenTransformer(Function<Stream<Token>, Stream<Token>> tokenTransformer){
        this.tokenTransformer = tokenTransformer;
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
            Stream<Token> tokens = getScanner().tokenize(code);

            if(tokenTransformer != null) {
                tokens = tokenTransformer.apply(tokens);
            }

            Stream<Token> rpn = rpnTransformer.toReversePolishNotation(tokens);
            return astGenerator.parse(rpn);
        }catch (CodeDomException e){
            throw new CodeDomException(String.format("Failed to parse '%s'", code), e);
        }
    }
}
