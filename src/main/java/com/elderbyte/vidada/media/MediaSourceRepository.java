package com.elderbyte.vidada.media;

import com.elderbyte.vidada.media.source.MediaSource;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Spring Data JPA repository for the User entity.
 */
public interface MediaSourceRepository extends JpaRepository<MediaSource, Integer> {



}
