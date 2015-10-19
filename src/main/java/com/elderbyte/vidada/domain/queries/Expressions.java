package com.elderbyte.vidada.domain.queries;

import archimedes.core.util.Lists;

import java.util.ArrayList;
import java.util.List;


public class Expressions {

	public static <T> ListExpression<T> or(Expression<T>... disjunction){
		return or(Lists.asNoNullList(disjunction));
	}

	public static <T> ListExpression<T> or(Iterable<Expression<T>> disjunction){
		return ListExpression.createDisjunction(disjunction);
	}

	public static <T> ListExpression<T> and(Expression<T>... conjunction){
		return and(Lists.asNoNullList(conjunction));
	}

	public static <T> ListExpression<T> and(Iterable<Expression<T>> conjunction){
		return ListExpression.createConjunction(conjunction);
	}

	public static <T> VariableReferenceExpression<T> varReference(String variable){
		return VariableReferenceExpression.build(variable);
	}

	public static LiteralValueExpression<String> literalString(String value){
		return new LiteralStringExpression(value);
	}

	/**
	 * Creates an 'item MEMBER OF collection' expression
	 * @param literal
	 * @param collectionReference
	 * @return
	 */
	public static <T> Expression<T> memberOf(LiteralValueExpression<?> literal, VariableReferenceExpression<T> collectionReference){
		return new MemberOfExpression<T>(literal, collectionReference);
	}

	/**
	 * Creates an 'item NOT MEMBER OF collection' expression
	 * @param literal
	 * @param collectionReference
	 * @return
	 */
	public static <T> Expression<T> notMemberOf(LiteralValueExpression<?> literal, VariableReferenceExpression<T> collectionReference){
		return new MemberOfExpression<T>(literal, collectionReference, true);
	}

    /**
     * Negates an expression
     * @param expression The expression to negate
     * @param <T>
     * @return
     */
    public static <T> Expression<T> not(Expression<T> expression){
        return  new NotExpression<T>(expression);
    }

}
