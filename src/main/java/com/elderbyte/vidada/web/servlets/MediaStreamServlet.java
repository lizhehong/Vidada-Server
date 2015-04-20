package com.elderbyte.vidada.web.servlets;

import archimedes.core.io.locations.ResourceLocation;
import com.elderbyte.vidada.domain.media.MediaItem;
import com.elderbyte.vidada.domain.media.source.MediaSource;
import com.elderbyte.vidada.service.media.MediaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Streams media data
 */
public class MediaStreamServlet extends AbstractStreamServlet {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MediaService mediaService;


    @Override
    protected StreamResource getStreamResource(HttpServletRequest request, HttpServletResponse response) {

        String relativeUri = getRelativeURI(request);

        ResourceLocation resource = null;
        if(relativeUri != null) {
            String[] parts = relativeUri.split("/");
            if (parts.length > 1) {
                String hash = parts[1];
                MediaItem media = mediaService.queryByHash(hash);

                if (media != null) {
                    MediaSource localSource = media.getSource();
                    if (localSource != null) {
                        resource = localSource.getResourceLocation();
                        return new StreamResource(media.getFilename(), resource.length(), Integer.MAX_VALUE, resource);
                    } else {
                        logger.error("Server: Stream - Media has no source! relative uri was: " + relativeUri);
                    }
                } else {
                    logger.warn("Requested media '" + relativeUri + "' could not be found. Hash = " + hash + " relative uri: " + relativeUri);
                }
            }
        }

        return null;
    }


    /**
     * Get the relative URI of this request
     */
    protected String getRelativeURI(final HttpServletRequest request) {
        String uri = request.getRequestURI();

        final String resourcesContextPath = request.getContextPath();
        if (resourcesContextPath != null && !resourcesContextPath.isEmpty()) {
            if (!uri.startsWith(resourcesContextPath)) {
                return null;
            }
            uri = uri.substring(resourcesContextPath.length());
        }

        return uri;
    }

}
