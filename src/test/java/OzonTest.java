import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OzonTest {

    private static Stream<Arguments> provideCountsForMultipleCalls() {
        return Stream.of(
                Arguments.of(new int[]{10, 20, 50}, 80),
                Arguments.of(new int[]{5, 15, 30, 50}, 100),
                Arguments.of(new int[]{100, 200, 300}, 600)
        );
    }

    @ParameterizedTest(name = "Test generate with n = {0}")
    @ValueSource(ints = {0, -1, 1001})
    public void testInvalidThreadCounts(int n) throws InterruptedException {
        Phones td = new Phones();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream originalOut = System.out; // Keep original System.out
        System.setOut(new PrintStream(baos));
        Ozon.generate(n, td);
        System.setOut(originalOut);
        String output = baos.toString();
        String expectedMessage = "Ошибка: количество должно быть от 1 до 1000.";
        assertTrue(output.contains(expectedMessage), "Error message should be printed for invalid n = " + n);
        assertEquals(0, td.getPhones().size(), "Phone list should be empty when n = " + n);
    }

    @ParameterizedTest(name = "Test generate with valid n = {0}")
    @ValueSource(ints = {1, 100, 1000})
    public void testValidThreadCounts(int n) throws InterruptedException {
        Phones td = new Phones();
        Ozon.generate(n, td);
        assertEquals(n, td.getPhones().size(), "Phone list should contain " + n + " phone numbers when n = " + n);
        for (long phoneNumber : td.getPhones()) {
            assertTrue(isValidPhoneNumber(phoneNumber), "Generated phone number should be within valid range");
        }
    }

    @ParameterizedTest(name = "Test multiple generate calls with counts: {0}")
    @MethodSource("provideCountsForMultipleCalls")
    public void testMultipleGenerateCalls(int[] counts, int expectedTotal) throws InterruptedException {
        Phones td = new Phones();

        for (int n : counts) {
            Ozon.generate(n, td);
        }
        assertEquals(expectedTotal, td.getPhones().size(), "After multiple calls, phone list should contain " + expectedTotal + " phone numbers");
    }

    @Test
    public void testThreadSafety() throws InterruptedException {
        Phones td = new Phones();

        int numberOfThreads = 100;
        int operationsPerThread = 10;

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numberOfThreads; i++) {
            Thread thread = new Thread(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    td.add();
                }
            });
            threads.add(thread);
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }

        int expectedTotal = numberOfThreads * operationsPerThread;
        assertEquals(expectedTotal, td.getPhones().size(), "Total phone numbers should match expected total in concurrent access");
        for (long phoneNumber : td.getPhones()) {
            assertTrue(isValidPhoneNumber(phoneNumber), "Generated phone number should be within valid range");
        }
    }

    // Helper method to check if phone number is valid
    private boolean isValidPhoneNumber(long phoneNumber) {
        long min = 89100000000L;    // 89100000000
        long max = 89999999999L;    // 89999999999
        return phoneNumber >= min && phoneNumber <= max;
    }
}