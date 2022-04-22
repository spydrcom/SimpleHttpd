
package net.myorb.httpd;

import java.util.HashMap;
import java.io.PrintStream;

import org.simpleframework.http.Response;

/**
 * mechanism for providing command line updates to server via URL
 * @author Michael Druckman
 */
public class ServerCommands
{


	/**
	 * exception for command not found
	 */
	@SuppressWarnings ("serial") public static
	class NoSuchCommand extends Exception { NoSuchCommand (String message) { super (message); } }

	/**
	 * exception for command completion notification
	 */
	@SuppressWarnings ("serial") public static class CommandExecuted extends Exception
	{ CommandExecuted (String message) { super (message); } }


	/**
	 * execution mechanism for commands
	 */
	public interface Command
	{
		/**
		 * run requested command
		 * @param resp response object to be used
		 * @throws Exception for errors and state notifications
		 */
		void execute (Response resp) throws Exception;
	}


	/**
	 * name of command maps to execution mechanism
	 */
	static HashMap <String, Command> COMMANDS = new HashMap <> ();


	/**
	 * initialize table of recognized commands
	 */
	void init ()
	{

		COMMANDS.put
		(
			"STOP.cmd",
			new Command ()
			{
				public void execute (Response resp) throws Exception
				{
					scheduleServerStop (resp);
				}
			}
		);

		COMMANDS.put
		(
			"VERBOSE.cmd",
			new Command ()
			{
				public void execute (Response resp) throws Exception
				{
					setServerVerboseTrace (resp);
				}
			}
		);

		COMMANDS.put
		(
			"TRACE.cmd",
			new Command ()
			{
				public void execute (Response resp) throws Exception
				{
					setServerTrace (resp, true);
				}
			}
		);

		COMMANDS.put
		(
			"SILENT.cmd",
			new Command ()
			{
				public void execute (Response resp) throws Exception
				{
					setServerTrace (resp, false);
				}
			}
		);

	}


	/**
	 * send text message to browser
	 * @param resp the response object to be used for message
	 * @param message the message to be sent
	 * @throws Exception for errors
	 */
	public void respondWithMessage (Response resp, String message) throws Exception
	{
		PrintStream out = resp.getPrintStream (1024);
		out.println (message); out.close ();
	}


    /**
     * enable verbose trace.
     *  each request object header will be fully displayed
     * @param resp the response object to be used for message
     * @throws Exception for errors
     */
    public void setServerVerboseTrace (Response resp) throws Exception
    {
    	respondWithMessage (resp, "Verbose trace enabled");
    	HttpServer.setVerbose ();
    }


    /**
     * enable or disable server trace
     * @param resp the response object to be used for message
     * @param enabled TRUE = enable server trace options
     * @throws Exception for errors
     */
    public void setServerTrace (Response resp, boolean enabled) throws Exception
    {
    	respondWithMessage
    	(resp, enabled? "Server trace enabled": "Server trace disabled");
    	HttpServer.setTrace (enabled);
    }


    /**
     * schedule server stop
     * @param resp the response object to be used for message
     * @throws Exception for any errors
     */
    public void scheduleServerStop (Response resp) throws Exception
    {
    	respondWithMessage
    	(resp, server==null? "Unable to stop server": "Server STOP scheduled");
		server.stop ();
    }


    /**
     * execute recognized CMD
     * @param requestedResource text of resource address
     * @param resp a response object for execution of the command
     * @throws Exception for errors and state notifications
     */
    public void execute (String requestedResource, Response resp) throws Exception
    {
    	checkCommand (requestedResource);
    	COMMANDS.get (requestedResource).execute (resp);
    	throw new CommandExecuted ("Command was executed: " + requestedResource);
    }


    /**
     * attempt to recognize command
     * @param requestedResource the name of the resource
     * @throws NoSuchCommand for command not found
     */
    public void checkCommand (String requestedResource) throws NoSuchCommand
    {
    	if ( ! COMMANDS.containsKey (requestedResource) )
    	{
    		throw new NoSuchCommand ("Command not found: " + requestedResource);
    	}
    }


    /**
     * check resource for CMD format
     * @param requestedResource text of resource address
     * @param resp a response object for execution of the command
     * @throws Exception for errors and state notifications
     */
    public void checkRequest (String requestedResource, Response resp) throws Exception
    {
    	if (requestedResource.endsWith (".cmd")) execute (requestedResource, resp);
    }


    /**
     * @param server the server to command
     */
    public ServerCommands (HttpServer server)
    { this.server = server; init (); }
	HttpServer server = null;


}

