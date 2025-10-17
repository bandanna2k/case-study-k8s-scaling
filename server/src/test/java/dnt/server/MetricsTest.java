package dnt.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MetricsTest {
    @Test
    void testMetrics() {
        Metrics metrics = new Metrics();
        metrics.incrementRequests();


    }
}