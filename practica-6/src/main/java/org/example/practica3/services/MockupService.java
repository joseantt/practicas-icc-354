package org.example.practica3.services;

import org.example.practica3.entities.Mockup;
import org.example.practica3.repositories.MockupRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MockupService {

    private final MockupRepository mockupRepository;
    private final JwtService jwtService;

    public MockupService(MockupRepository mockupRepository, JwtService jwtService) {
        this.mockupRepository = mockupRepository;
        this.jwtService = jwtService;
    }

    public Mockup save(Mockup mockup) {
        if (mockup.isRequiresJwt()) {
            mockup.setJwtToken(jwtService.generateToken(mockup));
        } else {
            mockup.setJwtToken(null);
        }
        return mockupRepository.save(mockup);
    }

    public List<Mockup> getAllMockups() {
        return mockupRepository.findAll();
    }

    public Optional<Mockup> getMockupById(Long id) { return mockupRepository.findByIdWithHeaders(id); }

    public Mockup getMockupByPathAndMethod(String path, String method) {
        return mockupRepository.findByPathAndAccessMethod(path, method);
    }

    public Mockup updateMockup(Long id, Mockup mockupDetails) {
        return mockupRepository.findById(id).map(mockup -> {
            mockup.setName(mockupDetails.getName());
            mockup.setDescription(mockupDetails.getDescription());
            mockup.setPath(mockupDetails.getPath());
            mockup.setAccessMethod(mockupDetails.getAccessMethod());
            mockup.setResponseCode(mockupDetails.getResponseCode());
            mockup.setContentType(mockupDetails.getContentType());
            mockup.setBody(mockupDetails.getBody());
            mockup.setHeaders(mockupDetails.getHeaders());
            mockup.setResponseTimeInSecs(mockupDetails.getResponseTimeInSecs());
            mockup.setExpirationTime(mockupDetails.getExpirationTime());

            mockup.setRequiresJwt(mockupDetails.isRequiresJwt());
            if (mockup.isRequiresJwt()) {
                mockup.setJwtToken(jwtService.generateToken(mockup));
            } else {
                mockup.setJwtToken(null);
            }

            return mockupRepository.save(mockup);
        }).orElseThrow(() -> new RuntimeException("Mockup not found with id " + id));
    }

    public Optional<Mockup> findByPath(String path) {
        return mockupRepository.findByPath(path);
    }
    public void deleteMockup(Long id) {
        mockupRepository.deleteById(id);
    }
}
