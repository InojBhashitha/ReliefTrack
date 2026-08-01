package com.relieftrack.service;

import com.relieftrack.enums.PriorityLevel;
import com.relieftrack.enums.RequestStatus;
import com.relieftrack.model.EmergencyRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    private EmergencyRequest request(int id, PriorityLevel priority, RequestStatus status) {
        EmergencyRequest request = new EmergencyRequest();
        request.setRequestId(id);
        request.setPriority(priority);
        request.setStatus(status);
        return request;
    }
}
