package ua.mykola.jobboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.mykola.jobboard.entity.Job;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    @Query(nativeQuery = true,
            value = "SELECT * FROM jobs WHERE posted_date = (SELECT MAX(posted_date) FROM jobs)")
    List<Job> findAllJobsWithLatestPostedDate();

    @Query(nativeQuery = true,
            value = "SELECT MAX(posted_date) FROM jobs")
    LocalDateTime findLastTime();

    @Query(nativeQuery = true,
            value = """
                SELECT * FROM jobs
                         WHERE posted_date >= :lastAvailablePostedDate
                         ORDER BY rating_count DESC, posted_date DESC
                         LIMIT 10
                     """)
    List<Job> findTop10JobsByRatingCount(LocalDateTime lastAvailablePostedDate);
}
