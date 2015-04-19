package com.elderbyte.vidada.domain.queries;


public class MemberOfExpression<T> extends Expression<T> {

	private static final String CODE_not = "NOT";
	private static final String CODE_memberOf = "MEMBER OF";

	private final boolean not;
	private final LiteralValueExpression<?> item;
	private final VariableReferenceExpression<T> collectionRef;

	public MemberOfExpression(LiteralValueExpression<?> item, VariableReferenceExpression<T> collection){
		this(item, collection, false);
	}

	public MemberOfExpression(LiteralValueExpression<?> item, VariableReferenceExpression<T> collection, boolean not){
		this.item = item;
		this.collectionRef = collection;
		this.not = not;
	}

	@Override
	public String code() {
		return item.code() + " " + (not ? CODE_not + " " : "" ) + CODE_memberOf + " " + collectionRef;
	}
}
