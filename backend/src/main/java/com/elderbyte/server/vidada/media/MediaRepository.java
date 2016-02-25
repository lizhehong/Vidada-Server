package com.elderbyte.server.vidada.media;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the User entity.
 */
@Repository
public interface MediaRepository extends JpaRepository<MediaItem, String>, MediaRepositoryCustom {


    /**
     * Finds the media with the given file-hash
     * @param hash
     * @return
     */
    Optional<MediaItem> findOneByFilehash(String hash);


}
