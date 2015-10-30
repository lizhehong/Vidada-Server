package com.elderbyte.vidada.repository;

import com.elderbyte.vidada.domain.media.source.MediaSource;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Spring Data JPA repository for the User entity.
 */
public interface MediaSourceRepository extends JpaRepository<MediaSource, Integer> {



}
