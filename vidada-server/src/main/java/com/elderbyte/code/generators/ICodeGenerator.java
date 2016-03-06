package com.elderbyte.code.generators;


import com.elderbyte.code.dom.expressions.ExpressionNode;

/**
 * Generates source code of the given AST node
 */
public interface ICodeGenerator {

    /**
     * Generate source code
     * @param node Expression Node node
     * @return
     */
    String generate(ExpressionNode node);

}
