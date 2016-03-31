package archimedes.core.io.locations.filters;

import archimedes.core.io.locations.UniformLocation;

/**
 * Location filter for files and or folders
 * @author IsNull
 *
 */
public interface ILocationFilter {

	/**
	 * Does the given location match this filter?
	 * @param location
	 * @return
	 */
	public boolean accept(UniformLocation location);
}
