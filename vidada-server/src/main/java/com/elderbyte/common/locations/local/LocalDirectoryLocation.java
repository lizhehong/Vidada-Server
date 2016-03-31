package com.elderbyte.common.locations.local;

import com.elderbyte.common.locations.Credentials;
import com.elderbyte.common.locations.DirectoryLocation;
import com.elderbyte.common.locations.UniformLocation;
import com.elderbyte.common.locations.filters.AbstractLocationFilter;
import com.elderbyte.common.locations.filters.IDirectoryFilter;
import com.elderbyte.common.locations.filters.ILocationFilter;
import com.elderbyte.common.NotSupportedException;
import com.elderbyte.common.util.UriUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Embeddable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Embeddable
public class LocalDirectoryLocation extends DirectoryLocation {

	private static final Logger logger = LoggerFactory.getLogger(LocalDirectoryLocation.class);


    transient private File folder;

	public LocalDirectoryLocation(){ super(null, null); }

	public LocalDirectoryLocation(File file, Credentials creditals) {
		this(UriUtil.createUri(file), creditals);
		folder = file;
	}

	public LocalDirectoryLocation(URI uri, Credentials creditals) {
		super(ensureScheme(uri, "file"), creditals);
	}


	@Override
	public List<UniformLocation> listAll(final ILocationFilter filter, final IDirectoryFilter recursionFilter) throws IOException {
		return locationFilter.listAll(getFolder(), filter, recursionFilter);
	}


	@Override
	public boolean exists() {
		if(getFolder() != null){
			return getFolder().exists();
		}
		return false;
	}

	@Override
	public boolean isHidden() {
		if(getFolder() != null){
			return getFolder().isHidden();
		}
		return false;
	}

	@Override
	public boolean mkdirs() {
		if(getFolder() != null){
			return getFolder().mkdirs();
		}
		return false;
	}

	@Override
	public String getName() {
		if(getFolder() != null){
			return getFolder().getName();
		}
		return null;
	}

	@Override
	public DirectoryLocation getParent() {
		if(getFolder() != null && getFolder().getParent() != null){
			return new LocalDirectoryLocation(new File(getFolder().getParent()), getCredentials());
		}
		return null;
	}

	@Override
	public boolean delete() {
		if(getFolder() != null){
			return getFolder().delete();
		}
		return false;
	}

	private File getFolder(){
		if(folder == null){
			folder = new File(getUri());
		}
		return folder;
	}


    /**
     *
     */
	transient private final AbstractLocationFilter<File> locationFilter = new AbstractLocationFilter<File>(){

		@Override
		protected UniformLocation buildLocation(File file) {
			if(file.isDirectory()){
				return new LocalDirectoryLocation(file, getCredentials());
			}else if(file.isFile()){
				return new LocalResourceLocation(file, getCredentials());
			}else {
				throw new NotSupportedException("File type not supported: " + file);
			}
		}

		@Override
		protected Iterable<File> nativeListFiles(File directory,
				final ILocationFilter filter, final IDirectoryFilter recursionFilter) {

			File[] files = directory.listFiles(pathname -> {
                return preAccept(filter, pathname, recursionFilter);
            });
			return Arrays.asList(files);
		}
	};

}
