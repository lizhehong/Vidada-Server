package com.elderbyte.vidada.domain.queries;

public abstract class LiteralValueExpression<T> extends Expression<T> {
	private final T value;

	public LiteralValueExpression(T value){
		this.value = value;
	}

	public T getValue(){
		return value;
	}

	@Override
	public abstract String code();
}
