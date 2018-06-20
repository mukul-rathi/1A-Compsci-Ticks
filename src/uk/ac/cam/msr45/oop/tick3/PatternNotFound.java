package uk.ac.cam.msr45.oop.tick3;

public class PatternNotFound extends Exception {
    public PatternNotFound() {
        super("Error: No such pattern found");
    }
}