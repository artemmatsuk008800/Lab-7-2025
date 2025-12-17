import functions.*;
import functions.basic.*;

public class Main {
    public static void main(String[] args) {
        // ЗАДАНИЕ 1
        System.out.println("Итераторы:");
        FunctionPoint[] points = {
                new FunctionPoint(0, 1),
                new FunctionPoint(1, 2),
                new FunctionPoint(2, 3),
                new FunctionPoint(3, 4)
        };

        TabulatedFunction f = new ArrayTabulatedFunction(0, 15, 15);
        for (FunctionPoint p : f) {
            System.out.println(p);
        }

        // ЗАДАНИЕ 2
        System.out.println("\nФабрика:");
        Function cos = new Cos();
        TabulatedFunction tf;

        tf = TabulatedFunctions.tabulate(cos, 0, Math.PI, 11);
        System.out.println(tf.getClass());

        TabulatedFunctions.setTabulatedFunctionFactory(
                new LinkedListTabulatedFunction.LinkedListTabulatedFunctionFactory());
        tf = TabulatedFunctions.tabulate(cos, 0, Math.PI, 11);
        System.out.println(tf.getClass());

        TabulatedFunctions.setTabulatedFunctionFactory(
                new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory());
        tf = TabulatedFunctions.tabulate(cos, 0, Math.PI, 11);
        System.out.println(tf.getClass());

        // ЗААДАНИЕ 3
        System.out.println("\nРефлексия:");

        // тест 1: создание ArrayTabulatedFunction через рефлексию
        f = TabulatedFunctions.createTabulatedFunction(
                ArrayTabulatedFunction.class, 0, 10, 3);
        System.out.println(f.getClass());
        System.out.println(f);

        // тест 2: создание ArrayTabulatedFunction с массивом значений
        f = TabulatedFunctions.createTabulatedFunction(
                ArrayTabulatedFunction.class, 0, 10, new double[] {0, 10});
        System.out.println(f.getClass());
        System.out.println(f);

        // тест 3: создание LinkedListTabulatedFunction из массива точек
        f = TabulatedFunctions.createTabulatedFunction(
                LinkedListTabulatedFunction.class,
                new FunctionPoint[] {
                        new FunctionPoint(0, 0),
                        new FunctionPoint(10, 10)
                }
        );
        System.out.println(f.getClass());
        System.out.println(f);

        // тест 4: табулирование через рефлексию
        f = TabulatedFunctions.tabulate(new Sin(), 0, Math.PI, 11, LinkedListTabulatedFunction.class);
        System.out.println(f.getClass());
        System.out.println(f);
    }
}