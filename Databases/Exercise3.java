import java.util.*;
import uk.ac.cam.cl.databases.moviedb.MovieDB;
import uk.ac.cam.cl.databases.moviedb.model.*;

public class Exercise3 {
    public static void main(String[] args) {
  // The set of person_id's that we want to count
  // If you want to add id (of type int) to the set
  // simply use pid_set.add(id) 
        Set<Integer> pid_set = new HashSet<Integer>();
        // open database 
        try (MovieDB database = MovieDB.open(args[0])) {
        	
        	Person person = database.getPersonById(3382035);
        
        	for (Role role : person.getActorIn()) {
        		Movie movie = database.getMovieById(role.getMovieId());
        		for (CreditActor actor : movie.getActors()) {
        			int actorID = actor.getPersonId();
        			if (actorID !=3382035) {
                		pid_set.add(actorID);
                	}
        	
        		}
           }
            System.out.println(pid_set.size());
        }
    }
}