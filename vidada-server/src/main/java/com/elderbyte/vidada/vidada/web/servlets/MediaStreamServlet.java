package com.elderbyte.vidada.vidada.web.servlets;

import com.elderbyte.common.locations.ResourceLocation;
import com.elderbyte.vidada.vidada.media.MediaItem;
import com.elderbyte.vidada.vidada.media.MediaService;
import com.elderbyte.vidada.vidada.media.source.MediaSource;
import com.elderbyte.vidada.web.servlets.streaming.AbstractStreamServlet;
import com.elderbyte.vidada.web.servlets.streaming.StreamResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Streams media-data such as videos and images to the client.
 *
 */
public class MediaStreamServlet extends AbstractStreamServlet {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static Pattern streamPattern = Pattern.compile("stream/([^/]+)");


    @Autowired
    private MediaService mediaService;


    /**
     * Returns an absolute URL to this stream servlet which will stream the given media.
     * @param media The media for which the uri should be created
     * @return
     */
    public static String getAbsoluteStreamUrl(MediaItem media){

        // TODO We force http for max compatibility. The video will not play if the ssh certificate is self signed.

        return ServletUriComponentsBuilder.fromCurrentContextPath()
            .scheme("http")
            .port(8080)
            .path("/stream")
            .pathSegment(media.getFilehash()).toUriString();
    }


    @Override
    protected StreamResource getStreamResource(HttpServletRequest request, HttpServletResponse response) {

        String relativeUri = getRelativeURI(request);

        if(relativeUri != null) {

            Matcher matcher = streamPattern.matcher(relativeUri);

            if(matcher.find()){
                String hash =  matcher.group(1);
                MediaItem media = mediaService.findById(hash).orElse(null);

                if (media != null) {
                    MediaSource localSource = media.getSource();
                    if (localSource != null) {

                        onMediaStreamRequested(media);

                        ResourceLocation resource = localSource.getResourceLocation();
                        return new StreamResource(
                            localSource.getName(),
                            resource.length(),
                            Integer.MAX_VALUE,
                            () -> resource.openInputStream(),
                            localSource.getMimeType());
                    } else {
                        logger.error("Server: Stream - Media has no source! relative uri was: " + relativeUri);
                    }
                } else {
                    logger.warn("Requested media '"  + hash + " could not be found. uri: " + relativeUri);
                }
            }else{
                logger.warn("No stream hash found in uri: " + relativeUri);
            }
        }

        return null;
    }

    /**
     * Occurs when the given media is requested for stream / access
     * @param mediaItem
     */
    private void onMediaStreamRequested(MediaItem mediaItem){
        mediaService.mediaAccessed(mediaItem);
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
