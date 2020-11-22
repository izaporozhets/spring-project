package com.springproject.beautysaloon.service;

import com.springproject.beautysaloon.model.Feedback;
import java.util.List;

public interface FeedbackService {
    List<Feedback> findAllByMasterId(Long id);
}
