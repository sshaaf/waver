package dev.shaaf.waver.backend;

import io.quarkus.funqy.Funq;

public class WaverHello {

    @Funq
    public String greet(String person) {
        return "Hello from Waver!";
    }
}
