
package net.myorb.httpd;

import java.io.File;

public class DebugDriver
{

	public static void main (String[] args) throws Exception
	{
		HttpServer.setTrace (true);
		//HttpHandler handler = new DebugHandler ();
		HttpHandler handler = new ZipHandler (content);
		HttpServer server = new HttpServer (8081, handler);
		server.start ();
	}
	static File content = new File ("content/javadocs.zip");

}
