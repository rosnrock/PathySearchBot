
public final class SystemUtils {

    public static void interrupt(Long ms) {

        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
