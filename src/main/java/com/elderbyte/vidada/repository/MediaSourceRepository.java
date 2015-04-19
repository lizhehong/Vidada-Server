package com.elderbyte.vidada.repository;

import com.elderbyte.vidada.domain.User;
import com.elderbyte.vidada.domain.media.source.MediaSource;
import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface MediaSourceRepository extends JpaRepository<MediaSource, Long> {



}
