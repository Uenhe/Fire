package fire.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** A copy of vanilla's annotations, never mind. */
public class FireAnnotations{

    /** A set of two booleans, one specifying server and one specifying client. */
    public enum Loc{

        /** Method can only be invoked on the client from the server. */
        server(true, false),
        /** Neither server nor client. */
        none(false, false);

        /** If true, this method can be invoked ON clients FROM servers. */
        public final boolean isServer;
        /** If true, this method can be invoked ON servers FROM clients. */
        public final boolean isClient;

        Loc(boolean server, boolean client){
            this.isServer = server;
            this.isClient = client;
        }
    }

    /** Marks a method as invokable remotely across a server/client connection. */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface Remote{

        /** The local locations where this method is called locally, when invoked. */
        Loc called() default Loc.none;
    }
}
