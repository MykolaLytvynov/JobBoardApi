package ua.mykola.jobboard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.mykola.jobboard.entity.Job;
import ua.mykola.jobboard.exception.ValidationException;
import ua.mykola.jobboard.service.JobService;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    private Set<String> validSortProperties = Set.of("title", "location",
            "companyName", "url", "postedDate");
    private Set<String> validDirections = Set.of("asc", "desc");

    @GetMapping
    public ResponseEntity<Page<Job>> getAllJobs(@RequestParam Integer page,
                                                @RequestParam Integer size,
                                                @RequestParam(required = false, defaultValue = "postedDate") String sortField,
                                                @RequestParam(required = false, defaultValue = "desc") String direction) {
        if (!validSortProperties.contains(sortField)) {
            throw new ValidationException("Invalid sort field: " + sortField);
        }
        if (!validDirections.contains(direction.toLowerCase())) {
            throw new ValidationException("Invalid direction: " + direction);
        }

        Sort sortRequest = Sort.by(Sort.Direction.fromString(direction), sortField);
        PageRequest pageRequest = PageRequest.of(page, size, sortRequest);
        return ResponseEntity.ok(jobService.findAll(pageRequest));
    }

    @GetMapping("/top")
    public ResponseEntity<List<Job>> getTopJobs() {
        return ResponseEntity.ok(jobService.findTop10ByRating());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getLocationStats() {
        return ResponseEntity.ok(jobService.getLocationStats());
    }

    @GetMapping("/rating/{id}")
    public ResponseEntity<Job> increaseRating(@PathVariable("id") Long id) {
        Job updatedJob = jobService.increaseRating(id);
        return ResponseEntity.ok(updatedJob);
    }
}
