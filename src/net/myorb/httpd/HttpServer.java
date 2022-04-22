
package net.myorb.httpd;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerTransportProcessor;

import org.simpleframework.transport.Socket;
import org.simpleframework.transport.SocketProcessor;
import org.simpleframework.transport.TransportProcessor;
import org.simpleframework.transport.TransportSocketProcessor;

import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import org.simpleframework.common.buffer.FileAllocator;
import org.simpleframework.common.buffer.Allocator;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import java.io.IOException;

/**
 * server layer using SimpleFramework.
 * modeled after org.simpleframework.http.ConnectionTest
 */
public class HttpServer implements Container
{


	/**
	 * @param port the port to handle requests for
	 * @param httpHandler the handler for the content
	 * @throws Exception for errors
	 */
	public HttpServer (int port, HttpHandler httpHandler) throws Exception
    {
       httpHandler.connectToServer (this);
       Allocator allocator = new FileAllocator ();
       TransportProcessor processor  = new ContainerTransportProcessor (this, allocator, 5);
       SocketProcessor server = new TransportSocketProcessor (processor);
       this.connection = new SocketConnection (new Processor (server));
       this.address = new InetSocketAddress (port);
       this.httpHandler = httpHandler;
       this.port = port;
    }
    private final Connection connection;
    private final SocketAddress address;
	private HttpHandler httpHandler;
	private int port;


    /**
     * @throws Exception for errors
     */
    public void start () throws Exception
    {
       try
       {
    	   if (tracing) System.err.println ("Starting HTTPD...");
           connection.connect (address);
       }
       finally
       {
    	   if (tracing) System.err.println (ACTIVE + port);
    	   currentlyActive = true;
       }
    }
    public boolean isActive () { return currentlyActive; }
    boolean currentlyActive = false;


    /**
     * @throws Exception for errors
     */
    public void stop () throws Exception
    {
       connection.close (); currentlyActive = false;
       if (tracing) System.err.println (INACTIVE + port);
    }
    public static final String
    INACTIVE = "Server is now inactive on port ",
    ACTIVE = "HTTPD Started on port ";


    /* (non-Javadoc)
	 * @see org.simpleframework.http.core.Container#handle(org.simpleframework.http.Request, org.simpleframework.http.Response)
	 */
	@Override
	public void handle (Request req, Response resp)
	{
		if (verbose) System.err.println (req);
		httpHandler.handle (req, resp);
	}


	/**
	 * socket processor layer
	 */
	private static class Processor implements SocketProcessor
	{
      
		/* (non-Javadoc)
		 * @see org.simpleframework.transport.SocketProcessor#process(org.simpleframework.transport.Socket)
		 */
		public void process (Socket socket) throws IOException
		{
			if (tracing) System.err.println ("Process Socket...");
			server.process (socket);
		}
      
		/* (non-Javadoc)
		 * @see org.simpleframework.transport.SocketProcessor#stop()
		 */
		public void stop () throws IOException
		{
			if (tracing) System.err.println ("Stop Socket Processor...");
			server.stop ();
		}

		public Processor (SocketProcessor server) { this.server = server; }
		private SocketProcessor server;

	}


	/**
	 * @param enabled TRUE = enable tracing
	 */
	public static void setTrace (boolean enabled) { tracing = enabled; }
	public static void setVerbose () { verbose = true; }
	static boolean tracing = false, verbose = false;


	/**
	 * @return a Runnable starter
	 */
	public Runnable runnerFor ()
	{
		return new Runnable ()
				{
					public void run ()
					{
						try { start (); }
						catch (Exception e) { e.printStackTrace (); }
					}
				};
	}


	/**
	 * start server in new Thread
	 */
	public void runAsThread ()
	{
		new Thread (runnerFor ()).start ();
	}


}

