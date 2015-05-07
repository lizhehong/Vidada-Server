package com.elderbyte.vidada.repository;

import com.elderbyte.vidada.domain.media.MediaLibrary;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the MediaLibrary entity.
 */
public interface MediaLibraryRepository extends JpaRepository<MediaLibrary, Integer> {


}
