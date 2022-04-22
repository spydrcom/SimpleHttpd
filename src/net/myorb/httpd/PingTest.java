
package net.myorb.httpd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class PingTest
{

	int port = 8081;
	
	public static void main (String[] args) throws Exception
	{
//		PingTest p = new PingTest ();
//		System.out.println (p.pingWithSocket());
//		System.out.println ("====================");
//		System.out.println (p.pingWithURL());
		System.out.println (HttpClient.read ("index.html", 8081));
	}

    /**
     * This works as it opens a socket and sends the request.
     * This will split using the CRLF and CRLF ending.
     * 
     * @return the response body
     * 
     * @throws Exception if the socket can not connect
     */
    public String pingWithSocket() throws Exception {
       java.net.Socket socket = new java.net.Socket("localhost", port);
       OutputStream out = socket.getOutputStream();
       out.write(
             ("GET /index.html HTTP/1.1\r\n" +
             "Host: localhost\r\n"+
             "\r\n").getBytes());
       out.flush();
       InputStream in = socket.getInputStream();
       byte[] block = new byte[1024];
       int count = in.read(block);       
       String result = new String(block, 0, count);
       String parts[] = result.split("\r\n\r\n");
       
		socket.close();
       if(!result.startsWith("HTTP")) {
          throw new IOException("Header is not valid");
       }
		System.out.println(parts[0]);
		System.out.println("========xxxxxxx=======");
       //sockets.add(socket);
       return parts[1];
    }
    
    /**
     * Use the standard URL tool to get the content.
     * 
     * @return the response body
     * 
     * @throws Exception if a connection can not be made.
     */
    public String pingWithURL() throws Exception {
       URL target = new URL("http://localhost:"+ port+"/index.html");
       InputStream in = target.openStream();
       byte[] block = new byte[1024];
       int count = in.read(block);       
       String result = new String(block, 0, count);
       
       return result;
    }

}
