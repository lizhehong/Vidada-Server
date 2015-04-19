package com.elderbyte.vidada.domain.queries;

/**
 * Represents an reference to a variable, such as:
 *
 * x
 * x.y
 * x[0]
 *
 * @author IsNull
 *
 * @param <T>
 */
public abstract class VariableReferenceExpression<T> extends Expression<T> {

	public static <T> VariableReferenceExpression<T> build(String ref){
		return new SimpleVariableReferenceExpression<T>(ref);
	}

	private static class SimpleVariableReferenceExpression<T> extends VariableReferenceExpression<T>{

		private final String refStr;
		public SimpleVariableReferenceExpression(String refStr){
			this.refStr = refStr;
		}
		@Override
		public String code() {
			return refStr;
		}
	}
}
