package gecko10000.AdventureCalendar.misc;

import me.arcaniax.hdb.api.DatabaseLoadEvent;
import redempt.redlib.misc.EventListener;

import java.util.concurrent.CompletableFuture;

public class HeadDBLoader {

    public static CompletableFuture<Void> dbLoad() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        new EventListener<>(DatabaseLoadEvent.class, e -> future.complete(null));
        return future;
    }

}
