package com.elderbyte.vidada.media;

import com.elderbyte.code.dom.expressions.ExpressionNode;
import com.elderbyte.vidada.media.libraries.MediaLibrary;

import java.util.ArrayList;
import java.util.List;

public class MediaExpressionQuery {

	private ExpressionNode tagsExpression;
    private MediaFilterType mediaType = MediaFilterType.ANY;
	private String keywords = null;
	private OrderProperty order = OrderProperty.FILENAME;
	private final List<MediaLibrary> allowedLibraries = new ArrayList<>();
	private boolean reverseOrder = false;



    public MediaExpressionQuery(ExpressionNode tagsExpression,
        MediaFilterType mediaType, String keywords, OrderProperty order, boolean reverseOrder) {
		super();
		this.tagsExpression = tagsExpression;
		this.mediaType = mediaType;
		this.keywords = keywords;
		this.order = order;
		this.reverseOrder = reverseOrder;
	}


	public ExpressionNode getTagsExpression() {
		return tagsExpression;
	}
	public MediaFilterType getMediaType() {
		return mediaType;
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

    public List<MediaLibrary> getAllowedLibraries() {
        return allowedLibraries;
    }

	public void setTagsExpression(ExpressionNode tagsExpression) {
		this.tagsExpression = tagsExpression;
	}
	public void setMediaType(MediaFilterType mediaType) {
		this.mediaType = mediaType;
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

	// Helper methods to check which restrictions this query defines


	public boolean hasKeyword() {
		return getKeywords() != null && !getKeywords().isEmpty();
	}

	public boolean hasMediaType() {
		return !MediaFilterType.ANY.equals(getMediaType());
	}

    public boolean hasAllowedLibraries() {
        return !getAllowedLibraries().isEmpty();
    }
}
