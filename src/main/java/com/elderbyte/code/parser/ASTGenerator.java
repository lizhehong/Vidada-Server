package com.elderbyte.code.parser;

import com.elderbyte.code.dom.expressions.*;

import java.util.*;

/**
 * Creates an Abstract Syntax tree from a stream of tokens in the RPN format.
 */
public class ASTGenerator {

    private final OperatorSet operatorSet;


    public ASTGenerator(OperatorSet operatorSet){
        this.operatorSet = operatorSet;
    }

    /**
     * Parses a RPN token stream into an AST
     * @param rpn
     * @return
     * @throws ASTGeneratorException
     */
    public ExpressionNode parse(Iterable<Token> rpn){

        Stack<ExpressionNode> expressionNodeStack = new Stack<>();

        for(Token token : rpn){
            if(isOperator(token)){
                // token is Operator / Function
                // Get count of required params for this op / func
                int paramsRequired = findParamCount(token);

                if(expressionNodeStack.size() < paramsRequired){
                    throw new InsufficientParametersException("Not enough parameters for this operator. Have " +  expressionNodeStack.size() + " but need " + paramsRequired);
                }

                List<ExpressionNode> params = new ArrayList<>();
                for(int i = 0; i < paramsRequired; i++){
                    params.add(expressionNodeStack.pop());
                }

                ExpressionNode expressionNode = buildExpression(token, params);
                expressionNodeStack.push(expressionNode);
            }else if(isLiteral(token)) {
                expressionNodeStack.push(new LiteralValueExpression(token.getValue()));
            }else if(isIdentfier(token)){
                expressionNodeStack.push(new VariableReference(token.getValue()));

            }else {
                throw new ASTGeneratorException("Unexpected token " + token + "!");
            }
        }

        if(expressionNodeStack.size() == 1){
            return expressionNodeStack.pop();
        }else{
            throw new ASTGeneratorException("Too many values for the given operators!");
        }

    }



    private ExpressionNode buildExpression(Token operator, List<ExpressionNode> params){

        if(operator.getType() == TokenType.Operator){
            Operator op = operatorSet.findOperator(operator.getValue());
            if(!op.isUnary()){
                return new BinaryOperatorExpression(params.get(0), op, params.get(1));
            }else{
                return new UnaryOperatorExpression(op, params.get(0));

            }
        }
        throw new ASTGeneratorException("Failed to build Expression. Unexpected Operator Token " + operator + " [" + operator.getType() + "]!");
    }

    private boolean isIdentfier(Token token){
        return token.getType() == TokenType.Identifier;
    }


    private boolean isLiteral(Token token){
        return token.getType() == TokenType.Literal;
    }

    private boolean isOperator(Token token){
        return token.getType() == TokenType.Operator || isFunction(token);
    }

    private boolean isFunction(Token token){
        return false;   // TODO
    }

    private int findParamCount(Token token) {
        if(token.getType() == TokenType.Operator){
            return !operatorSet.findOperator(token.getValue()).isUnary() ? 2 : 1;
        }else{
            // Function? Not implemented yet. Would require look-ahead to count params...
            throw new IllegalStateException("Only operators are currently supported");
        }
    }

}
