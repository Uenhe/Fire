package fire.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** A copy of vanilla annotations, never mind. */
public class FRAnnotations{

    public enum Loc{

        server(true, false),
        none(false, false);

        public final boolean isServer;
        public final boolean isClient;

        Loc(boolean server, boolean client){
            this.isServer = server;
            this.isClient = client;
        }
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface Remote{
        Loc called() default Loc.none;
    }
}
