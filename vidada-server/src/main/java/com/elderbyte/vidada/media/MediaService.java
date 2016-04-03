package com.elderbyte.vidada.media;


import com.elderbyte.common.locations.ResourceLocation;
import com.elderbyte.code.CodeDomException;
import com.elderbyte.code.dom.expressions.ExpressionNode;
import com.elderbyte.common.ListPage;
import com.elderbyte.common.NotSupportedException;
import com.elderbyte.vidada.media.libraries.MediaLibraryService;
import com.elderbyte.vidada.media.source.MediaSource;
import com.elderbyte.vidada.queries.TagExpressionBuilder;
import com.elderbyte.vidada.tags.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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

    /**
     * Persists the given media
     * @param media
     */
    @Transactional
	public void save(final MediaItem media) {
        repository.save(media);
	}

    /**
     * Persists the given medias
     * @param medias
     */
    @Transactional
	public void save(final Collection<MediaItem> medias) {
        repository.save(medias);
	}

    /**
     * Performs a media query and returns the matching medias.
     * The result-set is paged.
     * @param qry The media query which filters the medias
     * @param pageIndex The page index to return
     * @param maxPageSize The number of medias per page
     * @return
     */
    @Transactional
	public ListPage<MediaItem> query(final MediaQuery qry, final int pageIndex, final int maxPageSize) {

        ExpressionNode tagExpression = null;

        if(qry.getTagExpression() != null && !qry.getTagExpression().isEmpty()){

            try {
                tagExpression = TagExpressionBuilder.create()
                    .enableExpressionRepair()
                    .expandTags(tagService::getAllRelatedTags)
                        // Add additional related tags to the users request -
                        // This is what makes Vidada 'intelligent'
                    .build(qry.getTagExpression());
            }catch (CodeDomException e){
                logger.debug("Could not create Tag-Expression query!" + e.getMessage());
                logger.trace("Could not create Tag-Expression query!", e);

                return ListPage.empty(); // No results since wrong expression query
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

    /**
     * Returns all persisted medias
     * @return
     */
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


    /**
     * Deletes the given media from the persisted items.
     *
     * @param media
     */
    @Transactional
	public void delete(final MediaItem media) {
        repository.delete(media);
	}

    /**
     * Deletes all given medias from the persisted items.
     * @param media
     */
    @Transactional
    public void delete(final Collection<MediaItem> media) {
        repository.delete(media);
    }

    /**
     * Deletes the given medias local file permanently.
     * Afterwards, the media is removed from the persisted items.
     *
     * @param mediaItem
     * @throws
     */
    @Transactional
    public void deleteLocalFile(MediaItem mediaItem) {

        for (MediaSource source : mediaItem.getSources()) {
            ResourceLocation resource = source.getResourceLocation();

            try {
                resource.delete();
            } catch (IOException e) {
                throw new NotSupportedException("Failed to delete media resource " + resource, e);
            }
        }

        // Delete it from the database
        delete(mediaItem);
    }

    /**
     * Returns the number of medias currently persisted
     * @return
     */
    @Transactional
    public int count() {
        return (int)repository.count();
    }


    /**
     * Updates the last-accessed and opended-counter of the given media
     * @param mediaItem
     */
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
