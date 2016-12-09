package uq.deco2800.singularity.common.util;

import javax.ws.rs.core.Response.Status;

/**
 * Loosely based off of HTTP Status codes. 
 * https://en.wikipedia.org/wiki/List_of_HTTP_status_codes
 * 
 * 
 * @author Dion
 *
 */
public class StatusUpdateCodes {

	// Success
	public static final int OK = Status.OK.getStatusCode();
	public static final int REGISTERED = 230; 

	// Errors begin at 400
	public static final int FORBIDDEN = Status.FORBIDDEN.getStatusCode();
	public static final int BAD_REQUEST = Status.BAD_REQUEST.getStatusCode();
	public static final int NOT_FOUND = Status.NOT_FOUND.getStatusCode();
}
