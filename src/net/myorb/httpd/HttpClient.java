
package net.myorb.httpd;

import java.io.InputStream;
import java.io.OutputStream;

import java.net.Socket;
import java.net.URL;

/**
 * HTTP protocol client layer implementation
 * @author Michael Druckman
 */
public class HttpClient
{


	/**
	 * constants used building request packets
	 */
	public static String CRLF = "\r\n", PROTOCOL = "HTTP/1.1" + CRLF;


	/**
	 * format a socket request packet
	 * @param resource the resource being requested
	 * @param fromHost address of server hosting request
	 * @return the request block for socket request protocol
	 * @throws Exception for any errors
	 */
	public static String requestFor (String resource, String fromHost) throws Exception
	{
		String host = "Host: " + fromHost + CRLF;
		return "GET /" + resource + " " + PROTOCOL + host + CRLF;
	}


	/**
	 * format a request URL
	 * @param resource the resource being requested
	 * @param fromPort the port number to be used
	 * @return the text of the formatted URL
	 * @throws Exception for any errors
	 */
	public static String get (String resource, int fromPort) throws Exception
	{
		return get (resource, fromPort, "localhost");
	}
	public static String get (String resource, int fromPort, String fromHost) throws Exception
	{
		URL target = new URL
			(
				"http://" + hostPort (fromPort, fromHost) + "/" + resource
			);
		return readBlock (target.openStream ());
	}
	public static String hostPort (int port, String host)
	{
		return host + ":" + port;
	}


	/**
	 * read response from socket for requested packet
	 * @param resource the resource being requested
	 * @param fromPort the port number to be used
	 * @return the response read for the packet
	 * @throws Exception for any errors
	 */
	public static String read (String resource, int fromPort) throws Exception
	{
		return readBlock (requestFor (resource, fromPort));
	}


	/**
	 * open socket and send request
	 * @param resource the resource being requested
	 * @param fromPort the port number being used
	 * @return the generated socket object
	 * @throws Exception for any errors
	 */
	public static Socket requestFor (String resource, int fromPort) throws Exception
	{
		return requestFor (resource, fromPort, "localhost");
	}
	public static Socket requestFor (String resource, int fromPort, String fromHost) throws Exception
	{
		Socket socket =
			new Socket (fromHost, fromPort);
		OutputStream out = socket.getOutputStream ();
		out.write (requestFor (resource, fromHost).getBytes ()); out.flush ();
		return socket;
	}


	/**
	 * read from socket input stream
	 * @param socket the socket used for the request
	 * @return the response read from socket converted to String
	 * @throws Exception for any errors
	 */
	public static String readBlock (Socket socket) throws Exception
	{
		InputStream in = socket.getInputStream ();
		String response = readBlock (in); socket.close ();
		return response;
	}


	/**
	 * read block from input stream
	 * @param in the stream to read from
	 * @return the block of data converted to String
	 * @throws Exception for any errors
	 */
	public static String readBlock (InputStream in) throws Exception
	{
		byte [] block;
		int count = in.read (block = new byte [BLOCK_SIZE]);
		return new String (block, 0, count);
	}
	public static final int BLOCK_SIZE = 4096;


}

