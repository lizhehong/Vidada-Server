package com.elderbyte.server.vidada.tags;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the User entity.
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, String> {



    ///@Query(nativeQuery = true)
    //public List<TagInfo> findAvgRatingByVideoId();

}
