package com.elderbyte.server.vidada.tags.relations;


import com.elderbyte.server.vidada.tags.Tag;

/**
 * Represents a relation between two tags.
 * Immutable implementation.
 *
 * @author IsNull
 *
 */
public class TagRelation {

	private final Tag left;
	private final TagRelationOperator operator;
	private final Tag right;

	public TagRelation(Tag left, TagRelationOperator operator, Tag right) {
		super();

		if(left == null) throw new IllegalArgumentException("left must not be null!");
		if(right == null) throw new IllegalArgumentException("right must not be null!");

		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	public Tag getLeft() {
		return left;
	}

	public TagRelationOperator getOperator() {
		return operator;
	}

	public Tag getRight() {
		return right;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result
				+ ((operator == null) ? 0 : operator.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TagRelation other = (TagRelation) obj;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (operator != other.operator)
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		return true;
	}

	public String toString(){
		return getLeft()+" " + getOperator() + " " + getRight();
	}

}
