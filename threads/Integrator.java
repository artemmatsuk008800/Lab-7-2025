package threads;

import functions.Functions;

public class Integrator extends Thread {
    private final Task task;
    private final ReadWriteSemaphore semaphore;
    private int processedCount = 0;

    public Integrator(Task task, ReadWriteSemaphore semaphore) {
        if (task == null || semaphore == null) {
            throw new IllegalArgumentException("Аргументы не могут быть null");
        }
        this.task = task;
        this.semaphore = semaphore;
        this.setName("Integrator-Thread");
    }

    public void run() {
        try {
            int taskCount = task.getTaskCount();

            while ((processedCount < taskCount) && !isInterrupted() && !task.isFinished()) {
                // Захватываем семафор для чтения
                semaphore.lockRead();
                try {
                    // Получаем все параметры задачи
                    Task.TaskData taskData = task.getAll();

                    if (taskData.function == null) {
                        // Если данных еще нет, ждем
                        continue;
                    }

                    // Вычисляем значение интеграла
                    double integralResult;
                    try {
                        integralResult = Functions.integrate(
                                taskData.function,
                                taskData.leftBorder,
                                taskData.rightBorder,
                                taskData.step
                        );

                        processedCount++;

                        // Выводим результат
                        System.out.printf("%s: Result %.4f %.4f %.4f %.8f (обработано %d/%d)%n",
                                getName(), taskData.leftBorder, taskData.rightBorder,
                                taskData.step, integralResult, processedCount, taskCount);

                    } catch (IllegalArgumentException e) {
                        System.out.printf("%s: Ошибка интегрирования - %s%n", getName(), e.getMessage());
                        processedCount++;
                    }

                } finally {
                    // Всегда освобождаем семафор
                    semaphore.unlockRead();
                }

                // Небольшая пауза для демонстрации работы потоков
                Thread.sleep(10);
            }

            if (!isInterrupted()) {
                System.out.printf("%s: Все %d задач обработаны%n", getName(), taskCount);
            } else {
                System.out.printf("%s: Прервано после обработки %d задач%n", getName(), processedCount);
            }

        } catch (InterruptedException e) {
            System.out.printf("%s: Поток был прерван во время работы%n", getName());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.out.printf("%s: Критическая ошибка - %s%n", getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public int getProcessedCount() {
        return processedCount;
    }
}