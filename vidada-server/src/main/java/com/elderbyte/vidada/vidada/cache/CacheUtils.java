package com.elderbyte.vidada.vidada.cache;

import com.elderbyte.vidada.vidada.images.IMemoryImage;
import com.elderbyte.vidada.vidada.media.Resolution;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Provides some image cache extension methods
 *
 * @author IsNull
 *
 */
public class CacheUtils {

    private static final Logger logger = LogManager.getLogger(CacheUtils.class.getName());


	/**
	 * Try to create a rescaled image of the given size.
	 *
	 * This method tries to abuse existing cached images to minimize
	 * image processing and deliver maximal speed at high quality
	 *
	 * @param imageCache The image cache
	 * @param id
	 * @param desiredSize
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static IMemoryImage getRescaledInstance(IImageCache imageCache, String id, Resolution desiredSize ){

		IMemoryImage sourceImage, resizedImage = null;

		Resolution size = getBestMatchingSize(imageCache, id, desiredSize);

		if(size != null)
		{
			// Found an already rescaled image which is bigger
            logger.debug("Found an already extracted thumb which is "+size+", so will use this as source.");

			sourceImage = imageCache.getImageById(id, size);
			if(sourceImage != null){
				if(desiredSize.equals(size)) {
					// The already cached image fits perfectly!
					resizedImage = sourceImage;
				}else{
                    logger.debug("The existing image is too big, so will scale it down to fit the desired size " + desiredSize+ "!");
                    resizedImage =  sourceImage.rescale(desiredSize.getWidth(), desiredSize.getHeight());
				}
			}else{
				logger.warn("The expected thumb " + id + " with size " + size + " was not found in the image cache!");
			}
		}
		return resizedImage;
	}

	/**
	 * Returns a size of a already cached image which is as close to the desired size, but bigger or equal.
	 *
	 * @param imageCache
	 * @param id
	 * @param desiredSize
	 * @return
	 */
	private static Resolution getBestMatchingSize(IImageCache imageCache, String id, Resolution desiredSize){

		Set<Resolution> dims = imageCache.getCachedDimensions(id);

		if(!dims.isEmpty()){

            List<Resolution> dimensions = new ArrayList<>(dims);
			Collections.sort(dimensions, (o1, o2) -> Double.compare(o1.getWidth(), o2.getWidth()));

			int index = -1;
			for (int i = 0; i < dimensions.size(); i++) {
				if(dimensions.get(i).getWidth() >= desiredSize.getWidth()){
					index = i;
					break;
				}
			}
			if(index != -1 )
				return dimensions.get(index);
		}

		return null;
	}

}
