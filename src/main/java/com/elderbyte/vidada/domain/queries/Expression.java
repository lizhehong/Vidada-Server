package com.elderbyte.vidada.domain.queries;

public abstract class Expression<T> {

	/**
	 * Generates source code
	 * @return
	 */
	public abstract String code();


	@Override
	public String toString(){
		return code();
	}
}
