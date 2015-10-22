package com.elderbyte.vidada.service.media;

import archimedes.core.data.pagination.ListPage;
import archimedes.core.events.EventArgs;
import archimedes.core.events.EventHandlerEx;
import archimedes.core.events.IEvent;
import archimedes.core.exceptions.NotSupportedException;
import archimedes.core.io.locations.ResourceLocation;
import com.elderbyte.code.CodeDomException;
import com.elderbyte.code.dom.expressions.ExpressionNode;
import com.elderbyte.vidada.domain.media.MediaExpressionQuery;
import com.elderbyte.vidada.domain.media.MediaItem;
import com.elderbyte.vidada.domain.media.MediaLibrary;
import com.elderbyte.vidada.domain.media.MediaQuery;
import com.elderbyte.vidada.domain.queries.TagExpressionBuilder;
import com.elderbyte.vidada.repository.MediaRepository;
import com.elderbyte.vidada.service.MediaLibraryService;
import com.elderbyte.vidada.service.tags.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

@Service
public class MediaService {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


	private final MediaRepository repository;
    private final MediaLibraryService mediaLibraryService;
    private final TagService tagService;
    private final MediaHashService mediaHashService;

    /***************************************************************************
     *                                                                         *
     * Events                                                                  *
     *                                                                         *
     **************************************************************************/

    private final EventHandlerEx<EventArgs> mediasChangedEvent = new EventHandlerEx<>();


    public IEvent<EventArgs> getMediasChangedEvent() { return mediasChangedEvent;  }

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new MediaService
     *
     * @param repository
     * @param mediaLibraryService
     * @param tagService
     */
    @Inject
    public MediaService(
            MediaRepository repository,
            MediaLibraryService mediaLibraryService,
            TagService tagService,
            MediaHashService mediaHashService) {
		this.repository = repository;
        this.mediaLibraryService = mediaLibraryService;
        this.tagService = tagService;
        this.mediaHashService = mediaHashService;
	}

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    @Transactional
	public void store(final MediaItem media) {
        repository.save(media);
        fireMediasChanged();
	}

    @Transactional
	public void store(final Collection<MediaItem> medias) {
        repository.save(medias);
        fireMediasChanged();
	}

    @Transactional
	public void update(final MediaItem media) {
        repository.save(media);
	}

    @Transactional
	public void update(final Collection<MediaItem> medias) {
        repository.save(medias);
	}

    @Transactional
	public ListPage<MediaItem> query(final MediaQuery qry, final int pageIndex, final int maxPageSize) {

        ExpressionNode tagExpression = null;

        if(qry.getTagExpression() != null && !qry.getTagExpression().isEmpty()){

            try {
                tagExpression = TagExpressionBuilder.create()
                    .expandTags(tagService::getAllRelatedTags)
                        // Add additional related tags to the users request -
                        // This is what vidada makes intelligent
                    .build(qry.getTagExpression());
            }catch (CodeDomException e){
                logger.warn("Could not create Tag-Expression query!", e);
            }

        }

        MediaExpressionQuery exprQuery = new MediaExpressionQuery(
                tagExpression,
                qry.getMediaType(),
                qry.getKeywords(),
                qry.getOrder(),
                qry.isOnlyAvailable(),
                qry.isReverseOrder());

        return repository.query(exprQuery, pageIndex, maxPageSize);
    }


    @Transactional
	public List<MediaItem> getAllMedias(){
		return repository.findAll();
	}

    @Transactional
	public void delete(final MediaItem media) {
        repository.delete(media);
        fireMediasChanged();
	}

    @Transactional
	public void delete(final Collection<MediaItem> media) {
        repository.delete(media);
        fireMediasChanged();
	}

    public void fireMediasChanged() {
        mediasChangedEvent.fireEvent(this, EventArgs.Empty);
    }


	public MediaItem findOrCreateMedia(ResourceLocation file, boolean persist) {
		return findAndCreateMedia(file, true, persist);
	}

    @Transactional
    public int count() {
        return (int)repository.count();
    }


    @Transactional
    public MediaItem queryByHash(final String hash) {
        return repository.findOneByFilehash(hash).orElse(null);
    }


    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/

    /**
     * Search for the given media data by the given absolute path
     */
    private MediaItem findMediaData(ResourceLocation file) {
        return findAndCreateMedia(file, false, false);
    }

	/**
	 *
	 * @param resource
	 * @param canCreate
	 * @param persist
	 * @return
	 */
    @Transactional
	protected MediaItem findAndCreateMedia(final ResourceLocation resource, final boolean canCreate, final boolean persist){
		// We assume the given file is an absolute file path so we search for
		// a matching media library to substitute the library path

        MediaItem mediaData;

        final MediaLibrary library = mediaLibraryService.findLibrary(resource);
        if(library != null){

            String hash = null;

            // first we search for the media

            mediaData = repository.queryByPath(resource, library);
            if(mediaData == null)
            {
                hash = retrieveMediaHash(resource);
                if(hash != null)
                    mediaData = repository.findOneByFilehash(hash).orElse(null);
            }

            if(canCreate && mediaData == null){

                // we could not find a matching media so we create a new one

                mediaData = MediaItemFactory.instance().buildMedia(resource, library, hash);
                if(persist && mediaData != null){
                    repository.save(mediaData);
                }
            }
        }else
            throw new NotSupportedException("resource is not part of any media library");

        return mediaData;
	}

	/**
	 * Gets the hash for the given file
	 * @param file
	 * @return
	 */
	private String retrieveMediaHash(ResourceLocation file){
		return mediaHashService.retrieveFileHash(file);
	}


}
