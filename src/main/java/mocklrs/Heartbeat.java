package mocklrs;


import java.util.Timer;
import java.util.TimerTask;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;




@Path("hb")
public class Heartbeat {
	private static class ExitTask extends TimerTask {
		@Override
		public void run() {
    	    System.out.println("No heartbeat received within the interval. Exiting..");
            System.exit(0);
			
		}		
	}
	
	private static Timer timer = new Timer("hb_timer");

	
    private static TimerTask task = new ExitTask();


	private static final long hbdelay = 1000L * 60; //45 seconds
	
	@GET
	@Path("hb")
	public Response hbRecd()  {
		try {
			task.cancel();
			timer.purge();
		} catch (Exception e) {
			//ignore
		}

		
		task = new ExitTask();
		timer.schedule(task, hbdelay);
        ResponseBuilder response = Response.ok();  
        return response.build();  
	}
	
	@GET
	@Path("hboff")
	public Response hbOff()  {

		System.out.println("HB_OFF");
		try {
			task.cancel();
			timer.purge();
		} catch(Exception e){
			//nothing
		}
       
        ResponseBuilder response = Response.ok();  
        return response.build();

	}

}