package ua.mykola.jobboard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.mykola.jobboard.exception.ValidationException;
import ua.mykola.jobboard.jobParser.ArbeitNowParser;

@RestController
@RequiredArgsConstructor
@RequestMapping("/parsing")
public class ParsingController {
    private final ArbeitNowParser arbeitNowParser;

    @GetMapping
    public ResponseEntity<String> parse(@RequestParam String site) {
        switch (site) {
            case "arbeitnow" -> arbeitNowParser.parseSomePages();
            default -> new ValidationException("Invalid site: " + site);
        }
        return ResponseEntity.ok("Parsing was successful");
    }

}
