package br.ufc.great.tsd;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class DemoServer extends Application {

    /**
     * Creates a root Restlet that will receive all incoming calls.
     */
    @Override
    public Restlet createInboundRoot() {
        // Create a router Restlet that routes each call to a
        // new instance of HelloWorldResource.
        Router router = new Router(getContext());

        // Defines only one route
        router.attach("/users", UserResource.class);
        router.attach("/users/{id}", UserResource.class);
        router.attach("/users/{newUser}", UserResource.class);
        router.attach("/users/{user}", UserResource.class);
        router.attach("/users/{idToDelete}", UserResource.class);
        router.attach("/users/{email}/{senha}", UserResource.class);

        return router;
    }
}