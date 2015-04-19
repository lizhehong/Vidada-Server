package com.elderbyte.vidada.repository;

import com.elderbyte.vidada.domain.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface TagRepository extends JpaRepository<Tag, String> {



}
