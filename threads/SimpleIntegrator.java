package threads;

import functions.Function;
import functions.Functions;

public class SimpleIntegrator implements Runnable {
    private Task task;

    public SimpleIntegrator(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Задача не может быть null");
        }
        this.task = task;
    }

    public void run() {
        try {
            int taskCount = task.getTaskCount();

            for (int i = 0; i < taskCount; i++) {
                // Получаем все параметры атомарно
                Function function;
                double leftBorder, rightBorder, step;

                // Синхронизация для атомарного получения всех параметров
                synchronized (task) {
                    function = task.getFunction();
                    leftBorder = task.getLeftBorder();
                    rightBorder = task.getRightBorder();
                    step = task.getStep();

                }

                // Проверяем, что функция не null
                if (function == null) {
                    throw new NullPointerException("Функция не определена");
                }

                // Вычисляем значение интеграла
                double integralResult;
                try {
                    integralResult = Functions.integrate(function, leftBorder, rightBorder, step);

                    // Выводим результат
                    System.out.printf("Integrator: Result %.4f %.4f %.4f %.8f%n",
                            leftBorder, rightBorder, step, integralResult);

                } catch (IllegalArgumentException e) {
                    System.out.printf("Integrator: Ошибка интегрирования - %s%n", e.getMessage());
                }

                // Небольшая пауза для демонстрации работы потоков
                Thread.sleep(10);
            }

            System.out.println("Integrator: Все задачи обработаны");

        } catch (InterruptedException e) {
            System.out.println("Integrator: Поток был прерван");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.out.println("Integrator: Критическая ошибка - " + e.getMessage());
            e.printStackTrace();
        }
    }
}