package ua.mykola.jobboard.controller;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.mykola.jobboard.entity.Job;
import ua.mykola.jobboard.entity.TestJobFactory;
import ua.mykola.jobboard.service.JobService;

import java.util.List;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@WebMvcTest
class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobService jobService;

    @Test
    @DisplayName("Getting jobs")
    void givenTwoJobs_whenGetAllJobs_thenSuccessResponse() throws Exception {
        //given
        List<Job> jobs = TestJobFactory.createPersistedJobs();
        Page<Job> pageJobs = new PageImpl<>(jobs);
        given(jobService.findAll(Mockito.any(PageRequest.class))).willReturn(pageJobs);

        //when
        ResultActions result = mockMvc.perform(get("/jobs")
                .param("page", "0")
                .param("size", "10"));

        //then
        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements", CoreMatchers.is(jobs.size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].title",
                        CoreMatchers.is(jobs.get(0).getTitle())));
    }

    @Test
    @DisplayName("Getting jobs with invalid sort field")
    void givenInvalidSortField_whenGetAllJobs_thenErrorResponse() throws Exception {
        //given
        String invalidSortField = "invalidSortField";
        String errorMessage = "Invalid sort field: " + invalidSortField;

        //when
        ResultActions result = mockMvc.perform(get("/jobs")
                .param("page", "0")
                .param("size", "10")
                .param("sortField", invalidSortField));

        //then
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(errorMessage));
    }

    @Test
    @DisplayName("Getting jobs with invalid direction")
    void givenInvalidDirection_whenGetAllJobs_thenErrorResponse() throws Exception {
        //given
        String invalidDirection = "invalid desc-asc";
        String errorMessage = "Invalid direction: " + invalidDirection;

        //when
        ResultActions result = mockMvc.perform(get("/jobs")
                .param("page", "0")
                .param("size", "10")
                .param("direction", invalidDirection));

        //then
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(errorMessage));
    }

    @Test
    @DisplayName("Getting top jobs")
    void givenTwoJobs_whenGetTopJobs_thenSuccessResponse() throws Exception {
        //given
        List<Job> jobs = TestJobFactory.createPersistedJobs();
        given(jobService.findTop10ByRating()).willReturn(jobs);

        //when
        ResultActions result = mockMvc.perform(get("/jobs/top"));

        //then
        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].title",
                        CoreMatchers.is(jobs.get(1).getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title",
                        CoreMatchers.is(jobs.get(0).getTitle())));
    }

    @Test
    @DisplayName("Getting location statistic")
    void givenStatistic_whenGetLocationStats_thenSuccessResponse() throws Exception {
        //given
        Map<String, Long> stats = Map.of("Location 1", 1L);
        String jsonResponse = """
                {"Location 1":1}""";
        given(jobService.getLocationStats()).willReturn(stats);

        //when
        ResultActions result = mockMvc.perform(get("/jobs/stats"));

        //then
        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(jsonResponse));
    }

    @Test
    @DisplayName("Increasing rating")
    void givenJob_whenIncreaseRating_thenSuccessResponse() throws Exception {
        //given
        Job job = TestJobFactory.createPersistedJob1();
        given(jobService.increaseRating(Mockito.anyLong())).willReturn(job);

        //when
        ResultActions result = mockMvc.perform(get("/jobs/rating/" + job.getId()));

        //then
        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", CoreMatchers.is(job.getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ratingCount", CoreMatchers.is(job.getRatingCount())));
    }
}
