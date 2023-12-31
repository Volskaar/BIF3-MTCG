import application.controller.CardController;
import application.controller.DeckController;
import application.controller.UserController;
import application.controller.PackageController;


import httpserver.server.Server;
import httpserver.utils.Router;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(10001, configureRouter());
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Router configureRouter()
    {
        Router router = new Router();

        /*///////////////////////////////////////////////////////////////////
        // initial development testing
        router.addService("/user", new UserController());
        router.addService("/user/{userId}", new UserController());
        router.addService("/user/login", new UserController());

        ///////////////////////////////////////////////////////////////////*/
        // CURL - create users
        router.addService("/users", new UserController());

        // CURL - login users
        router.addService("/sessions", new UserController());

        // CURL - edit user data
        // router.addService("/users/{username}", new UserController());

        /////////////////////////////////////////////////////////////////////

        // CURL - create/add packages
        router.addService("/packages", new PackageController());

        // CURL - acquire packages
        router.addService("/transactions/packages", new PackageController());

        /////////////////////////////////////////////////////////////////////

        // CURL - show cards
        router.addService("/cards", new CardController());

        // CURL - show and configure deck
        router.addService("/deck", new DeckController());

        /////////////////////////////////////////////////////////////////////

        /* WIP

        // CURL - see stats
        router.addService("/stats", new statController());

        // CURL - see scoreboard
        router.addService("/battles", new statController());

        /////////////////////////////////////////////////////////////////////
        // CURL - do battle
        router.addService("/users", new battleController());

        /////////////////////////////////////////////////////////////////////
        // CURL - trades
        router.addService("/tradings", new packageController());
        router.addService("/tradings/{token}", new packageController());

        */

        return router;
    }
}