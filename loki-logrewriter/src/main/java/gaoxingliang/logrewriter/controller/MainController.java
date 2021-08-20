package gaoxingliang.logrewriter.controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class MainController {

    @PostMapping("/loki/api/v1/push")
    public void post(@RequestBody String body) {
        JsonSender.log(body);
    }
}
