package ua.mykola.jobboard.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ua.mykola.jobboard.entity.Job;
import ua.mykola.jobboard.entity.TestJobFactory;
import ua.mykola.jobboard.exception.NotFoundException;
import ua.mykola.jobboard.repository.JobRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class JobServiceImplTest {

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobServiceImpl jobService;

    @Test
    @DisplayName("Saving jobs")
    public void givenJobsToSave_whenSaveJobs_thenRepositoryIsCalled() {
        //given
        List<Job> newJobs = TestJobFactory.createNewJobs();
        List<Job> persistedJobs = TestJobFactory.createPersistedJobs();
        given(jobRepository.saveAll(Mockito.any())).willReturn(persistedJobs);
        given(jobRepository.findAllJobsWithLatestPostedDate()).willReturn(List.of());

        //when
        List<Job> obtainedJobs = jobService.saveNewJobs(newJobs);

        //then
        assertNotNull(obtainedJobs);
        assertEquals(persistedJobs.size(), obtainedJobs.size());
        assertEquals(persistedJobs.get(0).getId(), obtainedJobs.get(0).getId());
        verify(jobRepository, times(1)).saveAll(Mockito.any());
        verify(jobRepository, times(1)).findAllJobsWithLatestPostedDate();
    }

    @Test
    @DisplayName("Saving jobs with existed ones")
    public void givenJobsWithExistedOneToSave_whenSaveJobs_thenRepositoryIsCalled() {
        //given
        List<Job> newJobs = TestJobFactory.createNewJobs();
        List<Job> persistedJobs = List.of(
                TestJobFactory.createPersistedJob1(),
                TestJobFactory.createPersistedJob2());
        given(jobRepository.findAllJobsWithLatestPostedDate()).willReturn(persistedJobs);

        //when
        List<Job> obtainedJobs = jobService.saveNewJobs(newJobs);

        //then
        assertNotNull(obtainedJobs);
        assertEquals(0, obtainedJobs.size());
        verify(jobRepository, times(1)).saveAll(Mockito.any());
        verify(jobRepository, times(1)).findAllJobsWithLatestPostedDate();
    }

    @Test
    @DisplayName("Saving jobs with posted dates before the available date")
    public void givenJobsWithEarlyPostedDatesToSave_whenSaveJobs_thenRepositoryIsCalled() {
        //given
        List<Job> oldJobs = TestJobFactory.createNewJobs();
        List<Job> todayJobs = List.of(TestJobFactory.createTodayPersistedJob());
        given(jobRepository.findAllJobsWithLatestPostedDate()).willReturn(todayJobs);

        //when
        List<Job> obtainedJobs = jobService.saveNewJobs(oldJobs);

        //then
        assertNotNull(obtainedJobs);
        assertEquals(0, obtainedJobs.size());
        verify(jobRepository, times(1)).saveAll(Mockito.any());
        verify(jobRepository, times(1)).findAllJobsWithLatestPostedDate();
    }

    @Test
    @DisplayName("Getting all jobs")
    public void givenTwoJobs_whenGetAll_thenJobsAreReturned() {
        //given
        List<Job> jobs = TestJobFactory.createPersistedJobs();
        Page<Job> pageJobs = new PageImpl<>(jobs);
        given(jobRepository.findAll(Mockito.any(PageRequest.class))).willReturn(pageJobs);

        //when
        Page<Job> obtainedJobs = jobService.findAll(PageRequest.of(0, 10));

        //then
        assertNotNull(obtainedJobs);
        assertEquals(jobs.size(), obtainedJobs.getTotalElements());
        assertEquals(jobs.get(0).getId(), obtainedJobs.getContent().get(0).getId());
        verify(jobRepository, times(1)).findAll(Mockito.any(PageRequest.class));
    }

    @Test
    @DisplayName("Getting top 10 jobs")
    public void givenFourJobs_whenFindTop10ByRating_thenTopJobsAreReturned() {
        //given
        List<Job> jobs = TestJobFactory.createPersistedJobs();
        given(jobRepository.findTop10JobsByRatingCount(Mockito.any())).willReturn(jobs);

        //when
        List<Job> obtainedJobs = jobService.findTop10ByRating();

        //then
        assertNotNull(obtainedJobs);
        assertEquals(jobs.size(), obtainedJobs.size());
        assertEquals(jobs.get(0).getId(), obtainedJobs.get(0).getId());
        verify(jobRepository, times(1)).findTop10JobsByRatingCount(Mockito.any());
    }

    @Test
    @DisplayName("Increasing rating")
    public void givenId_whenIncreaseRating_thenRepositoryIsCalled() {
        //given
        Long id = 1l;
        Job job = TestJobFactory.createPersistedJob1();
        Integer currentRating = job.getRatingCount();
        given(jobRepository.findById(Mockito.any())).willReturn(Optional.of(job));
        given(jobRepository.save(Mockito.any())).willReturn(job);

        //when
        Job updatedJob = jobService.increaseRating(id);

        //then
        assertEquals(currentRating + 1, updatedJob.getRatingCount());
        verify(jobRepository, times(1)).findById(anyLong());
        verify(jobRepository, times(1)).save(Mockito.any());
    }

    @Test
    @DisplayName("Increasing rating of non-existent job")
    public void givenNonExistentId_whenIncreaseRating_thenExceptionIsThrown() {
        //given
        Long id = 404l;
        given(jobRepository.findById(Mockito.any())).willReturn(Optional.empty());
        String notFoundMessage = "Job was not found with id 404";

        //when
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> jobService.increaseRating(id));

        //then
        assertEquals(notFoundMessage, ex.getMessage());
        verify(jobRepository, times(1)).findById(anyLong());
        verify(jobRepository, never()).save(any());
    }

    @Test
    @DisplayName("Getting location statistics")
    public void givenThreeJobs_whenGetLocationStats_thenStatsAreReturned() {
        //given
        List<Job> jobs = List.of(TestJobFactory.createPersistedJob1(),
                                    TestJobFactory.createPersistedJob2(),
                                    TestJobFactory.createPersistedJob3());
        Map<String, Long> expectedLocationStats = Map.of("Location 1", 1L,
                                                         "Location 2", 2L);
        given(jobRepository.findAll()).willReturn(jobs);

        //when
        Map<String, Long> obtainedStats = jobService.getLocationStats();

        //then
        assertEquals(expectedLocationStats, obtainedStats);
    }
}
