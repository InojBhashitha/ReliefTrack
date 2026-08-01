package com.relieftrack.datastructure.queue;

import com.relieftrack.enums.PriorityLevel;
import com.relieftrack.model.EmergencyRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PriorityQueueTest {

    @Test
    void dequeuesCriticalRequestsBeforeLowerPriorityRequests() {
        PriorityQueue queue = new PriorityQueue();
        EmergencyRequest medium = request(1, PriorityLevel.MEDIUM);
        EmergencyRequest critical = request(2, PriorityLevel.CRITICAL);
        EmergencyRequest high = request(3, PriorityLevel.HIGH);

        queue.enqueue(medium);
        queue.enqueue(critical);
        queue.enqueue(high);

        assertSame(critical, queue.dequeue());
        assertSame(high, queue.dequeue());
        assertSame(medium, queue.dequeue());
        assertNull(queue.dequeue());
        assertTrue(queue.isEmpty());
    }

    private EmergencyRequest request(int id, PriorityLevel priority) {
        EmergencyRequest request = new EmergencyRequest();
        request.setRequestId(id);
        request.setPriority(priority);
        return request;
    }
}
