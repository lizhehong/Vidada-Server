package com.elderbyte.vidada.media.libraries;


import com.elderbyte.vidada.media.MediaItem;
import com.elderbyte.vidada.media.MediaRepository;
import com.elderbyte.vidada.media.source.MediaSource;
import com.elderbyte.vidada.tags.TagService;
import com.elderbyte.vidada.tags.relations.FileTagRelationSource;
import com.elderbyte.vidada.tags.relations.ITagRelationSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new MediaLibraryService
     * @param repository
     */
    @Autowired
	public MediaLibraryService(
        MediaLibraryRepository repository,
        MediaRepository mediaRepository,
        TagService tagService) {

        this.tagService = tagService;
		this.repository = repository;
        this.mediaRepository = mediaRepository;
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
        save(lib);
        onLibraryAdded(lib);
    }

    @Transactional
	public void removeLibrary(final MediaLibrary library) {
        MediaLibrary libToDelete = repository.findOne(library.getId());

        if(libToDelete != null){
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
        }else{
            log.warn("Could not delete library " + library + " since it was not found in the database!");
        }
	}

    @Transactional
	public void save(final MediaLibrary lib) {
        log.info("Saving media library " + lib);
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
