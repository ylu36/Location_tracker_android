package dbconnection;
import javax.inject.Inject;
import akka.actor.ActorSystem;
import play.libs.concurrent.CustomExecutionContext;

    //ref: https://www.playframework.com/documentation/2.6.x/api/java/play/libs/concurrent/CustomExecutionContext.html

    public class DatabaseExecutionContext extends CustomExecutionContext {

        // Dependency inject the actorsystem from elsewhere

        @Inject
        public DatabaseExecutionContext(ActorSystem actorSystem) {
            super(actorSystem, "play.db");
    }
}