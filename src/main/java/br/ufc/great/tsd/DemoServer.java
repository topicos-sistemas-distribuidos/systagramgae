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
        Router router = new Router(getContext());

        router.attach("/users", UserResource.class);
        router.attach("/users/{id}", UserResource.class);
        router.attach("/users/{newUser}", UserResource.class);
        router.attach("/users/{user}", UserResource.class);
        router.attach("/users/{idToDelete}", UserResource.class);
        router.attach("/users/{email}/{senha}", UserResource.class);
        
        router.attach("/person", PersonResource.class);
        router.attach("/person/{id}/comment", PersonResource.class);
        router.attach("/person/{id}/likes", PersonResource.class);
        router.attach("/person/comment", PersonResource.class);
        router.attach("/person/likes", PersonResource.class);
        router.attach("/person/{id}/comment/add", PersonResource.class);
        router.attach("/person/{id}/likes/add", PersonResource.class);
        router.attach("/person/{id}/comment/save", PersonResource.class);
        router.attach("/person/{id}/likes/save", PersonResource.class);
        router.attach("/person/{id}/comment/update", PersonResource.class);
        router.attach("/person/{personId}/comment/{commentId}/edit", PersonResource.class);
        router.attach("/person/{personId}/select/picture", PersonResource.class);
        router.attach("/person/{personId}/picture/{pictureId}/edit", PersonResource.class);
        router.attach("/person/{id}/post/{listPosts}", PersonResource.class);
        router.attach("/person/{personId}/picture/{pictureId}/post", PersonResource.class);
        router.attach("/person/{personLogged}/post/{postId}/{type}", PersonResource.class);
        router.attach("/person/{personLogged}/post/{postId}/{type}", PersonResource.class);
        router.attach("/person/{id}/post/search", PersonResource.class);
        
        router.attach("/upload/person/{id}/picture", FileUploadResource.class); // lista as figuras do usuário selecionado
        router.attach("/upload/selected/image/users/{idUser}", FileUploadResource.class);
        router.attach("/upload/selected/picture/person/{personId}", FileUploadResource.class);
        
        return router;
    }
}