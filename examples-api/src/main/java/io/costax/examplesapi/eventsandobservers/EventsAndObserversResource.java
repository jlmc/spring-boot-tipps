package io.costax.examplesapi.eventsandobservers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/events-and-observers-pattern")
public class EventsAndObserversResource {

    @Autowired
    GameService gameService;

    @GetMapping
    //@ResponseBody
    public ResponseEntity<?> hello(@RequestParam(name = "payer-name",
            required = false,
            defaultValue = "2.22. Publishing and consuming custom events") String playerName) {

        gameService.scoreGoal(playerName);

        return ResponseEntity.ok(playerName + " score Goal " + System.currentTimeMillis());
    }
}
