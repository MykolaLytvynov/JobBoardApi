package ua.mykola.jobboard.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ua.mykola.jobboard.entity.Job;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface JobService {

    List<Job> saveNewJobs(List<Job> jobs);

    Page<Job> findAll(PageRequest pageRequest);

    List<Job> findTop10ByRating();

    Map<String, Long>getLocationStats();

    LocalDateTime findLastTime();

    Job increaseRating(Long id);
}
