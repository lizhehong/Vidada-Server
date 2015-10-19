package com.elderbyte.code.parser;

import com.elderbyte.code.dom.expressions.Operator;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scans an input string and turns it into a stream of tokens.
 *
 */
public class ExpressionScanner {

    private final Map<String, Token> terminalMap = new HashMap<>();


    public ExpressionScanner(OperatorSet operatorSet){
        this(defaultTerminals(operatorSet));
    }


    /**
     * Creates a new ExpressionTokenizer with the given Operators
     * @param terminalTokens
     */
    public ExpressionScanner(Iterable<Token> terminalTokens){
        for(Token op : terminalTokens){
            terminalMap.put(op.getValue(), op);
        }
    }


    public Iterable<Token> tokenize(String expression){

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

    static final Pattern wordPattern = Pattern.compile("^\\w+$");

    private Token emit(String currentWord){
        Token t = getTerminal(currentWord);

        if(t == null && isLiteral(currentWord)){
            t = new Token(TokenType.Identifier, currentWord);
        }

        //System.out.println("emitting: '" + currentWord + "' T: " + t);

        if(t != null && t.getType() != TokenType.Whitespace){
            return t;
        }else
            return null;
    }

    private boolean isLiteral(String word){
        Matcher m = wordPattern.matcher(word);
        return m.matches();
    }

    private Token getTerminal(String terminal) {
        if(terminalMap.containsKey(terminal)){
            return terminalMap.get(terminal);
        }
        return null;
    }


    private static Iterable<Token> defaultTerminals(OperatorSet operatorSet){

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
