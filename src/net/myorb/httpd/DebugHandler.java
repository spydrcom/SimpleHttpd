
package net.myorb.httpd;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import java.io.PrintStream;

public class DebugHandler implements HttpHandler
{


	/* (non-Javadoc)
	 * @see net.myorb.httpd.HttpHandler#handle(org.simpleframework.http.Request, org.simpleframework.http.Response)
	 */
	public void handle (Request req, Response resp)
	{
		try
		{
			System.err.println (req);
			System.err.println ("add: " + req.getAddress());
			PrintStream out = resp.getPrintStream (1024);
			resp.setValue ("Content-Type", "text/plain");
			out.print ("DEBUG Server response");
			out.close ();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void connectToServer(HttpServer server) {}


}

