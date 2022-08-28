package org.selenium.trailers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
public class TrailerController {

    private final TrailerService trailerService;

    public TrailerController(TrailerService trailerService) {
        this.trailerService = trailerService;
    }

    @GetMapping("/trailer")
    public ResponseEntity<Trailer> getTrailer(@RequestParam String title) {
        return ResponseEntity.ok(new Trailer(trailerService.getTrailerByTitle(title)));
    }

}
