package com.elderbyte.vidada.media;

import com.elderbyte.vidada.queries.AbstractQuery;

/**
 * Represents an abstract media query.
 *
 * This query will be translated into a storage specific query for execution.
 * @author IsNull
 *
 */
public class MediaQuery extends AbstractQuery<MediaItem> {

	/**
	 * Represents a query which will match all entities
	 */
	public static final MediaQuery ALL = new MediaQuery(AbstractQuery.QueryType.All);

	private MediaFilterType selectedtype = MediaFilterType.ANY;
	private String keywords = null;
	private OrderProperty order = OrderProperty.TITLE;
	private boolean onlyAvailable = false;
	private boolean reverseOrder = false;
    private String tagExpression = "";


    /**
	 * Creates a media query with the following
	 * @param selectedType
	 * @param keywords
	 * @param order
	 * @param onlyAvailable
	 * @param reverseOrder
	 */
	public MediaQuery(MediaFilterType selectedType, String keywords,
			OrderProperty order, String tagExpression, boolean onlyAvailable,
			boolean reverseOrder) {
		this(AbstractQuery.QueryType.Query);
		this.selectedtype = selectedType;
		this.keywords = keywords;
		this.order = order;
		this.onlyAvailable = onlyAvailable;
		this.reverseOrder = reverseOrder;
        this.tagExpression = tagExpression;
	}

	public MediaQuery(){
		super(QueryType.Query, MediaItem.class);
	}

	protected MediaQuery(AbstractQuery.QueryType type) {
		super(type, MediaItem.class);
	}


	public MediaFilterType getMediaType() {
		return selectedtype;
	}

	public String getKeywords() {
		return keywords;
	}

	public OrderProperty getOrder() {
		return order;
	}

	public boolean isReverseOrder() {
		return reverseOrder;
	}

	public void setSelectedtype(MediaFilterType selectedtype) {
		this.selectedtype = selectedtype;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public void setOrder(OrderProperty order) {
		this.order = order;
	}

	public void setReverseOrder(boolean reverseOrder) {
		this.reverseOrder = reverseOrder;
	}

	public boolean isOnlyAvailable() {
		return onlyAvailable;
	}

    public void setOnlyAvailable(boolean onlyAvailable) {
        this.onlyAvailable = onlyAvailable;
    }



    // Helper methods to check which restrictions this query defines


    public boolean hasKeyword() {
		return getKeywords() != null && !getKeywords().isEmpty();
	}

	public boolean hasMediaType() {
		return !MediaFilterType.ANY.equals(getMediaType());
	}

    public void setTagExpression(String tagExpression) {
        this.tagExpression = tagExpression;
    }

    public String getTagExpression(){
        return this.tagExpression;
    }
}
