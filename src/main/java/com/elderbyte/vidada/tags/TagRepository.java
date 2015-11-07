package com.elderbyte.vidada.tags;

import com.elderbyte.vidada.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface TagRepository extends JpaRepository<Tag, String> {



}
