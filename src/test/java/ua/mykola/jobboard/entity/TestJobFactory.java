package ua.mykola.jobboard.entity;

import java.time.LocalDateTime;
import java.util.List;

public class TestJobFactory {
    public static Job createJob1() {
        return Job.builder()
                .title("Title 1")
                .url("mykola.ua/job1")
                .postedDate(LocalDateTime.now().minusDays(1))
                .companyName("Company 1")
                .location("Location 1")
                .build();
    }

    public static Job createJob2() {
        return Job.builder()
                .title("Title 2")
                .url("mykola.ua/job2")
                .postedDate(LocalDateTime.now().minusDays(1))
                .companyName("Company 2")
                .location("Location 2")
                .build();
    }

    public static Job createPersistedJob1() {
        Job job = createJob1();
        job.setId(1l);
        return job;
    }

    public static Job createPersistedJob2() {
        Job job = createJob2();
        job.setId(2l);
        return job;
    }

    public static Job createPersistedJob3() {
        return Job.builder()
                .title("Title 3")
                .url("mykola.ua/job3")
                .postedDate(LocalDateTime.now().minusDays(1))
                .companyName("Company 3")
                // Same location as the second job
                .location("Location 2")
                .build();
    }

    public static List<Job> createNewJobs() {
        return List.of(createJob1(),
                createJob2());
    }

    public static List<Job> createPersistedJobs() {
        return List.of(createPersistedJob1(),
                createPersistedJob2());
    }

    public static Job createTodayPersistedJob() {
        Job job = createJob1();
        job.setId(1l);
        job.setPostedDate(LocalDateTime.now());
        return job;
    }

}
