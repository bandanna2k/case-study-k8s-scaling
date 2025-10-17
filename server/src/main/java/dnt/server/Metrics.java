package dnt.server;


public final class Metrics {

    private long startTime = System.currentTimeMillis();
    private int recentRequests = 0;
    private int requests = 0;
    private double requestsPerSecond = 0;

    public double recalculateRequestsPerSecond() {
        long now = System.currentTimeMillis();
        requestsPerSecond = ((double) recentRequests) / ((now - startTime) / 1000.0);
        recentRequests = 0;
        startTime = now;

        // Scale down
        if(requestsPerSecond < 0.5)
        {
            requestsPerSecond = 0;
        }
        return requestsPerSecond();
    }

    public double requestsPerSecond() {
        return requestsPerSecond;
    }

    public void incrementRequests() {
        recentRequests++;
        requests++;
    }

    public int countOfRequests() {
        return requests;
    }

    @Override
    public String toString() {
        return "Metrics{" +
                "startTime=" + startTime +
                ", recentRequests=" + recentRequests +
                ", requests=" + requests +
                ", requestsPerSecond=" + requestsPerSecond +
                '}';
    }
}
