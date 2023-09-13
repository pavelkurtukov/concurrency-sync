import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Main {
    final static int ROUTE_COUNT = 1000; // Кол-во генерируемых маршрутов
    final static int ROUTE_LENGTH = 100; // Кол-во генерируемых маршрутов
    final static String ROUTE_LETTERS = "RLRFR"; // Кол-во генерируемых маршрутов

    // В ключах попавшиеся частоты буквы 'R', а в значениях — количество раз их появления.
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    static int maxFreqKey = 0; // Самое частое кол-во повторений буквы "R" в маршруте
    static int maxFreqValue = 0; // Максимальное кол-во повторений частоты повторений букры "R" в маршруте

    static int doneThreads = 0; // Кол-во завершённых потоков (для отслеживания конца процесса анализа)

    public static void main(String[] args) {

        // Процесс генерации, подсчёта букв "R" и вывод результата для одного маршрута
        Runnable analyzeRoute = () -> {
            // Генерируем строку (маршрут)
            String route = generateRoute(ROUTE_LETTERS, ROUTE_LENGTH);
            // Считаем кол-во символов "R" (команд поворота направо)
            int rCount = route.length() - route.replace("R", "").length();
            // Выводим результат на экран
            System.out.printf("%s - %d\n", route, rCount);

            // Критическая секция для работы потоков с общей мапой
            synchronized (sizeToFreq) {
                if (sizeToFreq.containsKey(rCount)) {
                    // Если значение частоты уже есть в мапе - увеличиваем её число повторений на 1
                    sizeToFreq.put(rCount, sizeToFreq.get(rCount) + 1);
                } else {
                    // Иначе - добавляем со значением 1
                    sizeToFreq.put(rCount, 1);
                }
                sizeToFreq.notify();
            }

            // Инкрементим число завершённых потоков
            doneThreads++;
        };

        // Определение текущего максимума в мапе
        Runnable printCurrentMaxSize = () -> {
            while (!Thread.interrupted()) {
                synchronized (sizeToFreq) {

                    try {
                        sizeToFreq.wait(); // Ожидаем освобождения мапы
                    } catch (InterruptedException e) {
                        return; // Завершаем поток
                    }

                    // Находим самое частое кол-во повторений
                    for (Map.Entry<Integer, Integer> freq : sizeToFreq.entrySet()) {
                        if (freq.getValue() > maxFreqValue) {
                            maxFreqValue = freq.getValue();
                            maxFreqKey = freq.getKey();
                        }
                    }
                }

                System.out.printf("Текущее самое частое количество повторений: %s (встретилось %s раз)\n", maxFreqKey, maxFreqValue);
            }
        };

        // Стартуем поток для мониторинга текущего состояния мапы
        Thread monitorThread = new Thread(printCurrentMaxSize);
        monitorThread.start();

        // Стартуем потоки для анализа
        for (int i = 0; i < ROUTE_COUNT; i++) {
            Thread thread = new Thread(analyzeRoute);
            thread.start();
        }

        // Чтобы не создавать массив потоков или пул потоков, будем каждые 100 милисекунд проверять кол-во выполненных потоков
        while (doneThreads < ROUTE_COUNT) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Завершаем поток мониторинга мапы
        monitorThread.interrupt();

        // Выводим итоговый максимум
        System.out.printf("\nСамое частое количество повторений %s (встретилось %s раз)\n", maxFreqKey, maxFreqValue);

        // Выводим остальные результаты
        for (Map.Entry<Integer, Integer> freq : sizeToFreq.entrySet()) {
            if (freq.getKey() != maxFreqKey) {
                System.out.printf(" - %s (%s раз)\n", freq.getKey(), freq.getValue());
            }
        }
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }
}
