package examples;

/**
 * the entry point of all examples
 */
public class Entry {
    public static void main(String[] args) {
        Reactor reactor = new Reactor(9999);
        new Thread(reactor).start();
    }
}
