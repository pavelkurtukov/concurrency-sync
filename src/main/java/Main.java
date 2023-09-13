import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Main {
    final static int ROUTE_COUNT = 1000; // Кол-во генерируемых маршрутов
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) {

        System.out.println(generateRoute("RLRFR", 100));

        sizeToFreq.put(12, 7);
        sizeToFreq.put(10, 5);
        sizeToFreq.put(8, 88);
        sizeToFreq.put(16, 4);

        int maxFreqKey = 0; // Самое частое кол-во повторений буквы "R" в маршруте
        int maxFreqValue = 0; // Максимальное кол-во повторений частоты повторений букры "R" в маршруте

        // Находим самое частое кол-во повторений
        for (Map.Entry<Integer, Integer> freq : sizeToFreq.entrySet()) {
            if  (freq.getValue() > maxFreqValue) {
                maxFreqValue = freq.getValue();
                maxFreqKey = freq.getKey();
            }
        }

        System.out.printf("Самое частое количество повторений %s (встретилось %s раз)\n", maxFreqKey, maxFreqValue);

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
