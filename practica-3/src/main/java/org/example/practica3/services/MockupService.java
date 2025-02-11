package org.example.practica3.services;

import org.example.practica3.entities.Mockup;
import org.example.practica3.repositories.MockupRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MockupService {

    private final MockupRepository mockupRepository;

    public MockupService(MockupRepository mockupRepository) {
        this.mockupRepository = mockupRepository;
    }

    public Mockup save(Mockup mockup) {
        return mockupRepository.save(mockup);
    }

    public List<Mockup> getAllMockups() {
        return mockupRepository.findAll();
    }

    public Optional<Mockup> getMockupById(Long id) {
        return mockupRepository.findById(id);
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
            mockup.setExpirationTimeInHours(mockupDetails.getExpirationTimeInHours());
            return mockupRepository.save(mockup);
        }).orElseThrow(() -> new RuntimeException("Mockup not found with id " + id));
    }

    public void deleteMockup(Long id) {
        mockupRepository.deleteById(id);
    }
}
