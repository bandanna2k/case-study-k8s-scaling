package dnt.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicLong;

public class Status
{
    private static final AtomicLong counter = new AtomicLong();

    public final long id;
    public final String hostname;

    Status()
    {
        id = counter.incrementAndGet();
        try
        {
            hostname = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e)
        {
            throw new RuntimeException(e);
        }
    }
}
