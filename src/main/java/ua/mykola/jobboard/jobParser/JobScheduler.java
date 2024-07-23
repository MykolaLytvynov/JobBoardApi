package ua.mykola.jobboard.jobParser;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JobScheduler {
    private final ArbeitNowParser arbeitNowParser;

    // to run at 8:00 AM and 8:00 PM every day
    @Scheduled(cron = "0 0 8,15 * * ?")
    public void parseArbeitNow() {
        arbeitNowParser.parseSomePages();
    }
}
