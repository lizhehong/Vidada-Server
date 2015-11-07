package com.elderbyte.vidada.media;

import archimedes.core.exceptions.NotSupportedException;

import java.util.Comparator;

public class MediaComparator implements Comparator<MediaItem>{


	public static Comparator<MediaItem> build(OrderProperty order, boolean reverse){
		return new MediaComparator(order, reverse);
	}


	private final OrderProperty order;
	private final boolean reverse;

	public MediaComparator(OrderProperty order, boolean reverse){
		this.order = order;
		this.reverse = reverse;
	}

	@Override
	public int compare(MediaItem o1, MediaItem o2) {

		int cmp = 0;

		switch (order) {
		case FILENAME:
			cmp = o1.getFilename().compareTo(o2.getFilename());
			break;

		case OPENED:
			cmp = Integer.compare(o1.getOpened(), o2.getOpened());
			break;

		case ADDEDDATE:
			cmp = o1.getAddedDate().compareTo(o2.getAddedDate());
			break;

		case RATING:
			cmp = Integer.compare(o1.getRating(), o2.getRating());
			break;

		default:
			throw new NotSupportedException("Unknown order property: " + order);
		}

		if(cmp == 0 &&  !order.equals(OrderProperty.FILENAME)){
			cmp = o1.getFilename().compareTo(o2.getFilename());
		}

		return reverse ? cmp * -1 : cmp;
	}

}
