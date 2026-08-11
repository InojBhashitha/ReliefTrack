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

    @Test
    void enqueueNullRequestThrowsIllegalArgumentException() {
        PriorityQueue queue = new PriorityQueue();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> queue.enqueue(null));

        assertTrue(exception.getMessage().toLowerCase().contains("null"));
        assertTrue(queue.isEmpty(), "Queue must remain empty after rejected null enqueue.");
        assertEquals(0, queue.size());
    }

    @Test
    void enqueueRequestWithNullPriorityIsTreatedAsLowest() {
        PriorityQueue queue = new PriorityQueue();
        EmergencyRequest nullPriority = request(1, null);
        EmergencyRequest low = request(2, PriorityLevel.LOW);
        EmergencyRequest high = request(3, PriorityLevel.HIGH);

        queue.enqueue(nullPriority);
        queue.enqueue(low);
        queue.enqueue(high);

        assertEquals(3, queue.size());
        assertSame(high, queue.dequeue(), "HIGH should come first.");
        assertSame(low, queue.dequeue(), "LOW should come before null priority.");
        assertSame(nullPriority, queue.dequeue(), "Null priority should come last.");
        assertTrue(queue.isEmpty());
    }

    @Test
    void enqueueMultipleNullPriorityRequestsPreservesInsertionOrder() {
        PriorityQueue queue = new PriorityQueue();
        EmergencyRequest first = request(1, null);
        EmergencyRequest second = request(2, null);
        EmergencyRequest medium = request(3, PriorityLevel.MEDIUM);

        queue.enqueue(first);
        queue.enqueue(second);
        queue.enqueue(medium);

        assertSame(medium, queue.dequeue(), "MEDIUM should come first.");
        assertSame(first, queue.dequeue(), "First null-priority request should come next.");
        assertSame(second, queue.dequeue(), "Second null-priority request should come last.");
    }

    private EmergencyRequest request(int id, PriorityLevel priority) {
        EmergencyRequest request = new EmergencyRequest();
        request.setRequestId(id);
        request.setPriority(priority);
        return request;
    }
}

