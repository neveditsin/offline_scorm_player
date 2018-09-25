package mocklrs;


import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;


@Path("activities/state")
public class State {
    @POST
    @Consumes("text/plain")
    public void process(String data) {    	
        System.out.println(data.toString());
    }
}