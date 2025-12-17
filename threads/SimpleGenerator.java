package threads;

import functions.Function;
import functions.basic.Log;

public class SimpleGenerator implements Runnable {
    private Task task;

    public SimpleGenerator(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Задача не может быть null");
        }
        this.task = task;
    }

    public void run() {
        try {
            int taskCount = task.getTaskCount();

            for (int i = 0; i < taskCount; i++) {
                // Создаем логарифмическую функцию со случайным основанием от 1 до 10
                double base = 1.0 + Math.random() * 9.0; // от 1 до 10
                // Избегаем основания = 1
                if (Math.abs(base - 1.0) < 1e-10) {
                    base = 1.5;
                }
                Function logFunction = new Log(base);

                // Левая граница области интегрирования (от 0 до 100)
                double leftBorder = Math.random() * 100.0;

                // Правая граница области интегрирования (от 100 до 200)
                double rightBorder = 100.0 + Math.random() * 100.0;

                // Убедимся, что левая граница меньше правой
                if (leftBorder >= rightBorder) {
                    double temp = leftBorder;
                    leftBorder = rightBorder;
                    rightBorder = temp;
                }

                // Проверяем, что левая граница > 0
                if (leftBorder <= 0) {
                    leftBorder = 0.1;
                }

                // Шаг дискретизации (от 0 до 1, но > 0)
                double step = Math.random();
                if (step <= 0) {
                    step = 0.01;
                }

                // Синхронизация для атомарной установки всех параметров
                synchronized (task) {
                    // Устанавливаем параметры задачи
                    task.setFunction(logFunction);
                    task.setIntegrationBorders(leftBorder, rightBorder);
                    task.setStep(step);

                    // Выводим сообщение о параметрах задачи
                    System.out.printf("Generator: Source %.4f %.4f %.4f (основание логарифма: %.4f)%n",
                            leftBorder, rightBorder, step, base);
                }

                // Небольшая пауза для демонстрации работы потоков
                Thread.sleep(10);
            }

            System.out.println("Generator: Задачи сгенерированы");

        } catch (InterruptedException e) {
            System.out.println("Generator: Поток был прерван");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.out.println("Generator: Ошибка - " + e.getMessage());
            e.printStackTrace();
        }
    }
}