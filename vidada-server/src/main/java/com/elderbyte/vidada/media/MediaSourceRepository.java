package com.elderbyte.vidada.media;

import com.elderbyte.vidada.media.source.MediaSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data JPA repository for the User entity.
 */
@Repository
public interface MediaSourceRepository extends JpaRepository<MediaSource, Integer> {



}
