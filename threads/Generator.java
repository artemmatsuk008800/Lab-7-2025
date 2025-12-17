package threads;

import functions.Function;
import functions.basic.Log;

public class Generator extends Thread {
    private final Task task;
    private final ReadWriteSemaphore semaphore;

    public Generator(Task task, ReadWriteSemaphore semaphore) {
        if (task == null || semaphore == null) {
            throw new IllegalArgumentException("Аргументы не могут быть null");
        }
        this.task = task;
        this.semaphore = semaphore;
        this.setName("Generator-Thread");
    }

    public void run() {
        try {
            int taskCount = task.getTaskCount();

            for (int i = 0; i < taskCount && !isInterrupted(); i++) {
                // Создаем логарифмическую функцию со случайным основанием от 1 до 10
                double base = 1.0 + Math.random() * 9.0;
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

                // Захватываем семафор для записи
                semaphore.lockWrite();
                try {
                    // Атомарно устанавливаем все параметры
                    task.setAll(logFunction, leftBorder, rightBorder, step);

                    // Выводим сообщение о параметрах задачи
                    System.out.printf("%s: Source %.4f %.4f %.4f (основание: %.4f, задача %d/%d)%n",
                            getName(), leftBorder, rightBorder, step, base, i + 1, taskCount);

                } finally {
                    // Всегда освобождаем семафор
                    semaphore.unlockWrite();
                }

                // Небольшая пауза для демонстрации работы потоков
                Thread.sleep(10);
            }

            if (!isInterrupted()) {
                System.out.printf("%s: Все %d задач сгенерированы%n", getName(), taskCount);
            } else {
                System.out.printf("%s: Прервано после генерации части задач%n", getName());
            }

            // Помечаем задачу как завершенную
            task.finish();

        } catch (InterruptedException e) {
            System.out.printf("%s: Поток был прерван во время работы%n", getName());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.out.printf("%s: Ошибка - %s%n", getName(), e.getMessage());
            e.printStackTrace();
        }
    }
}