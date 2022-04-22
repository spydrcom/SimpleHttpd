
package net.myorb.httpd;

import net.myorb.data.abstractions.SimpleStreamIO;
import net.myorb.data.abstractions.ZipIndexedSource;
import net.myorb.data.abstractions.ContentTypes;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;

import java.io.FileNotFoundException;
import java.io.File;

/**
 * http request handler.
 * content is served out of ZIP file
 * @author Michael Druckman
 */
public class ZipHandler implements HttpHandler
{


	/**
	 * @param contentSource the source for all content
	 * @throws Exception for all errors
	 */
	public ZipHandler (File contentSource) throws Exception
	{ this.zipIndexedSource = new ZipIndexedSource (contentSource); }
	protected ZipIndexedSource zipIndexedSource;


    /**
     * copy content to response stream
	 * @param requested the address of the requested resource
     * @param using the response object to be used
     * @throws Exception for any errors
     */
    public void respondTo (String requested, Response using) throws Exception
    { zipIndexedSource.copyTo (using.getOutputStream (1024), requested); }


	/**
	 * @param req the request object
	 * @return the address of the requested resource
	 */
	public String addressFor (Request req)
	{
		return req.getAddress ().toString ().substring (1);
	}


	/**
	 * check for CMD resource request.
	 *  if not found respond with requested resource
	 * @param requested the address of the requested resource
	 * @param resp the response object
	 * @throws Exception for errors
	 */
	public void execute (String requested, Response resp) throws Exception
	{
		if (serverCommandprocessor != null)
		{ serverCommandprocessor.checkRequest (requested, resp); }
		resp.setValue ("Content-Type", ContentTypes.getTypeFor (requested));
		if (HttpServer.tracing) System.err.println ("GET " + requested);
		respondTo (requested, resp);
	}


	/**
	 * status set to NOT_FOUND, 404 code sent to user
	 * @param resp the response object
	 */
	public void respondWithError (Response resp)
	{
		resp.setStatus (Status.NOT_FOUND); SimpleStreamIO.forceClosed (resp);
	}


    /* (non-Javadoc)
	 * @see net.myorb.httpd.HttpHandler#handle(org.simpleframework.http.Request, org.simpleframework.http.Response)
	 */
	public void handle (Request req, Response resp)
	{
		try { execute (addressFor (req), resp); }
		catch (ServerCommands.NoSuchCommand noSuch) { System.err.println (noSuch.getMessage ()); respondWithError (resp); }
		catch (ServerCommands.CommandExecuted executed) { System.err.println (executed.getMessage ()); }
		catch (FileNotFoundException e) { respondWithError (resp); }
		catch (Exception e) { e.printStackTrace (); }
	}


	/* (non-Javadoc)
	 * @see net.myorb.httpd.HttpHandler#connectToServer(net.myorb.httpd.HttpServer)
	 */
	@Override
	public void connectToServer (HttpServer server)
	{ this.server = server; serverCommandprocessor = new ServerCommands (server); }
	ServerCommands serverCommandprocessor = null;
	HttpServer server = null;


	/**
	 * start server on specified port
	 * @param content the content to serve
	 * @param onPort the port number to use
	 * @return the server object generated
	 * @throws Exception for errors
	 */
	public static HttpServer serve (File content, int onPort) throws Exception
	{
		HttpServer.setTrace (true);
		HttpHandler handler = new ZipHandler (content);
		HttpServer server = new HttpServer (onPort, handler);
		server.runAsThread ();
		return server;
	}


}

