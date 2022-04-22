
package net.myorb.httpd;

import java.io.File;

/**
 * tailored HTTPD server for delivery of JavaDocs from ZIP file.
 *  assumption is made that file javadocs.zip exists in start-up directory.
 *  main program provide command line start ability with assumption of use of port 8081.
 *  static start method may be used to specify alternate port.
 * @author Michael Druckman
 */
public class JavaDocsServer
{

	/**
	 * @param args optional port number
	 * @throws Exception for failure to start server
	 */
	public static void main (String[] args) throws Exception
	{
		int port = 8081;
		if (args.length > 0) port = Integer.parseInt (args[0]);
		start (port);
	}

	/**
	 * @param onPort port to use for serving docs
	 * @return the server object that was started
	 * @throws Exception for any errors
	 */
	public static HttpServer start (int onPort) throws Exception
	{
		return ZipHandler.serve (new File ("javadocs.zip"), onPort);
	}

}
