package com.elderbyte.code.parser;

import com.elderbyte.code.dom.expressions.Operator;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds operator definitions for a parser
 */
public class OperatorSet {

    final Map<String, Operator> operators = new HashMap<>();

    public OperatorSet(Operator... operators){
        for (Operator o : operators){
            this.operators.put(o.getSign(), o);
        }
    }

    public OperatorSet(Iterable<Operator> operators){
        for (Operator o : operators){
            this.operators.put(o.getSign(), o);
        }
    }

    Iterable<Operator> getAllOperators(){
        return operators.values();
    }

    public Operator findOperator(String sign){
        return operators.get(sign);
    }

}
