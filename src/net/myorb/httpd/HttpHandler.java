
package net.myorb.httpd;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

/**
 * generic handler interface for HTTP content
 * @author Michael Druckman
 */
public interface HttpHandler
{
	/**
	 * generate response from request
	 * @param req request object for service
	 * @param resp response object for service
	 */
	void handle (Request req, Response resp);

	/**
	 * identify server to handler
	 * @param server access to server object
	 */
	void connectToServer (HttpServer server);
}
