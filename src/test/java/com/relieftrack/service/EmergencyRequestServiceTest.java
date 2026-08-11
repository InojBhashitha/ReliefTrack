package com.relieftrack.service;

import com.relieftrack.enums.PriorityLevel;
import com.relieftrack.enums.RequestStatus;
import com.relieftrack.model.EmergencyRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmergencyRequestServiceTest {

    @Test
    void prioritizesOnlyPendingRequestsFromCriticalToLow() {
        EmergencyRequestService service = new EmergencyRequestService();

        List<EmergencyRequest> prioritized = service.prioritizePendingRequests(List.of(
                request(1, PriorityLevel.LOW, RequestStatus.PENDING),
                request(2, PriorityLevel.CRITICAL, RequestStatus.PENDING),
                request(3, PriorityLevel.HIGH, RequestStatus.APPROVED)
        ));

        assertEquals(List.of(2, 1), prioritized.stream().map(EmergencyRequest::getRequestId).toList());
    }

    @Test
    void saveNullRequestThrowsException() {
        EmergencyRequestService service = new EmergencyRequestService();
        assertThrows(IllegalArgumentException.class, () -> service.save(null));
    }

    @Test
    void updateNullRequestThrowsException() {
        EmergencyRequestService service = new EmergencyRequestService();
        assertThrows(IllegalArgumentException.class, () -> service.update(null));
    }

    @Test
    void saveRequestWithInvalidFieldsThrowsException() {
        EmergencyRequestService service = new EmergencyRequestService();

        // Null organization
        EmergencyRequest req1 = validRequest();
        req1.setOrganization(null);
        assertThrows(IllegalArgumentException.class, () -> service.save(req1));

        // Empty organization
        EmergencyRequest req2 = validRequest();
        req2.setOrganization("   ");
        assertThrows(IllegalArgumentException.class, () -> service.save(req2));

        // Null disaster type
        EmergencyRequest req3 = validRequest();
        req3.setDisasterType(null);
        assertThrows(IllegalArgumentException.class, () -> service.save(req3));

        // Null relief item
        EmergencyRequest req4 = validRequest();
        req4.setReliefItem(null);
        assertThrows(IllegalArgumentException.class, () -> service.save(req4));

        // Zero or negative quantity
        EmergencyRequest req5 = validRequest();
        req5.setQuantity(0);
        assertThrows(IllegalArgumentException.class, () -> service.save(req5));
        req5.setQuantity(-5);
        assertThrows(IllegalArgumentException.class, () -> service.save(req5));

        // Null priority
        EmergencyRequest req6 = validRequest();
        req6.setPriority(null);
        assertThrows(IllegalArgumentException.class, () -> service.save(req6));

        // Null status
        EmergencyRequest req7 = validRequest();
        req7.setStatus(null);
        assertThrows(IllegalArgumentException.class, () -> service.save(req7));

        // Null request date
        EmergencyRequest req8 = validRequest();
        req8.setRequestDate(null);
        assertThrows(IllegalArgumentException.class, () -> service.save(req8));
    }

    private EmergencyRequest validRequest() {
        EmergencyRequest request = new EmergencyRequest();
        request.setRequestId(1);
        request.setOrganization("Test Org");
        request.setDisasterType(com.relieftrack.enums.DisasterType.FLOOD);
        request.setReliefItem(new com.relieftrack.model.ReliefItem());
        request.setQuantity(100);
        request.setPriority(PriorityLevel.HIGH);
        request.setStatus(RequestStatus.PENDING);
        request.setRequestDate(java.time.LocalDateTime.now());
        return request;
    }

    private EmergencyRequest request(int id, PriorityLevel priority, RequestStatus status) {
        EmergencyRequest request = new EmergencyRequest();
        request.setRequestId(id);
        request.setPriority(priority);
        request.setStatus(status);
        return request;
    }
}
