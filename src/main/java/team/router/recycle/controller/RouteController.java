package team.router.recycle.controller;

import team.router.recycle.domain.route.RouteRequest.CycleRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/route")
public class RouteController {

    @PostMapping("/cycle")
    public String cycle(@RequestBody CycleRequest cycleRequest) {
        System.out.println("cycleRequest = " + cycleRequest);
        return "cycle";
    }
}
