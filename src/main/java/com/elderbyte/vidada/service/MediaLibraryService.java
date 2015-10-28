package com.elderbyte.vidada.service;

import archimedes.core.events.EventArgsG;
import archimedes.core.events.EventHandlerEx;
import archimedes.core.events.IEvent;
import archimedes.core.exceptions.NotImplementedException;
import archimedes.core.io.locations.ResourceLocation;
import com.elderbyte.vidada.domain.media.MediaItem;
import com.elderbyte.vidada.domain.media.MediaLibrary;
import com.elderbyte.vidada.domain.media.source.MediaSource;
import com.elderbyte.vidada.repository.MediaLibraryRepository;
import com.elderbyte.vidada.repository.MediaRepository;
import com.elderbyte.vidada.repository.MediaSourceRepository;
import com.elderbyte.vidada.service.tags.FileTagRelationSource;
import com.elderbyte.vidada.service.tags.ITagRelationSource;
import com.elderbyte.vidada.service.tags.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Manages all MediaLibraries
 * @author IsNull
 *
 */
@Service
public class MediaLibraryService  {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger log = LoggerFactory.getLogger(MediaLibraryService.class);


    private final TagService tagService;
	private final MediaLibraryRepository repository;
    private final MediaRepository mediaRepository;
    private final MediaSourceRepository mediaSourceRepository;

	private final EventHandlerEx<EventArgsG<MediaLibrary>> libraryAddedEvent = new EventHandlerEx<>();
    private final EventHandlerEx<EventArgsG<MediaLibrary>> libraryRemovedEvent = new EventHandlerEx<>();


    /***************************************************************************
     *                                                                         *
     * Events                                                                  *
     *                                                                         *
     **************************************************************************/

	public IEvent<EventArgsG<MediaLibrary>> getLibraryAddedEvent() {return libraryAddedEvent; }

	public IEvent<EventArgsG<MediaLibrary>> getLibraryRemovedEvent() {return libraryRemovedEvent; }

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new MediaLibraryService
     * @param repository
     */
    @Inject
	public MediaLibraryService(
        MediaLibraryRepository repository,
        MediaRepository mediaRepository,
        MediaSourceRepository mediaSourceRepository,
        TagService tagService) {

        this.tagService = tagService;
		this.repository = repository;
        this.mediaRepository = mediaRepository;
        this.mediaSourceRepository = mediaSourceRepository;
	}

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    @Transactional
    @PostConstruct
    public void init(){
        List<MediaLibrary> libraries = getAllLibraries();

        log.info(String.format("Initializing MediaLibraryService, found %s libraries.", libraries.size()));

        for(MediaLibrary lib : libraries){
            onLibraryAdded(lib);
        }
    }


    @Transactional
	public List<MediaLibrary> getAllLibraries(){
		return repository.findAll();
	}


    @Transactional
	public void addLibrary(final MediaLibrary lib){
        repository.save(lib);
        onLibraryAdded(lib);
        libraryAddedEvent.fireEvent(this, EventArgsG.build(lib));
	}

    @Transactional
	public void removeLibrary(final MediaLibrary library) {
        MediaLibrary libToDelete = repository.findOne(library.getId());


        // Find all medias which have this library
        List<MediaItem> mediaItems = mediaRepository.queryByLibrary(library);

        for (MediaItem mediaItem : mediaItems) {
            for (MediaSource source : new HashSet<>(mediaItem.getSources())) {
                if(source.getParentLibrary().equals(library)) {
                    mediaItem.getSources().remove(source);
                }
            }

            // Check if the media has any sources left
            if(mediaItem.getSources().isEmpty()){
                // The media has no sources left
                // TODO We can delete it now, but then we loose all general info - is this intended?
                mediaRepository.delete(mediaItem);
            }
        }

        repository.delete(libToDelete);
        libraryRemovedEvent.fireEvent(this, EventArgsG.build(library));
	}

    @Transactional
	public void save(final MediaLibrary lib) {
        repository.save(lib);
	}

    @Transactional
	public MediaLibrary findById(final int id) {
		return repository.findOne(id);
	}

	public List<MediaLibrary> getAvailableLibraries() {
		List<MediaLibrary> available = new ArrayList<>();
		for (MediaLibrary library : getAllLibraries()) {
			if(library.isAvailable()){
				available.add(library);
			}
		}
		return available;
	}

    private void onLibraryAdded(MediaLibrary library){
        ITagRelationSource source = new FileTagRelationSource(library.getUserTagRelationDef());
        tagService.registerTagRelationSource(source);
    }
}
