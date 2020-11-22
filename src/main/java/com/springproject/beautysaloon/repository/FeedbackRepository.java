package com.springproject.beautysaloon.repository;

import com.springproject.beautysaloon.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    @Query("select feedback from Feedback feedback where feedback.request.procedure.master.id = ?1")
    List<Feedback> findAllByMasterId(Long id);

}
