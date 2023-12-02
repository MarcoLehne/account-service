package account.controller;

import account.DTO.SecurityEventResponse;
import account.entity.SecurityEvent;
import account.repository.SecurityRepository;
import account.route.v1.SecurityEventsRoute;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GetSecurityEventsController {

    private final SecurityRepository securityRepository;

    public GetSecurityEventsController(SecurityRepository securityRepository) {
        this.securityRepository = securityRepository;
    }

    @GetMapping(path = SecurityEventsRoute.PATH)
    public ResponseEntity<List<SecurityEventResponse>> getSecurityEvents() {
        Iterable<SecurityEvent> securityEvents = securityRepository.findAll();
        List<SecurityEventResponse> responses = new ArrayList<>();
        securityEvents.forEach(event -> responses.add(mapToResponse(event)));
        return ResponseEntity.ok(responses);
    }

    private SecurityEventResponse mapToResponse(SecurityEvent event) {
        return new SecurityEventResponse(
                event.getId(),
                event.getDate().toString(),
                event.getAction(),
                event.getSubject() == null ? "Anonymous" : event.getSubject(),
                event.getObject(),
                event.getPath()
        );
    }
}
