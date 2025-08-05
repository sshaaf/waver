package dev.shaaf.waver.backend;

import io.quarkus.funqy.Funq;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

@ApplicationScoped
public class WaverFunqy {

    @Inject
    Event<WaverProcessEvent> processingEvent;

    @Funq
    public void process(WaverProcessEvent request) {
        if (request == null || request.sourceUrl() == null) {
            System.err.println("Received invalid request: payload or sourceUrl is null.");
            return;
        }

        System.out.println("FUNQY_ENDPOINT: Received request for " + request.sourceUrl() + ". Handing off to background processor.");

        // call back immediately.
        processingEvent.fire(request);
    }
}