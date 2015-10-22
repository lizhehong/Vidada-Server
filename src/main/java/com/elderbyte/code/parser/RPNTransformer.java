package com.elderbyte.code.parser;

import com.elderbyte.code.CodeDomException;
import com.elderbyte.code.dom.expressions.Operator;
import com.elderbyte.common.ArgumentNullException;

import java.util.*;

/**
 * Transforms a stream of tokens into Reverse Polish Notation
 *
 */
public class RPNTransformer {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private final OperatorSet operatorSet;

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new RPNTransformer
     * @param operatorSet
     */
    public RPNTransformer(OperatorSet operatorSet){

        if(operatorSet == null) throw new ArgumentNullException("operatorSet");

        this.operatorSet = operatorSet;
    }

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /**
     * Transfroms the given token stream into RPN.
     * Runtime complexity is O(n)
     *
     * @param tokens
     * @return A token stream in RPN order
     *
     * @exception CodeDomException Thrown when the transformation failed.
     */
    public List<Token> toReversePolishNotation(Iterable<Token> tokens){

        Stack<Token> operatorStack = new Stack<>();
        List<Token> rpn = new ArrayList<>();

        for(Token token : tokens){
            if(isLiteral(token)){
                rpn.add(token);
            }else if(isOperator(token)){

                Token o1 = token;

                while (!operatorStack.isEmpty()){

                    Token o2 = operatorStack.peek();

                    if(     isOperator(o2) &&
                            // o1 is left-associative and its precedence is less than or equal to that of o2, or
                            ((isLeftAssociative(o1) && precedence(o1) <= precedence(o2)) |
                                    (!isLeftAssociative(o1) && precedence(o1) < precedence(o2))
                            )){
                        // then pop o2 off the operator stack, onto the output queue;
                        rpn.add(operatorStack.pop());
                    }else{
                        break;
                    }
                }
                operatorStack.push(o1);
            }else if(isLeftParenthesis(token)){
                operatorStack.push(token);
            }else if (isRightParenthesis(token)){

                boolean success = false;
                while (!operatorStack.isEmpty()){
                    Token op = operatorStack.pop();
                    if(isLeftParenthesis(op)){
                        success = true;
                        break;
                    }else{
                        rpn.add(op);
                    }
                }
                if(!success)
                    throw new ParenthesisMismatchException("No closing Parenthesis found for open Parenthesis!");
            }else{
                throw new CodeDomException(String.format("Unexpected Token '%s' in RPN Expression!", token));
            }
        }

        while (!operatorStack.isEmpty()){
            Token op = operatorStack.pop();
            if(isParenthesis(op)){
                throw  new ParenthesisMismatchException("");
            }
            rpn.add(op);
        }
        return rpn;
    }


    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/

    private boolean isParenthesis(Token token){
        return isLeftParenthesis(token) || isRightParenthesis(token);
    }

    private boolean isLeftParenthesis(Token token){
        return token.getType() == TokenType.Parentheses_Open;
    }

    private boolean isRightParenthesis(Token token){
        return token.getType() == TokenType.Parentheses_Closed;
    }

    private int precedence(Token operator){
        Operator op = operatorSet.findOperator(operator.getValue());
        if(op == null){
            throw new CodeDomException("Operator Token expected: " + operator);
        }
        return op.getPrecedence();
    }

    private boolean isLeftAssociative(Token operator){
        Operator op = operatorSet.findOperator(operator.getValue());

        if(op == null){
            throw new CodeDomException("Operator Token expected: " + operator);
        }
        return op.isLeftAssociative();
    }

    private boolean isLiteral(Token token){
        return token.getType() == TokenType.Identifier;
    }

    private boolean isOperator(Token token){
        return token.getType() == TokenType.Operator;
    }
}
