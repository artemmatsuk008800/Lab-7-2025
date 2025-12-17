package functions;

import functions.meta.*;

public final class Functions {
    private Functions() {
        throw new AssertionError("Нельзя создавать экземпляры класса Functions");
    }

    public static Function shift(Function f, double shiftX, double shiftY) {
        if (f == null) {
            throw new IllegalArgumentException("Функция не может быть null");
        }
        return new Shift(f, shiftX, shiftY);
    }

    public static Function scale(Function f, double scaleX, double scaleY) {
        if (f == null) {
            throw new IllegalArgumentException("Функция не может быть null");
        }
        if (Math.abs(scaleX) < 1e-10) {
            throw new IllegalArgumentException("Коэффициент масштабирования по X не может быть нулевым");
        }
        return new Scale(f, scaleX, scaleY);
    }

    public static Function power(Function f, double power) {
        if (f == null) {
            throw new IllegalArgumentException("Функция не может быть null");
        }
        return new Power(f, power);
    }

    public static Function sum(Function f1, Function f2) {
        if (f1 == null || f2 == null) {
            throw new IllegalArgumentException("Функции не могут быть null");
        }
        return new Sum(f1, f2);
    }

    public static Function mult(Function f1, Function f2) {
        if (f1 == null || f2 == null) {
            throw new IllegalArgumentException("Функции не могут быть null");
        }
        return new Mult(f1, f2);
    }

    public static Function composition(Function f1, Function f2) {
        if (f1 == null || f2 == null) {
            throw new IllegalArgumentException("Функции не могут быть null");
        }
        return new Composition(f1, f2);
    }

    public static double integrate(Function f, double leftBorder, double rightBorder, double step) {
        if (f == null) {
            throw new IllegalArgumentException("Функция не может быть null");
        }
        if (step <= 0) {
            throw new IllegalArgumentException("Шаг интегрирования должен быть положительным");
        }
        if (leftBorder >= rightBorder) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }

        // Проверяем, что интервал интегрирования находится в области определения функции
        if (leftBorder < f.getLeftDomainBorder() || rightBorder > f.getRightDomainBorder()) {
            throw new IllegalArgumentException("Интервал интегрирования выходит за границы области определения функции");
        }

        double integral = 0.0;
        double currentX = leftBorder;

        // Вычисляем интеграл методом трапеций
        while (currentX < rightBorder) {
            double nextX = Math.min(currentX + step, rightBorder);

            double y1 = f.getFunctionValue(currentX);
            double y2 = f.getFunctionValue(nextX);

            // Если функция не определена в какой-то точке, выбрасываем исключение
            if (Double.isNaN(y1) || Double.isNaN(y2)) {
                throw new IllegalArgumentException("Функция не определена в точке на интервале интегрирования");
            }

            // Площадь трапеции
            double segmentArea = (y1 + y2) * (nextX - currentX) / 2.0;
            integral += segmentArea;

            currentX = nextX;
        }

        return integral;
    }

}