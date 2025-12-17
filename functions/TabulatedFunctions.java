package functions;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class TabulatedFunctions {
    private static TabulatedFunctionFactory factory = new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory();

    private TabulatedFunctions() {
        throw new AssertionError("Нельзя создавать экземпляры класса TabulatedFunctions");
    }

    // Метод для установки фабрики
    public static void setTabulatedFunctionFactory(TabulatedFunctionFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("Фабрика не может быть null");
        }
        TabulatedFunctions.factory = factory;
    }

    // ========== Методы с использованием фабрики ==========

    public static TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
        return factory.createTabulatedFunction(leftX, rightX, pointsCount);
    }

    public static TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) {
        return factory.createTabulatedFunction(leftX, rightX, values);
    }

    public static TabulatedFunction createTabulatedFunction(FunctionPoint[] points) {
        return factory.createTabulatedFunction(points);
    }

    // ========== Методы с использованием рефлексии ==========

    public static TabulatedFunction createTabulatedFunction(Class<? extends TabulatedFunction> functionClass,
                                                            double leftX, double rightX, int pointsCount) {
        try {
            // Получаем конструктор с тремя параметрами: double, double, int
            Constructor<? extends TabulatedFunction> constructor =
                    functionClass.getConstructor(double.class, double.class, int.class);

            // Создаем объект с помощью конструктора
            return constructor.newInstance(leftX, rightX, pointsCount);

        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Класс " + functionClass.getName() +
                    " не имеет требуемого конструктора", e);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Не удалось создать объект класса " +
                    functionClass.getName(), e);
        }
    }

    public static TabulatedFunction createTabulatedFunction(Class<? extends TabulatedFunction> functionClass,
                                                            double leftX, double rightX, double[] values) {
        try {
            // Получаем конструктор с тремя параметрами: double, double, double[]
            Constructor<? extends TabulatedFunction> constructor =
                    functionClass.getConstructor(double.class, double.class, double[].class);

            // Создаем объект с помощью конструктора
            return constructor.newInstance(leftX, rightX, values);

        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Класс " + functionClass.getName() +
                    " не имеет требуемого конструктора", e);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Не удалось создать объект класса " +
                    functionClass.getName(), e);
        }
    }

    public static TabulatedFunction createTabulatedFunction(Class<? extends TabulatedFunction> functionClass,
                                                            FunctionPoint[] points) {
        try {
            // Получаем конструктор с одним параметром: FunctionPoint[]
            Constructor<? extends TabulatedFunction> constructor =
                    functionClass.getConstructor(FunctionPoint[].class);

            // Создаем объект с помощью конструктора
            return constructor.newInstance((Object)points);

        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Класс " + functionClass.getName() +
                    " не имеет требуемого конструктора", e);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Не удалось создать объект класса " +
                    functionClass.getName(), e);
        }
    }

    // ========== Основной метод tabulate ==========

    public static TabulatedFunction tabulate(Function function, double leftX, double rightX, int pointsCount) {
        return tabulate(function, leftX, rightX, pointsCount, factory);
    }

    // Метод tabulate с фабрикой
    public static TabulatedFunction tabulate(Function function, double leftX, double rightX,
                                             int pointsCount, TabulatedFunctionFactory customFactory) {
        if (function == null) {
            throw new IllegalArgumentException("Функция не может быть null");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }

        if (leftX < function.getLeftDomainBorder() || rightX > function.getRightDomainBorder()) {
            throw new IllegalArgumentException("Границы табулирования выходят за область определения функции");
        }

        double[] values = new double[pointsCount];
        double step = (rightX - leftX) / (pointsCount - 1);

        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            double y = function.getFunctionValue(x);

            if (Double.isNaN(y)) {
                throw new IllegalArgumentException(String.format("Функция не определена в точке x = %.2f", x));
            }

            values[i] = y;
        }

        return customFactory.createTabulatedFunction(leftX, rightX, values);
    }

    // ========== НОВЫЙ МЕТОД: tabulate с рефлексией ==========
    public static TabulatedFunction tabulate(Function function, double leftX, double rightX,
                                             int pointsCount, Class<? extends TabulatedFunction> functionClass) {
        if (function == null) {
            throw new IllegalArgumentException("Функция не может быть null");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }

        if (leftX < function.getLeftDomainBorder() || rightX > function.getRightDomainBorder()) {
            throw new IllegalArgumentException("Границы табулирования выходят за область определения функции");
        }

        double[] values = new double[pointsCount];
        double step = (rightX - leftX) / (pointsCount - 1);

        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            double y = function.getFunctionValue(x);

            if (Double.isNaN(y)) {
                throw new IllegalArgumentException(String.format("Функция не определена в точке x = %.2f", x));
            }

            values[i] = y;
        }

        // Используем рефлексивный метод создания
        return createTabulatedFunction(functionClass, leftX, rightX, values);
    }

    // ========== Существующие методы остаются без изменений ==========

    public static void outputTabulatedFunction(TabulatedFunction function, OutputStream out) throws IOException {
        if (function == null || out == null) {
            throw new IllegalArgumentException();
        }

        try (DataOutputStream dataOut = new DataOutputStream(out)) {
            int pointsCount = function.getPointsCount();
            dataOut.writeInt(pointsCount);

            for (int i = 0; i < pointsCount; i++) {
                dataOut.writeDouble(function.getPointX(i));
                dataOut.writeDouble(function.getPointY(i));
            }

            dataOut.flush();
        }
    }

    public static TabulatedFunction inputTabulatedFunction(InputStream in) throws IOException {
        if (in == null) {
            throw new IllegalArgumentException();
        }

        try (DataInputStream dataIn = new DataInputStream(in)) {
            int pointsCount = dataIn.readInt();

            if (pointsCount < 2) {
                throw new IOException("Недостаточное количество точек");
            }

            FunctionPoint[] points = new FunctionPoint[pointsCount];

            for (int i = 0; i < pointsCount; i++) {
                double x = dataIn.readDouble();
                double y = dataIn.readDouble();
                points[i] = new FunctionPoint(x, y);
            }

            // Используем фабрику
            return factory.createTabulatedFunction(points);
        }
    }

    // Перегруженная версия inputTabulatedFunction с рефлексией
    public static TabulatedFunction inputTabulatedFunction(InputStream in,
                                                           Class<? extends TabulatedFunction> functionClass)
            throws IOException {
        if (in == null) {
            throw new IllegalArgumentException();
        }

        try (DataInputStream dataIn = new DataInputStream(in)) {
            int pointsCount = dataIn.readInt();

            if (pointsCount < 2) {
                throw new IOException("Недостаточное количество точек");
            }

            FunctionPoint[] points = new FunctionPoint[pointsCount];

            for (int i = 0; i < pointsCount; i++) {
                double x = dataIn.readDouble();
                double y = dataIn.readDouble();
                points[i] = new FunctionPoint(x, y);
            }

            // Используем рефлексивный метод создания
            return createTabulatedFunction(functionClass, points);
        }
    }

    public static void writeTabulatedFunction(TabulatedFunction function, Writer out) throws IOException {
        if (function == null || out == null) {
            throw new IllegalArgumentException();
        }

        try (BufferedWriter writer = new BufferedWriter(out)) {
            int pointsCount = function.getPointsCount();
            writer.write(String.valueOf(pointsCount));
            writer.newLine();

            for (int i = 0; i < pointsCount; i++) {
                writer.write(function.getPointX(i) + " " + function.getPointY(i));
                writer.newLine();
            }

            writer.flush();
        }
    }

    public static TabulatedFunction readTabulatedFunction(Reader in) throws IOException {
        if (in == null) {
            throw new IllegalArgumentException();
        }

        StreamTokenizer tokenizer = new StreamTokenizer(in);

        if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
            throw new IOException("Ожидалось число (количество точек)");
        }

        int pointsCount = (int) tokenizer.nval;
        if (pointsCount < 2) {
            throw new IOException("Недостаточное количество точек");
        }

        FunctionPoint[] points = new FunctionPoint[pointsCount];

        for (int i = 0; i < pointsCount; i++) {
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new IOException("Ожидалась координата X");
            }
            double x = tokenizer.nval;

            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new IOException("Ожидалась координата Y");
            }
            double y = tokenizer.nval;

            points[i] = new FunctionPoint(x, y);
        }

        // Используем фабрику
        return factory.createTabulatedFunction(points);
    }

    // Перегруженная версия readTabulatedFunction с рефлексией
    public static TabulatedFunction readTabulatedFunction(Reader in,
                                                          Class<? extends TabulatedFunction> functionClass)
            throws IOException {
        if (in == null) {
            throw new IllegalArgumentException();
        }

        StreamTokenizer tokenizer = new StreamTokenizer(in);

        if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
            throw new IOException("Ожидалось число (количество точек)");
        }

        int pointsCount = (int) tokenizer.nval;
        if (pointsCount < 2) {
            throw new IOException("Недостаточное количество точек");
        }

        FunctionPoint[] points = new FunctionPoint[pointsCount];

        for (int i = 0; i < pointsCount; i++) {
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new IOException("Ожидалась координата X");
            }
            double x = tokenizer.nval;

            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new IOException("Ожидалась координата Y");
            }
            double y = tokenizer.nval;

            points[i] = new FunctionPoint(x, y);
        }

        // Используем рефлексивный метод создания
        return createTabulatedFunction(functionClass, points);
    }
}