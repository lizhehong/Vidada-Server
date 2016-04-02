package com.elderbyte.vidada.vidada.media.libraries;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MediaLibrary entity.
 */
@Repository
public interface MediaLibraryRepository extends JpaRepository<MediaLibrary, Integer> {


}
