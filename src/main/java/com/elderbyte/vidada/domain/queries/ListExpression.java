package com.elderbyte.vidada.domain.queries;

import archimedes.core.expressions.BinaryCombination;
import archimedes.core.util.Lists;

import java.util.List;

/**
 * Conjunction and Disjunction helper class
 * @author IsNull
 *
 * @param <T>
 */
public class ListExpression<T> extends Expression<T> {


	/**
	 * Create a disjunction (OR combined expression list)
	 * @return
	 */
	public static <T> ListExpression<T> createDisjunction(){
		return new ListExpression<T>(BinaryCombination.OR, null);
	}

	/**
	 * Creates a conjunction (AND combined expression list)
	 * @return
	 */
	public static <T> ListExpression<T> createConjunction(){
		return new ListExpression<T>(BinaryCombination.AND, null);
	}




	/**
	 * Create a disjunction (OR combined expression list)
	 * @param disjunction
	 * @return
	 */
	public static <T> ListExpression<T> createDisjunction(Iterable<Expression<T>> disjunction){
		return new ListExpression<T>(BinaryCombination.OR, disjunction);
	}

	/**
	 * Creates a conjunction (AND combined expression list)
	 * @param conjunction
	 * @return
	 */
	public static <T> ListExpression<T> createConjunction(Iterable<Expression<T>> conjunction){
		return new ListExpression<T>(BinaryCombination.AND, conjunction);
	}

	private final List<Expression<T>> list;
	private final BinaryCombination operator;

	private ListExpression(BinaryCombination operator, Iterable<Expression<T>> list) {
		super();
		this.operator = operator;
		this.list = Lists.newList(list);
	}

	public BinaryCombination getCombinationOperator(){
		return operator;
	}

	public void add(Expression<T> expr){
		list.add(expr);
	}

	@Override
	public String code() {

		if(list.isEmpty()) return "";

		String code = "";
		for (int i = 0; i < list.size(); i++) {
			Expression<T> expr = list.get(i);

			if(i == list.size()-1){
				// last expression
				code += expr.code();
			}else{
				code += expr.code() + " " + getCombinationOperator().name() + " ";
			}
		}
		return list.size() == 1 ? code :  "( " + code + " )";
	}
}
