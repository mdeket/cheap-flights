package com.cheapflights.tickets.repository;

import com.cheapflights.tickets.domain.model.Comment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {
}
