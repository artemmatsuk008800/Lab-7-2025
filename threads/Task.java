package threads;

import functions.Function;

public class Task {
    private Function function;
    private double leftBorder;
    private double rightBorder;
    private double step;
    private int taskCount;
    private volatile boolean isFinished = false;

    public Task() {
        // Конструктор по умолчанию
    }

    // Упрощенные методы без синхронизации
    public void setFunction(Function function) {
        this.function = function;
    }

    public void setIntegrationBorders(double leftBorder, double rightBorder) {
        this.leftBorder = leftBorder;
        this.rightBorder = rightBorder;
    }

    public void setStep(double step) {
        this.step = step;
    }

    public Function getFunction() {
        return function;
    }

    public double getLeftBorder() {
        return leftBorder;
    }

    public double getRightBorder() {
        return rightBorder;
    }

    public double getStep() {
        return step;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void finish() {
        isFinished = true;
    }

    public boolean isFinished() {
        return isFinished;
    }

    // Метод для атомарной установки всех параметров
    public void setAll(Function function, double leftBorder, double rightBorder, double step) {
        this.function = function;
        this.leftBorder = leftBorder;
        this.rightBorder = rightBorder;
        this.step = step;
    }

    // Метод для атомарного получения всех параметров
    public TaskData getAll() {
        return new TaskData(function, leftBorder, rightBorder, step);
    }

    // Вспомогательный класс для передачи всех параметров задачи
    public static class TaskData {
        public final Function function;
        public final double leftBorder;
        public final double rightBorder;
        public final double step;

        public TaskData(Function function, double leftBorder, double rightBorder, double step) {
            this.function = function;
            this.leftBorder = leftBorder;
            this.rightBorder = rightBorder;
            this.step = step;
        }
    }
}