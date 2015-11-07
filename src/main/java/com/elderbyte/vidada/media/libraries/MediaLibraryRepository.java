package com.elderbyte.vidada.media.libraries;

import com.elderbyte.vidada.media.libraries.MediaLibrary;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the MediaLibrary entity.
 */
public interface MediaLibraryRepository extends JpaRepository<MediaLibrary, Integer> {


}
