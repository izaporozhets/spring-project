package com.springproject.beautysaloon.service.impl;

import com.springproject.beautysaloon.model.Feedback;
import com.springproject.beautysaloon.repository.FeedbackRepository;
import com.springproject.beautysaloon.service.FeedbackService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public FeedbackServiceImpl(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @Override
    public List<Feedback> findAllByMasterId(Long id) {
        return feedbackRepository.findAllByMasterId(id);
    }
}
