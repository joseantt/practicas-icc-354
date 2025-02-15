package org.example.practica3.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.example.practica3.entities.Header;
import org.example.practica3.entities.Mockup;
import org.example.practica3.services.MockupService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RestController
@RequestMapping("/mockup-server")
public class MockupController {
    private final MockupService mockupService;

    public MockupController(MockupService mockupService) {
        this.mockupService = mockupService;
    }

    @RequestMapping(value = "/**", method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
                                                 RequestMethod.DELETE, RequestMethod.PATCH, RequestMethod.OPTIONS })
    @ResponseBody
    public ResponseEntity<String> handleRequest(HttpServletRequest request) {
        String path = request.getRequestURI().substring("/mockup-server/".length());
        String method = request.getMethod();

        Mockup mockup = mockupService.getMockupByPathAndMethod(path, method);
        if (mockup == null || mockup.getExpirationTime().isBefore(LocalDateTime.now()) ) {
            return new ResponseEntity<>("Mockup not found", HttpStatus.NOT_FOUND);
        }

        if (mockup.getResponseTimeInSecs() > 0) {
            try {
                Thread.sleep(mockup.getResponseTimeInSecs() * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return new ResponseEntity<>("Thread was interrupted", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return ResponseEntity
                .status(mockup.getResponseCode())
                .contentType(MediaType.parseMediaType(mockup.getContentType()))
                .headers(parseHeaders(mockup.getHeaders()))
                .body(mockup.getBody());
    }

    private HttpHeaders parseHeaders(List<Header> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        headers.forEach(header -> httpHeaders.add(header.getKey(), header.getValue()));
        return httpHeaders;
    }
}
