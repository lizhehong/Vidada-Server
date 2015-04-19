package com.elderbyte.vidada.domain.images.cache;

import archimedes.core.geometry.Size;
import archimedes.core.images.IMemoryImage;
import archimedes.core.util.Debug;
import archimedes.core.util.Lists;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.Comparator;
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
	public static IMemoryImage getRescaledInstance(IImageCache imageCache, String id, Size desiredSize ){

		IMemoryImage sourceImage, resizedImage = null;

		Size size = getBestMatchingSize(imageCache, id, desiredSize);

		if(size != null)
		{
			// Found an already rescaled image which is bigger

			sourceImage = imageCache.getImageById(id, size);
			if(sourceImage != null){
				if(desiredSize.equals(size)) {
					// The already cached image fits perfectly!
					resizedImage = sourceImage;
				}else{
					resizedImage =  sourceImage.rescale(desiredSize.width, desiredSize.height);
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
	private static Size getBestMatchingSize(IImageCache imageCache, String id, Size desiredSize){

		Set<Size> dims = imageCache.getCachedDimensions(id);

		if(!dims.isEmpty()){

			List<Size> dimensions = Lists.toList(dims);
			Collections.sort(dimensions, new Comparator<Size>() {
				@Override
				public int compare(Size o1, Size o2) {
					return Double.compare(o1.width, o2.width) ;
				}
			});

			int index = -1;
			for (int i = 0; i < dimensions.size(); i++) {
				if(dimensions.get(i).width >= desiredSize.width){
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
