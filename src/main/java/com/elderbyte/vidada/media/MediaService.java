package com.elderbyte.vidada.media;

import archimedes.core.data.pagination.ListPage;
import archimedes.core.events.EventArgs;
import archimedes.core.events.EventHandlerEx;
import archimedes.core.events.IEvent;
import archimedes.core.io.locations.ResourceLocation;
import com.elderbyte.code.CodeDomException;
import com.elderbyte.code.dom.expressions.ExpressionNode;
import com.elderbyte.vidada.queries.TagExpressionBuilder;
import com.elderbyte.vidada.media.libraries.MediaLibraryService;
import com.elderbyte.vidada.tags.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
    @Autowired
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
	public void save(final MediaItem media) {
        repository.save(media);
        fireMediasChanged();
	}

    @Transactional
	public void save(final Collection<MediaItem> medias) {
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
                logger.debug("Could not create Tag-Expression query!", e.getMessage());
                logger.trace("Could not create Tag-Expression query!", e);

                return new ListPage<>(null, 0, maxPageSize, 0); // No results since wrong expression query
            }
        }

        MediaExpressionQuery exprQuery = new MediaExpressionQuery(
                tagExpression,
                qry.getMediaType(),
                qry.getKeywords(),
                qry.getOrder(),
                qry.isReverseOrder());

        if(qry.isOnlyAvailable()){
            exprQuery.getAllowedLibraries().addAll(mediaLibraryService.getAvailableLibraries());
        }

        logger.info("Searching all medias which match query: " + exprQuery);

        return repository.query(exprQuery, pageIndex, maxPageSize);
    }


    @Transactional
	public List<MediaItem> findAllMedias(){
		return repository.findAll();
	}

    /**
     * Returns the media with the given id / hash
     * @param hash
     * @return
     */
    @Transactional
    public Optional<MediaItem> findById(final String hash) {
        return repository.findOneByFilehash(hash);
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


    @Transactional
    public int count() {
        return (int)repository.count();
    }

    @Transactional
    public void mediaAccessed(MediaItem mediaItem) {
       findById(mediaItem.getFilehash()).ifPresent(m -> {
           m.setOpened(m.getOpened()+1);
           m.setLastAccessed(ZonedDateTime.now());
           save(m);
       });
    }


    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/


	/**
	 * Gets the hash for the given file
	 * @param file
	 * @return
	 */
	private String retrieveMediaHash(ResourceLocation file){
		return mediaHashService.retrieveFileHash(file);
	}



}
