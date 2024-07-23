package ua.mykola.jobboard.jobParser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.mykola.jobboard.entity.Job;
import ua.mykola.jobboard.exception.ParsingException;
import ua.mykola.jobboard.service.JobService;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ArbeitNowParser{
    private final JobService jobService;
    private final Integer defaultCountOfPages = 5;

    public void parseSomePages() {
        parseSomePages(defaultCountOfPages);
    }

    public void parseSomePages(int countOfPages) {
        List<Job> jobs = new ArrayList<>();

        int page = 1;
        while(page <= countOfPages) {
            jobs.addAll(parse(page));
            page++;
        }

        jobService.saveNewJobs(jobs);
    }

    public List<Job> parse(int page) {
        List<Job> jobs = new ArrayList<>();
        try {
            URL url = new URL("https://www.arbeitnow.com/api/job-board-api?page=" + page);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == 200) {
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

                if (jsonObject.has("data")) {
                    JsonElement dataElement = jsonObject.get("data");
                    LocalDateTime lastTime = jobService.findLastTime();
                    for (JsonElement jsonElement : dataElement.getAsJsonArray()) {
                        Job newJob = convertJsonElementToJob(jsonElement);
                        if (lastTime != null && newJob.getPostedDate().isBefore(lastTime)) return jobs;
                        jobs.add(newJob);
                    }
                }
            } else {
                throw new ParsingException("Connection failed");
            }
        } catch (IOException e) {
            throw new ParsingException("A problem while performing Input/Output (I/O) operations");
        }
        return jobs;
    }

    private Job convertJsonElementToJob(JsonElement jsonElement) {
        return Job.builder()
                .title(jsonElement.getAsJsonObject().get("title").getAsString())
                .location(jsonElement.getAsJsonObject().get("location").getAsString())
                .url(jsonElement.getAsJsonObject().get("url").getAsString())
                .postedDate(getLocalDateTime(jsonElement.getAsJsonObject().get("created_at").getAsLong()))
                .companyName(jsonElement.getAsJsonObject().get("company_name").getAsString())
                .build();
    }

    private LocalDateTime getLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.of("UTC"));
    }
}
