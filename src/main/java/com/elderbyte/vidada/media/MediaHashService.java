package com.elderbyte.vidada.media;

import archimedes.core.data.hashing.FileHashAlgorythms;
import archimedes.core.data.hashing.IFileHashAlgorythm;
import archimedes.core.io.locations.ResourceLocation;
import com.elderbyte.vidada.metadata.MediaMetaAttribute;
import com.elderbyte.vidada.VidadaSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

/**
 * Provides file-hash utility functions
 * @author IsNull
 *
 */
@Service
public class MediaHashService {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final IFileHashAlgorythm fileHashAlgorithm;
    private static final boolean forceUpdateMetaData = false;
    private final boolean useExtendedAttributes;
    private MediaMetaDataService metaDataService;

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     * Default Instance Constructor
     */
    @Autowired
	public MediaHashService(MediaMetaDataService metaDataService, VidadaSettings settings){
        this.metaDataService = metaDataService;
        this.fileHashAlgorithm = FileHashAlgorythms.instance().getButtikscheHashAlgorythm();
        this.useExtendedAttributes = settings.isUsingMetaData();
    }

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

	/**
	 * Returns the file-hash of the given file.
	 * This method invoke may take some time depending on the file and hash algorithm.
	 *
	 * Depending on the file system, file hashes can be stored in metadata which dramatically improves
	 * performance.
	 * @param mediaPath
	 * @return
	 */
	public String retrieveFileHash(ResourceLocation mediaPath){
		if(useExtendedAttributes && metaDataService.isMetaDataSupported(mediaPath)) {
            return retrieveFileHashMetaData(mediaPath);
        }else {
            return calculateHash(mediaPath);
        }
	}


    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/

	/**
	 * Retrieves the file hash using meta-data.
	 * If the hash is not yet stored in meta-data, it will be calculated and stored in meta-data.
	 * @param mediaPath
	 * @return
	 */
	private String retrieveFileHashMetaData(ResourceLocation mediaPath){

		String hash = null;

        if(!forceUpdateMetaData){
            hash = metaDataService.readMetaData(mediaPath, MediaMetaAttribute.FileHash);
        }

        if(hash == null)
		{
            // We could not read the hash from meta-data, we have to recalculate it

			hash = calculateHash(mediaPath);
            if(hash != null){
                if(metaDataService.writeMetaData(mediaPath, MediaMetaAttribute.FileHash, hash)){
                    LOG.info("Hash recalculated and written to meta-data: " + hash);
                }else{
                    LOG.warn("Could not write hash to meta-data attribute!");
                }
            }else{
                LOG.error("Hash could not be calculated for " + mediaPath);
            }
		}else{
            LOG.debug(String.format("Hash '%s' was retrieved from meta-data!", hash));
		}

		return hash;
	}

	/**
	 * Calculate the hash for the given media path
	 * @param mediaPath
	 * @return
	 */
	private String calculateHash(ResourceLocation mediaPath){
		String hash = null;
		InputStream is = null;
		try{
			is = mediaPath.openInputStream();
			hash = fileHashAlgorithm.calculateHashString(is, mediaPath.length());
		}catch(Exception e){
            LOG.error("Failed to read and calculate file hash!", e);
		}finally{
			if(is != null)
				try {
					is.close();
				} catch (IOException e) {
                    // ignore
				}
		}
		return hash;
	}

}
