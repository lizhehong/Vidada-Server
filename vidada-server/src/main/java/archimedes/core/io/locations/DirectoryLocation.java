package archimedes.core.io.locations;

import archimedes.core.io.locations.factories.DirectoryLocationFactory;
import archimedes.core.io.locations.filters.IDirectoryFilter;
import archimedes.core.io.locations.filters.ILocationFilter;


import javax.persistence.Embeddable;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a directory location
 * @author IsNull
 *
 */
@Embeddable
public abstract class DirectoryLocation extends UniformLocation {

	public transient static DirectoryLocationFactory Factory = new DirectoryLocationFactory();

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new DirectoryLocation
     * @param uri
     * @param credentials
     */
	protected DirectoryLocation(URI uri, Credentials credentials) {
		super(ensureEndingSlash(uri), credentials);
	}

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/


	/**
	 * List all files/folders in this directory
	 * @return
	 */
	public final List<UniformLocation> listFilesAndFolders() throws IOException {
		return listAll(LocationFilters.AcceptAll, null);
	}

	/**
	 * List all resource locations in this directory
	 * @return
	 */
	public final List<ResourceLocation> listFiles() throws IOException {
        List<ResourceLocation> locations=new ArrayList<>();
        for (UniformLocation lu : listAll(LocationFilters.AcceptAllDirs, null)) {
            locations.add((ResourceLocation)lu);
        }
        return locations;
	}

	/**
	 * List all directory locations in this directory
	 * @return
	 */
	public final List<DirectoryLocation> listDirs() throws IOException {
        List<DirectoryLocation> locations=new ArrayList<>();
        for (UniformLocation lu : listAll(LocationFilters.AcceptAllDirs, null)) {
            locations.add((DirectoryLocation)lu);
        }
		return locations;
	}

	/**
	 * List all files / folders in this directory which match the given filter.
	 * This is a shorthand for <code>listAll(ILocationFilter returnFilter, null)</code>
	 * @param returnFilter
	 * @return
	 */
	public List<UniformLocation> listAll(ILocationFilter returnFilter) throws IOException {
		return listAll(returnFilter, null);
	}


	/**
	 *  List all items depending on the location filter
	 *
	 * @param returnFilter This filter specifies which files/folders are being returned
	 * @param recursiveDirFilter This filter specifies the handling of recursion.
	 * <code>null</code> will cause no recursion.
	 *
	 * @return
	 */
	public abstract List<UniformLocation> listAll(ILocationFilter returnFilter, IDirectoryFilter recursiveDirFilter) throws IOException;
}
