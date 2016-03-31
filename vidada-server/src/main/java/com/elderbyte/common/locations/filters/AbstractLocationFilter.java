package com.elderbyte.common.locations.filters;

import com.elderbyte.common.locations.DirectoryLocation;
import com.elderbyte.common.locations.UniformLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An abstract file filter strategy.
 *
 *
 * @author IsNull
 *
 * @param <T>
 */
public abstract class AbstractLocationFilter<T> {

    /**
     * List all files in the given root directory which match the provided filters.
     * @param root
     * @param filter
     * @param recursiveDirFilter
     * @return
     */
	public final List<UniformLocation> listAll(
			final T root,
			final ILocationFilter filter,
			final IDirectoryFilter recursiveDirFilter) throws IOException {

		List<UniformLocation> locations = new ArrayList<>();

		if(root == null) return locations;

		Iterable<T> files = nativeListFiles(root, filter, recursiveDirFilter);


		for (T file : files) {
			UniformLocation loc =  buildLocation(file);

			if(recursiveDirFilter != null && loc instanceof DirectoryLocation){

				// In case of a recursive scan, we yielded all allowed directories for recursion.
				// This means we have to re-check directories for the final list again.

				if(filter.accept(loc))
					locations.add(loc);

				// recursion
				locations.addAll(((DirectoryLocation)loc).listAll(filter, recursiveDirFilter));
			}else {
				locations.add(loc);
			}
		}

		return locations;
	}

	/**
	 * Filter the files for one directory level! (Non recursive)
	 * This filter handles the recursive cases as well, where folders generally are accepted.
	 *
	 * @param filter
	 * @param file
	 * @param recursiveDirFilter
	 * @return
	 */
	protected final boolean preAccept(final ILocationFilter filter, T file, final IDirectoryFilter recursiveDirFilter){
		boolean accept = false;

		UniformLocation loc = buildLocation(file);

		if(recursiveDirFilter != null && loc instanceof DirectoryLocation){
			accept = recursiveDirFilter.accept((DirectoryLocation)loc);
		}
		if(!accept)
			accept = filter.accept(loc);

		return accept;
	}


	/**
	 * List all files/folders in the given directory which are either accepted by the "return" filter
	 * or folders which are accepted by the directory filter. (Non recursive!)
	 *
	 * Usually, you can delegate the native filter call to any check/accept to <code>preAccept</code>
	 *
	 * @param directory
	 * @param filter
	 * @param recursiveDirFilter Is the current list request in recursive mode, i.e. shall folders be returned?
	 * So this parameter does not mean you should recurse into folders here!
	 * @return
	 */
	protected abstract Iterable<T> nativeListFiles(T directory, final ILocationFilter filter, final IDirectoryFilter recursiveDirFilter) throws IOException;

	/**
	 * Build a location from the given generic file type
	 * @param file
	 * @return
	 */
	protected abstract UniformLocation buildLocation(T file);


}
