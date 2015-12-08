package com.elderbyte.server.vidada.tags.relations;

/**
 * Represents a relation operator between two tags
 *
 * left <rel Opr> right
 *
 * @author IsNull
 *
 */
public enum TagRelationOperator {
	/**
	 * The two tags are equal
	 */
	Equal,

	/**
	 * The left tag is the parent of the right one
	 */
	IsParentOf
}
