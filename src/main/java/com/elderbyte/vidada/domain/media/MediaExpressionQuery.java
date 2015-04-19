package com.elderbyte.vidada.domain.media;

import com.elderbyte.vidada.domain.queries.Expression;
import com.elderbyte.vidada.domain.tags.Tag;

public class MediaExpressionQuery {

	private Expression<Tag> tagsExpression;
    private MediaType mediaType = MediaType.ANY;
	private String keywords = null;
	private OrderProperty order = OrderProperty.FILENAME;
	private boolean onlyAvailable = false;
	private boolean reverseOrder = false;


	public MediaExpressionQuery(Expression<Tag> tagsExpression,
			MediaType mediaType, String keywords, OrderProperty order,
			boolean onlyAvailable, boolean reverseOrder) {
		super();
		this.tagsExpression = tagsExpression;
		this.mediaType = mediaType;
		this.keywords = keywords;
		this.order = order;
		this.onlyAvailable = onlyAvailable;
		this.reverseOrder = reverseOrder;
	}


	public Expression<Tag> getTagsExpression() {
		return tagsExpression;
	}
	public MediaType getMediaType() {
		return mediaType;
	}
	public String getKeywords() {
		return keywords;
	}
	public OrderProperty getOrder() {
		return order;
	}
	public boolean isOnlyAvailable() {
		return onlyAvailable;
	}
	public boolean isReverseOrder() {
		return reverseOrder;
	}
	public void setTagsExpression(Expression<Tag> tagsExpression) {
		this.tagsExpression = tagsExpression;
	}
	public void setMediaType(MediaType mediaType) {
		this.mediaType = mediaType;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public void setOrder(OrderProperty order) {
		this.order = order;
	}
	public void setOnlyAvailable(boolean onlyAvailable) {
		this.onlyAvailable = onlyAvailable;
	}
	public void setReverseOrder(boolean reverseOrder) {
		this.reverseOrder = reverseOrder;
	}

	// Helper methods to check which restrictions this query defines


	public boolean hasKeyword() {
		return getKeywords() != null && !getKeywords().isEmpty();
	}

	public boolean hasMediaType() {
		return !MediaType.ANY.equals(getMediaType());
	}
}
