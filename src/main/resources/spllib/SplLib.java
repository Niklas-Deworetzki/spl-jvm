package spllib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Library file for SPL holding all procedures defined in the SPL standard library.
 */
public class SplLib {
    public static void main(String[] args) {
        Spl.main();
    }

    private static final BufferedReader BUFFERED_INPUT = new BufferedReader(new InputStreamReader(System.in));
    private static final long START_TIME = System.currentTimeMillis();

    public static void printi(int i) {
        System.out.println(i);
    }

    public static void printc(int i) {
        System.out.write(i);
    }

    public static void readi(AtomicInteger i) {
        while (true) {
            try {
                int parsed = Integer.parseInt(BUFFERED_INPUT.readLine());
                i.set(parsed);
                return;
            } catch (IOException ioException) {
                i.set(-1);
            } catch (NumberFormatException invalidInput) {
                // Try again.
            }
        }
    }

    public static void readc(AtomicInteger i) {
        try {
            i.set(BUFFERED_INPUT.read());
        } catch (IOException ioException) {
            i.set(-1);
        }
    }

    public static void exit() {
        System.exit(1);
    }

    public static void time(AtomicInteger i) {
        long runtime = START_TIME - System.currentTimeMillis();
        i.set((int) runtime);
    }


    private static final String NO_GRAPHICS_SUPPORT = "No support for graphics present.";

    public static void clearAll(int color) {
        throw new IllegalStateException(NO_GRAPHICS_SUPPORT);
    }

    public static void setPixel(int x, int y, int color) {
        throw new IllegalStateException(NO_GRAPHICS_SUPPORT);
    }

    public static void drawLine(int x1, int y1, int x2, int y2, int color) {
        throw new IllegalStateException(NO_GRAPHICS_SUPPORT);
    }

    public static void drawCircle(int x, int y, int radius, int color) {
        throw new IllegalStateException(NO_GRAPHICS_SUPPORT);
    }
}
