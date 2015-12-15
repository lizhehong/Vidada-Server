package com.elderbyte.server.vidada.queries;

import com.elderbyte.code.ExpressionParser;
import com.elderbyte.code.dom.expressions.Operator;
import com.elderbyte.code.parser.OperatorSet;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * An expression parser which is able to parse logical (tag) expressions
 */
public class TagExpressionParser extends ExpressionParser {

    public TagExpressionParser(){
        super(tagExpressionOpSet(), Pattern.compile("^[\\w|\\.]+$"));
    }

    private static OperatorSet tagExpressionOpSet(){
        List<Operator> mathOperators = new ArrayList<>();

        mathOperators.add(new Operator("&", 3, true, false));
        mathOperators.add(new Operator("|", 3, true, false));
        mathOperators.add(new Operator("!", 5, true, true));

        return new OperatorSet(mathOperators);
    }
}
