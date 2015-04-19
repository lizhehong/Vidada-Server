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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
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
     *
     * @param repository
     */
    @Inject
	public MediaLibraryService(MediaLibraryRepository repository, MediaRepository mediaRepository, MediaSourceRepository mediaSourceRepository) {
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
	public List<MediaLibrary> getAllLibraries(){
		return repository.findAll();
	}


    @Transactional
	public void addLibrary(final MediaLibrary lib){
        repository.save(lib);
        libraryAddedEvent.fireEvent(this, EventArgsG.build(lib));
	}

    @Transactional
	public void removeLibrary(final MediaLibrary library) {
        MediaLibrary libToDelete = repository.findOne((long) library.getId());


        // TODO Remove all Sources which used this library

        // Find all medias which have this library
        List<MediaSource> sourcesToDelete = new ArrayList<>();
        List<MediaItem> mediaItems = mediaRepository.queryByLibrary(library);

        for (MediaItem mediaItem : mediaItems) {
            Set<MediaSource> sources = mediaItem.getSources();
            for (MediaSource source : sources) {
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

        // Now delete all media sources...
        mediaSourceRepository.deleteInBatch(sourcesToDelete);

        repository.delete(libToDelete);
        libraryRemovedEvent.fireEvent(this, EventArgsG.build(library));
	}

    @Transactional
	public void update(final MediaLibrary lib) {
        repository.save(lib);
	}

    @Transactional
	public MediaLibrary findLibrary(final ResourceLocation file) {

        throw new NotImplementedException();
        //return repository.queryByLocation(file); // TODO implement this in repository...
	}

    @Transactional
	public MediaLibrary getById(final long id) {
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
}
