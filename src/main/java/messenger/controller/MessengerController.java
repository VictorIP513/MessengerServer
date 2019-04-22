package messenger.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class MessengerController {

    @PostMapping("/api/registration")
    private ResponseEntity<String> registration(@RequestBody String string) {
        System.out.println(string);
        return ResponseEntity.ok("");
    }
}
