package com.elderbyte.common.locations.filters;

import com.elderbyte.common.locations.DirectoryLocation;

public interface IDirectoryFilter {
	/**
	 *
	 * @param location
	 * @return
	 */
	public boolean accept(DirectoryLocation location);
}
