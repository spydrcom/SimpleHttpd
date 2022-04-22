
package net.myorb.httpd;

import java.io.File;

public class JreDocsServer
{

	/**
	 * @param args not used
	 * @throws Exception for failure to start server
	 */
	public static void main (String[] args) throws Exception
	{
		ZipHandler.serve (new File ("JRE/javadocs.zip"), 8881);
		ZipHandler.serve (new File ("JRE_JAVAX/javadocs.zip"), 8882);
		ZipHandler.serve (new File ("JRE_XML/javadocs.zip"), 8883);
	}

}
