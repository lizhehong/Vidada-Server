package com.elderbyte.vidada.queries;

import com.elderbyte.code.CodeDomException;
import com.elderbyte.code.dom.expressions.*;
import com.elderbyte.code.parser.Token;
import com.elderbyte.code.parser.TokenType;
import com.elderbyte.vidada.tags.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Builds an Expression AST from a given tag expression
 */
public class TagExpressionBuilder {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private final TagExpressionParser parser = new TagExpressionParser();

    private Function<Tag, Set<Tag>> expander;

    /**
     *
     * @return
     */
    public static TagExpressionBuilder create(){
        return new TagExpressionBuilder();
    }

    private TagExpressionBuilder(){

    }

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /**
     *
     * @param expander
     * @return
     */
    public TagExpressionBuilder expandTags(Function<Tag, Set<Tag>> expander){
        this.expander = expander;
        return this;
    }

    /**
     * Enables repairing of simple tag queries, such as injecting missing
     * AND Operators.
     *
     * "foo bar" ---> "foo & bar"
     *
     * @return
     */
    public TagExpressionBuilder enableExpressionRepair(){
        parser.setTokenTransformer(tokenStream -> {

            final Token andOperator = new Token(TokenType.Operator, "&");
            List<Token> enhancedList = new ArrayList<>();
            Token previous = null;

            for (Token token : tokenStream.collect(Collectors.toList())) {
                if(previous != null && previous.getType() == TokenType.Identifier){
                    // previous was variable - next must not be one:

                    if(token.getType() == TokenType.Identifier){
                        // Fix this by injecting AND (&) Operator:
                        enhancedList.add(andOperator);
                    }
                }
                enhancedList.add(token);
                previous = token;
            }

            return enhancedList.stream();
        });
        return this;
    }


    /**
     * Build a tag-query expression.
     * @param tagExpression Sample expression: 'action & comedy & (1080p | 720p)'
     * @return
     * @exception CodeDomException Thrown when building the expression AST failed!
     */
    public ExpressionNode build(String tagExpression){

        ExpressionNode ast = parser.parseExpression(tagExpression);

        // Since the expression implicitly assumes that each tag is replaced by '{x} MEMEBER OF tags'
        // We have to repair the AST now. The tags are currently recognized as variable references (which they are not)
        ast = repairMemberOf(ast, new VariableReference("tags"));
        return ast;
    }

    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/





    /**
     * Converts the given Expression Tree into a real query.
     *
     * foo & bar ---> (foo MEMBER OF tags) & (bar MEMBER OF tags)
     *
     * @param ast
     * @param tagsRef
     * @return
     */
    private ExpressionNode repairMemberOf(ExpressionNode ast, ExpressionNode tagsRef) {

        final Operator memberOf = new Operator("MEMBER OF", 5, true, false);

        // We replace all VariableReferences with MemberOfExpressions

        return recursiveReplace(ast,
            x -> (x instanceof VariableReference),
            x -> {
                VariableReference varRef = (VariableReference)x;
                LiteralValueExpression literalTag = new LiteralValueExpression((varRef).getName());

                return new BinaryOperatorExpression(literalTag, memberOf, tagsRef);
            });

    }

    /**
     * Works similar to a string-replace method
     *
     * The whole AST Tree is recursively traversed and if a node matches (see matcher,
     * then it will be replaced with whatever the replacer() returns.
     *
     * @param current
     * @param matcher
     * @param replacer
     * @return
     */
    private static ExpressionNode recursiveReplace(ExpressionNode current, Predicate<ExpressionNode> matcher, Function<ExpressionNode,ExpressionNode> replacer){

        if(matcher.test(current)){
            return replacer.apply(current);
        }

        // Traversal is currently hardcoded here, probably the visitor pattern could be enhanced to support replacements.

        if(current instanceof UnaryExpression){
            ExpressionNode replaced = recursiveReplace(((UnaryOperatorExpression) current).getInner(), matcher, replacer);
            ((UnaryOperatorExpression) current).setInner(replaced);
        }

        if(current instanceof BinaryExpression){
            BinaryExpression binary = (BinaryExpression)current;

            ExpressionNode replacedLeft = recursiveReplace(binary.getLeft(), matcher, replacer);
            ExpressionNode replacedRight = recursiveReplace(binary.getRight(), matcher, replacer);

            binary.setLeft(replacedLeft);
            binary.setRight(replacedRight);
        }

        return current;
    }


}
