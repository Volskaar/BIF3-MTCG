import httpclient.controller.UserController;
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
        router.addService("/user", new UserController());
        router.addService("/user/{userId}", new UserController());
        router.addService("/user/login", new UserController());
        return router;
    }
}