package com.elderbyte.code.parser;

import com.elderbyte.code.dom.expressions.Operator;
import com.elderbyte.common.ArgumentNullException;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Scans an input string and turns it into a stream of tokens.
 *
 */
public class ExpressionScanner {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private final Map<String, Token> terminalMap = new HashMap<>();
    private final Predicate<String> isWordPredicate;

    private static final Pattern DefaultWordPattern = Pattern.compile("^\\w+$");

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new expression scanner using the given operator-set.
     * Uses the default word-pattern matcher, which matches alpha-characters as words.
     * @param operatorSet
     */
    public ExpressionScanner(OperatorSet operatorSet){
        this(operatorSet, DefaultWordPattern);
    }

    /**
     * Creates a new expression scanner using the given operator-set and word regex.
     * @param operatorSet
     */
    public ExpressionScanner(OperatorSet operatorSet, Pattern isWordRegex){
        this(
            defaultTerminals(operatorSet),
            x -> isWordRegex.matcher(x).matches());
    }


    /**
     * Creates a new ExpressionTokenizer with the given Operators
     * @param terminalTokens
     */
    public ExpressionScanner(Iterable<Token> terminalTokens, Predicate<String> isWordPredicate){

        if(terminalTokens == null) throw new ArgumentNullException("terminalTokens");
        if(isWordPredicate == null) throw new ArgumentNullException("isWordPredicate");


        this.isWordPredicate = isWordPredicate;
        for(Token op : terminalTokens){
            terminalMap.put(op.getValue(), op);
        }
    }


    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /**
     * Turns the given string into a token stream
     * @param expression
     * @return
     */
    public Iterable<Token> tokenize(String expression){

        if(expression == null) throw new ArgumentNullException("expression");


        List<Token> tokens = new ArrayList<>();

        List<String> rawTokens = StringUtils.splitKeep(expression, terminalMap.keySet());

        for(String rawToken : rawTokens){
            Token t = emit(rawToken);
            if(t != null){
                tokens.add(t);
            }
        }

        return tokens;
    }

    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/


    private Token emit(String currentWord){
        Token t = getTerminal(currentWord);

        if(t == null && isLiteral(currentWord)){
            t = new Token(TokenType.Identifier, currentWord);
        }

        if(t != null && t.getType() != TokenType.Whitespace){
            return t;
        }else
            return null;
    }

    private boolean isLiteral(String word){
        return isWordPredicate.test(word);
    }

    private Token getTerminal(String terminal) {
        if(terminalMap.containsKey(terminal)){
            return terminalMap.get(terminal);
        }
        return null;
    }


    private static Iterable<Token> defaultTerminals(OperatorSet operatorSet){

        if(operatorSet == null) throw new ArgumentNullException("operatorSet");


        List<Token> defaultTerminals = new ArrayList<>();

        defaultTerminals.add(new Token(TokenType.Whitespace, " "));
        defaultTerminals.add(new Token(TokenType.Whitespace, "\t"));
        defaultTerminals.add(new Token(TokenType.Whitespace, ","));

        defaultTerminals.add(new Token(TokenType.Parentheses_Open, "("));
        defaultTerminals.add(new Token(TokenType.Parentheses_Closed, ")"));

        for(Operator o : operatorSet.getAllOperators()){
            defaultTerminals.add(new Token(TokenType.Operator, o.getSign()));
        }

        return defaultTerminals;
    }

}
