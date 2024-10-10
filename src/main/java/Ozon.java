import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * В задачке на GO 2 проблемы - отсутствие синхронизации при работе горутин с phones + не реализовано ожидание
 * завершения работы горутин. Реализован код на Java с использованием synchronizedList, вместо горутин взят Thread,
 * все треды гарантированно завершат свою работу.
 */


public class Ozon {
    public static void main(String[] args) throws InterruptedException {
        Phones td = new Phones();
        Scanner in = new Scanner(System.in);
        System.out.print("Введите число n для вызова генератора : ");
        int n = in.nextInt();
        generate(n, td);
        System.out.println("Количество телефонных номеров: " + td.getPhones().size());
    }

    public static void generate(int n, Phones td) throws InterruptedException {
        if (n < 1 || n > 1000) {
            System.out.println("Ошибка: количество должно быть от 1 до 1000.");
            return;
        }

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            Thread thread = new Thread(td::add);
            thread.start();
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }
}
