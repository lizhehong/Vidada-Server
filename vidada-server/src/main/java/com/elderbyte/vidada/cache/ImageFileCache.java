package com.elderbyte.vidada.cache;


import com.elderbyte.common.locations.DirectoryLocation;
import com.elderbyte.common.locations.ResourceLocation;
import com.elderbyte.vidada.images.IMemoryImage;
import com.elderbyte.vidada.images.IRawImageFactory;
import com.elderbyte.vidada.media.Resolution;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.*;


/**
 * A basic implementation of a image file cache,
 * using the current OS file system to persist and manage the images.
 *
 * @author IsNull
 *
 */
public class ImageFileCache implements IImageCache {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LogManager.getLogger(ImageFileCache.class.getName());

    private static final String RESOLUTION_DELEMITER = "_";

	private final DirectoryLocation cacheRoot;
	private final DirectoryLocation scaledCacheDataBase;

	private final IRawImageFactory imageFactory;

	// file path caches
	private final Map<Integer, ResourceLocation> dimensionPathCache = new HashMap<>(2000);
	private final Map<Resolution, DirectoryLocation> resolutionFolders = new HashMap<>(10);

	private final Set<Resolution> knownDimensions = new HashSet<>();
	private final Object knownDimensionsLOCK = new Object();

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

	/**
	 * Creates a new image cache which uses the file system to save images.
	 *
	 * This is a basic implementation of the image file cache,
	 * using the current OS file system to persist and manage the images.
	 *
	 * @param cacheRoot The root folder for the cache
	 */
	public ImageFileCache(IRawImageFactory imageFactory, DirectoryLocation cacheRoot){

        this.imageFactory = imageFactory;
		this.cacheRoot = cacheRoot;
		scaledCacheDataBase = buildScaledCacheFolder(cacheRoot);

        knownDimensions.addAll( readExistingScaleDimensions(scaledCacheDataBase) );
	}

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

	public DirectoryLocation getCacheRoot() {
		return cacheRoot;
	}

	/**
	 * Returns all cached image dimensions for the given id.
	 */
	@Override
	public Set<Resolution> getCachedDimensions(String id) {

		Set<Resolution> avaiableForId = new HashSet<>();

        try {
            for (Resolution knownDim : getKnownDimensions()) {
                if (getFilePath(id, knownDim).exists()) {
                    avaiableForId.add(knownDim);
                }
            }
        }catch (IOException e){
            logger.error(e);
        }

		return avaiableForId;
	}

	@Override
	public IMemoryImage getImageById(String id, Resolution size) {

		IMemoryImage thumbnail = null;
        ResourceLocation cachedPath = getFilePath(id, size);
        try {
            if(cachedPath.exists())
            {
                thumbnail = load(cachedPath);
            }
        }catch (IOException e){
            logger.error("Can not read cached image" + cachedPath, e);
        }

		return thumbnail;
	}



	/**
	 * Store a new image. It will be handled as the new native image.
	 */
	@Override
	public void storeImage(String id, IMemoryImage image) {

		if(image == null)
			throw new IllegalArgumentException("image can not be null");


        Resolution imageSize = new Resolution(image.getWidth(), image.getHeight());
		ResourceLocation outputfile = getFilePath(id, imageSize);

		persist(image, outputfile);

		synchronized (knownDimensionsLOCK) {
			knownDimensions.add(imageSize);
		}
	}




	@Override
	public boolean exists(String id, Resolution size) {
        try {
            ResourceLocation cachedPath = getFilePath(id, size);
            //
            // The appropriate thing here would be Files.isReadable( cachedPath );
            // However, isReadable seems to have a very big performance hit when called in frequent rounds.

            return cachedPath.exists();
        } catch (IOException e) {
            logger.warn("Error while checking existence!", e);
            return false;
        }
    }



	@Override
	public void removeImage(String id) {

		ResourceLocation path;
		for (Resolution knownDim : getKnownDimensions())
		{
			path = getFilePath(id, knownDim);
            try {
                path.delete();
            } catch (IOException e) {
                logger.error(e);
            }
        }
	}

    /**
     * Gets the image extension to use for cached files
     * @return
     */
    public String getImageExtension(){
        return ".png";
    }


    /***************************************************************************
     *                                                                         *
     * Protected methods                                                       *
     *                                                                         *
     **************************************************************************/

    /**
     * Gets all known dimensions in this cache
     * @return
     */
    protected Set<Resolution> getKnownDimensions(){
        synchronized (knownDimensionsLOCK) {
            return new HashSet<>(this.knownDimensions);
        }
    }

	/**
	 * Load an image file from the disk
	 * @param path
	 * @return
	 */
	protected final IMemoryImage load(ResourceLocation path) throws IOException{
        InputStream is = openImageStream(path);
        try {
            IMemoryImage image = imageFactory.createImage(is);
            return image;
        }catch (Exception e){
            throw e;
        }finally {
            if(is != null) is.close();
        }
	}

	/**
	 * Open a InputStream which must return the raw image bytes
	 * @param path
	 * @return
	 */
	protected InputStream openImageStream(ResourceLocation path) throws IOException {
		return path.openInputStream();
	}

	protected byte[] retrieveBytes(IMemoryImage image){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		imageFactory.writeImage(image, out);
		return out.toByteArray();
		// no need for byte array stream to be closed explicitly
	}


	/**
	 * Writes the given image to disk
	 * @param image
	 * @param path
	 */
	protected final void persist(IMemoryImage image, ResourceLocation path){

        try {

            byte[] rawImageData = retrieveBytes(image);

            if(rawImageData != null && rawImageData.length > 0){

                path.mkdirs();
                OutputStream fos = null;
                try {
                    fos =  path.openOutputStream();
                    if(fos != null) {
                        fos.write(rawImageData);
                    }else{
                        logger.error(String.format("Failed to write image data %s bytes to file '%s'", rawImageData.length, path.getPath()));
                    }
                } catch (FileNotFoundException e) {
                    logger.error(e);
                }finally {
                    if(fos != null)
                        fos.close();
                }
            }else{
                logger.warn("Failed to persist image because no image bytes could be extracted!");
            }

        }catch (IOException e){
            logger.error("Failed to persist the image!", e);
        }

    }


	/**
	 * Gets the filepath for the given image id in the given resolution
	 * @param id
	 * @param size
	 * @return
	 */
	protected ResourceLocation getFilePath(String id, Resolution size){

		int combindedHash = id.hashCode() * 31 ^ size.hashCode();

		ResourceLocation cachedThumb = dimensionPathCache.get(combindedHash);

		if(cachedThumb == null)
		{
			DirectoryLocation folder = getFolderForResolution( size );
			try {
				cachedThumb = ResourceLocation.Factory.create(folder, id + getImageExtension());
				dimensionPathCache.put(combindedHash, cachedThumb);
			} catch (URISyntaxException e) {
				logger.error(e);
			}
		}

		return cachedThumb;
	}

    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/


    /**
     * Scans the given folder for dimension subfolders
     * @param scaledCacheDataBase
     */
    private Set<Resolution> readExistingScaleDimensions(DirectoryLocation scaledCacheDataBase) {

        Set<Resolution> existingDimensions = new HashSet<>();
        List<DirectoryLocation> resolutionFolders = null;
        try {
            resolutionFolders = scaledCacheDataBase.listDirs();
            if(resolutionFolders != null && !resolutionFolders.isEmpty())
            {
                for (DirectoryLocation folder : resolutionFolders) {
                    String[] parts  = folder.getName().split(RESOLUTION_DELEMITER);
                    if(parts.length == 2){
                        try {
                            existingDimensions.add(new Resolution(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])));
                        } catch (NumberFormatException e) {
                            // Ignore wrong (NON resolution) folders
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Failed to retrive existing cache dimension folder!", e);
        }


        return existingDimensions;
    }

	private DirectoryLocation getFolderForResolution(Resolution size){

		DirectoryLocation resolutionFolder = resolutionFolders.get(size);

		if(resolutionFolder == null){

			String resolutionName = size.getWidth() + RESOLUTION_DELEMITER + size.getHeight();

			try {
				resolutionFolder = DirectoryLocation.Factory
						.create(scaledCacheDataBase, resolutionName);
			} catch (URISyntaxException e) {
                logger.error("Failed to create resolutionFolder! '" + scaledCacheDataBase + "/" + resolutionName + "'", e);
			}
			resolutionFolders.put(size, resolutionFolder);
		}

		return resolutionFolder;
	}

    private static DirectoryLocation buildScaledCacheFolder(DirectoryLocation cacheRoot){
        try {
            DirectoryLocation folder = DirectoryLocation.Factory.create(cacheRoot, "scaled");
            folder.mkdirs();
            return folder;
        } catch (URISyntaxException e1) {
            logger.error(e1);
        } catch (IOException e) {
            logger.error(e);
        }
        return null;
    }

}
