package com.elderbyte.common.locations.local;

import com.elderbyte.common.locations.Credentials;
import com.elderbyte.common.locations.DirectoryLocation;
import com.elderbyte.common.locations.IResourceAccessContext;
import com.elderbyte.common.locations.ResourceLocation;
import com.elderbyte.common.locations.access.FileResourceAccessContext;
import com.elderbyte.vidada.streaming.FileRandomAccessInputStream;
import com.elderbyte.common.util.UriUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Embeddable;
import java.io.*;
import java.net.URI;

@Embeddable
public class LocalResourceLocation extends ResourceLocation {

	private static final Logger logger = LoggerFactory.getLogger(LocalResourceLocation.class);

    transient private File localFile;

	public LocalResourceLocation(File file, Credentials creditals) {
		this(UriUtil.createUri(file), creditals);
		localFile = file;
	}

	public LocalResourceLocation(URI uri, Credentials creditals) {
		super(ensureScheme(uri, "file"));
	}

	private File getFile(){
		if(localFile == null)
		{
			localFile = new File(getUri());
		}
		return localFile;
	}

	@Override
	public InputStream openInputStream() {
		try {
			return new FileRandomAccessInputStream(
					new RandomAccessFile(getFile(), "r"));
		} catch (FileNotFoundException e) {
            logger.error("", e);
		}
		return null;
	}

	@Override
	public OutputStream openOutputStream() {
		if(getFile() != null){
			try {
				if(!getFile().exists()){
					mkdirs();
					if(!getFile().createNewFile()){
						// Failed to create new file
					}
				}
				return new FileOutputStream(getFile());
			} catch (IOException e) {
                logger.error("", e);
			}
		}
		return null;
	}

	@Override
	public IResourceAccessContext openResourceContext() {
		return new FileResourceAccessContext(getUri(), getFile());
	}

	@Override
	public long length() {
		return getFile().length();
	}

	@Override
	public boolean exists() {
		return getFile().exists();
	}

	@Override
	public boolean isHidden() {
		if(getFile() != null){
			return getFile().isHidden();
		}
		return false;
	}

	@Override
	public boolean mkdirs() {
		if(getFile() != null){
			File parent = getFile().getParentFile();
			return parent.mkdirs();
		}
		return false;
	}

	@Override
	public DirectoryLocation getParent() {
		if(getFile() != null && getFile().getParent() != null){
			return new LocalDirectoryLocation(new File(getFile().getParent()), getCredentials());
		}
		return null;
	}

	@Override
	public boolean delete() {
		if(getFile() != null){
			try{
				return getFile().delete();
			}catch(SecurityException e){
                logger.error("", e);
			}
		}
		return false;
	}



}
