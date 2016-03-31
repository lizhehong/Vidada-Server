package com.elderbyte.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single page of a paged list.
 * @author IsNull
 *
 * @param <T>
 */
@Deprecated // TODO Replace with spring pagination
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ListPage<T> {

	/***************************************************************************
     *                                                                         *
	 * Static                                                                  *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * Returns an empty page.
	 * @return
     */
	public static <T> ListPage<T> empty(){
		return new ListPage<>(new ArrayList<>(), 0, 0, 0);
	}

    /***************************************************************************
     *                                                                         *
     * Private fields                                                          *
     *                                                                         *
     **************************************************************************/

	private int page;
	private long totalListSize;
	private int maxPageSize;

	@XmlAnyElement(lax=true)
	private List<T> pageItems;


    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     * ORM and Serialisation Empty Constructor
     */
    protected ListPage() { }

	/**
	 * Creates a new ListPage
	 * @param pageItems The items of this page. Must not be null.
	 * @param totalListSize The total size of all elements.
	 * @param maxPageSize Max Items per page.
	 * @param page The current page index
	 */
	public ListPage(List<T> pageItems, long totalListSize, int maxPageSize, int page) {

		if(pageItems == null) throw new IllegalArgumentException("pageItems must not be NULL!");

		this.pageItems = pageItems;
		this.totalListSize = totalListSize;
		this.maxPageSize = maxPageSize;
		this.page = page;
	}


    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/

	/**
	 * Get all items of this page
	 * @return
	 */
	public List<T> getPageItems() {
		return pageItems;
	}

	/**
	 * Get the size of the full result set / list
	 * @return
	 */
	public long getTotalListSize() {
		return totalListSize;
	}

	/**
	 * Get the max item count per page
	 * @return
	 */
	public int getMaxPageSize() {
		return maxPageSize;
	}

	/**
	 * Get the page number (zero based index)
	 * @return
	 */
	public int getPage() {
		return page;
	}

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

	/**
	 * Gets the element at the real index position.
	 * That means you can use the index u would have used to access the real list.
	 * @param index
	 * @return
	 */
	T getByRealIndex(int index) {
		int pageStartIndex = getPage() * getMaxPageSize();
		int localIndex = index - pageStartIndex;

		if(localIndex < 0)
			throw new IllegalArgumentException("The given real index "+ index +" is too small for this page (" + getPage() + ").");

		if(localIndex >= getPageItems().size())
			throw new IllegalArgumentException("The given real index "+ index +" is too high for this page (" + getPage() + ").");

		return getPageItems().get(localIndex);
	}

    @Override
    public String toString(){
        return "[Items: " +  getPageItems().size() + ", Page: " + getPage() + ", Total Count: " + getTotalListSize() + ", MaxPageSize: " +maxPageSize+  "]";
    }

    /***************************************************************************
     *                                                                         *
     * Protected methods                                                       *
     *                                                                         *
     **************************************************************************/

	protected void setPageItems(List<T> pageItems) {
		this.pageItems = pageItems;
	}
	protected void setTotalListSize(long listSize) {
		this.totalListSize = listSize;
	}
	protected void setPageSize(int pageSize) {
		this.maxPageSize = pageSize;
	}
	protected void setPage(int page) {
		this.page = page;
	}

}
