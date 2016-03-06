package com.elderbyte.server.vidada.tags;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the User entity.
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, String> {



}
