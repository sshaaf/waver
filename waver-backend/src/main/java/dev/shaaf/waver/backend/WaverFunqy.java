package dev.shaaf.waver.backend;

import io.quarkus.funqy.Funq;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class WaverFunqy {

    @Inject
    @Channel("requests")
    Emitter<WaverProcessEvent> requestEmitter;

    // FIX: The value here now matches the ce-type from your curl command.
    @Funq("requests")
    public void process(WaverProcessEvent request) {
        if (request == null || request.sourceUrl() == null) {
            System.err.println("Received invalid request: payload or sourceUrl is null.");
            return;
        }

        System.out.println("FUNQY_ENDPOINT: Received request for " + request.sourceUrl() + ". Handing off to background processor.");

        // call back immediately and forward
        requestEmitter.send(request);
    }
}