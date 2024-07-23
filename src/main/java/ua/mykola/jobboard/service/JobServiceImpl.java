package ua.mykola.jobboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ua.mykola.jobboard.entity.Job;
import ua.mykola.jobboard.exception.NotFoundException;
import ua.mykola.jobboard.repository.JobRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService{
    private final JobRepository jobRepository;
    private final Object LOCK_TO_INCREASE_RATING = new Object();

    @Override
    public List<Job> saveNewJobs(List<Job> jobs) {
        List<Job> allJobsWithLatestPostedDate = jobRepository.findAllJobsWithLatestPostedDate();

        if (!allJobsWithLatestPostedDate.isEmpty()) {
            LocalDateTime lastTime = allJobsWithLatestPostedDate.get(0).getPostedDate();

            Set<String> existUrls = allJobsWithLatestPostedDate.stream()
                    .map(Job::getUrl)
                    .collect(Collectors.toSet());

            jobs = jobs.stream()
                    .filter(job -> (job.getPostedDate().isAfter(lastTime) || job.getPostedDate().equals(lastTime))
                            && !existUrls.contains(job.getUrl()))
                    .toList();
        }

        return jobRepository.saveAll(jobs);
    }

    @Override
    public Page<Job> findAll(PageRequest pageRequest) {
        return jobRepository.findAll(pageRequest);
    }

    @Override
    public List<Job> findTop10ByRating() {
        LocalDateTime lastAvailablePostedDate = LocalDateTime.now().minusWeeks(2);
        return jobRepository.findTop10JobsByRatingCount(lastAvailablePostedDate);
    }

    @Override
    public Map<String, Long> getLocationStats() {
        List<Job> jobs = jobRepository.findAll();
        return jobs.stream()
                .map(job -> job.getLocation())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    @Override
    public LocalDateTime findLastTime() {
        return jobRepository.findLastTime();
    }

    @Override
    public Job increaseRating(Long id) {
        synchronized (LOCK_TO_INCREASE_RATING) {
            Job job = jobRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Job was not found with id " + id));
            job.setRatingCount(job.getRatingCount() + 1);
            return jobRepository.save(job);
        }
    }
}
