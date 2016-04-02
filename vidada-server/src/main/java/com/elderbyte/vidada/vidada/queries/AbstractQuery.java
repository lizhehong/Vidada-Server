package com.elderbyte.vidada.vidada.queries;

/**
 * Represents an abstract query
 * @author IsNull
 *
 * @param <T> Type of queried entities
 */
public abstract class AbstractQuery<T> {

	/**
	 * Represents the type of the query
	 * @author IsNull
	 *
	 */
	public static enum QueryType {
		/**
		 * Performs a normal query according to the specified filters
		 */
		Query,

		/**
		 * Match all entities of this type
		 */
		All,
	}

	private QueryType type;
	transient private final Class<T> clazz;

	/**
	 * Creates a new abstract query
	 * @param type
	 * @param clazz
	 */
	protected  AbstractQuery(QueryType type, Class<T> clazz) {
		this.type = type;
		this.clazz = clazz;
	}

	/**
	 * Returns the query type
	 * @return
	 */
	public QueryType getType(){
		return type;
	}

	/**
	 * Returns the type of the queried entity
	 * @return
	 */
	public Class<T> getEntityType() {
		return clazz;
	}
}
