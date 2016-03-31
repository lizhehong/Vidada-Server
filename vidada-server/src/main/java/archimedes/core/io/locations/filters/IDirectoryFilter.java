package archimedes.core.io.locations.filters;

import archimedes.core.io.locations.DirectoryLocation;

public interface IDirectoryFilter {
	/**
	 * 
	 * @param location
	 * @return
	 */
	public boolean accept(DirectoryLocation location);
}
