package com.elderbyte.vidada.repository;

import com.elderbyte.vidada.domain.media.MediaItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface MediaRepository extends JpaRepository<MediaItem, Long>, ICustomMediaRepository {


    /**
     * Finds the media with the given file-hash
     * @param hash
     * @return
     */
    Optional<MediaItem> findOneByFilehash(String hash);


}
