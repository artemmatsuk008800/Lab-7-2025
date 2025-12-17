package functions;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;

public class ArrayTabulatedFunction implements TabulatedFunction, Externalizable {
    private FunctionPoint[] points;
    private int pointsCount;
    private static final double Epsilon = 1e-10;

    public ArrayTabulatedFunction() {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(pointsCount);
        for (int i = 0; i < pointsCount; i++) {
            out.writeDouble(points[i].getX());
            out.writeDouble(points[i].getY());
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int count = in.readInt();
        points = new FunctionPoint[count + 3];
        pointsCount = count;
        for (int i = 0; i < count; i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            points[i] = new FunctionPoint(x, y);
        }
    }

    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }

        this.points = new FunctionPoint[pointsCount];
        this.pointsCount = pointsCount;
        double step = (rightX - leftX) / (pointsCount - 1);

        for (int i = 0; i < pointsCount; i++){
            double x = leftX + i * step;
            this.points[i] = new FunctionPoint(x, 0.0);
        }
    }

    public ArrayTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }

        // Проверка упорядоченности по X
        for (int i = 1; i < points.length; i++) {
            if (points[i].getX() <= points[i-1].getX() + Epsilon) {
                throw new IllegalArgumentException("Точки должны быть упорядочены по возрастанию X");
            }
        }

        this.pointsCount = points.length;
        this.points = new FunctionPoint[pointsCount];

        // Копируем точки с обеспечением инкапсуляции
        for (int i = 0; i < pointsCount; i++) {
            this.points[i] = new FunctionPoint(points[i]); // Используем конструктор копирования
        }
    }

    public ArrayTabulatedFunction(double leftX, double rightX, double[] values){
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }

        int pointsCount = values.length;
        this.points = new FunctionPoint[pointsCount];
        this.pointsCount = pointsCount;
        double step = (rightX - leftX) / (pointsCount - 1);

        for (int i = 0; i < pointsCount; i++){
            double x = leftX + i * step;
            this.points[i] = new FunctionPoint(x, values[i]);
        }
    }

    public double getLeftDomainBorder() {
        return points[0].getX();
    }

    public double getRightDomainBorder(){
        return points[pointsCount - 1].getX();
    }

    public double getFunctionValue(double x){
        if (x < getLeftDomainBorder() - Epsilon || x > getRightDomainBorder() + Epsilon) {
            return Double.NaN;
        }

        for (int i = 0; i < pointsCount; i++){
            if (Math.abs(points[i].getX() - x) < Epsilon) {
                return points[i].getY(); // Возвращаем соответствующий y
            }
        }

        for (int i = 0; i < pointsCount - 1; i++){
            double x1 = points[i].getX();
            double x2 = points[i + 1].getX();

            if (x >= x1 - Epsilon && x <= x2 + Epsilon){
                double y1 = points[i].getY();
                double y2 = points[i + 1].getY();

                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
        }

        return Double.NaN;
    }

    public int getPointsCount(){
        return pointsCount;
    }

    public FunctionPoint getPoint(int index){
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс выходит за границы: " + index);
        }
        return new FunctionPoint(points[index]);
    }

    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс выходит за границы: " + index);
        }

        double newX = point.getX();
        if (index > 0 && newX <= points[index - 1].getX() + Epsilon) {
            throw new InappropriateFunctionPointException("X точки должен быть больше предыдущей точки");
        }
        if (index < pointsCount - 1 && newX >= points[index + 1].getX() - Epsilon) {
            throw new InappropriateFunctionPointException("X точки должен быть меньше следующей точки");
        }

        points[index] = new FunctionPoint(point);
    }

    public double getPointX(int index){
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс выходит за границы: " + index);
        }
        return points[index].getX();
    }

    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс выходит за границы: " + index);
        }

        if (index > 0 && x <= points[index - 1].getX() + Epsilon) {
            throw new InappropriateFunctionPointException("X точки должен быть больше предыдущей точки");
        }
        if (index < pointsCount - 1 && x >= points[index + 1].getX() - Epsilon) {
            throw new InappropriateFunctionPointException("X точки должен быть меньше следующей точки");
        }

        points[index].setX(x);
    }

    public double getPointY(int index){
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс выходит за границы: " + index);
        }
        return points[index].getY();
    }

    public void setPointY(int index, double y){
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс выходит за границы: " + index);
        }
        points[index].setY(y);
    }

    public void deletePoint(int index){
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс выходит за границы: " + index);
        }
        if (pointsCount <= 2) {
            throw new IllegalStateException("Нельзя удалить точку: должно остаться минимум 2 точки");
        }

        System.arraycopy(points, index + 1, points, index, pointsCount - index - 1);
        pointsCount--;
    }

    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        double newX = point.getX();
        for (int i = 0; i < pointsCount; i++) {
            if (Math.abs(points[i].getX() - newX) < Epsilon) {
                throw new InappropriateFunctionPointException("Точка с таким X уже существует");
            }
        }

        if (pointsCount == points.length) {
            FunctionPoint[] newPoints = new FunctionPoint[points.length * 2];
            System.arraycopy(points, 0, newPoints, 0, pointsCount);
            points = newPoints;
        }

        int insertIndex = 0;
        while (insertIndex < pointsCount && points[insertIndex].getX() < newX - Epsilon) {
            insertIndex++;
        }

        System.arraycopy(points, insertIndex, points, insertIndex + 1, pointsCount - insertIndex);

        points[insertIndex] = new FunctionPoint(point);
        pointsCount++;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < pointsCount; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(points[i].toString());
        }
        sb.append("}");
        return sb.toString();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TabulatedFunction)) return false;

        TabulatedFunction otherFunc = (TabulatedFunction) o;

        // Сначала проверяем количество точек
        if (this.pointsCount != otherFunc.getPointsCount()) {
            return false;
        }

        // Оптимизация: если другой объект тоже ArrayTabulatedFunction
        if (o instanceof ArrayTabulatedFunction) {
            ArrayTabulatedFunction other = (ArrayTabulatedFunction) o;

            // Прямой доступ к массивам для ускорения
            for (int i = 0; i < pointsCount; i++) {
                if (!points[i].equals(other.points[i])) {
                    return false;
                }
            }
        } else {
            // Общий случай для любой TabulatedFunction
            for (int i = 0; i < pointsCount; i++) {
                FunctionPoint thisPoint = this.getPoint(i);
                FunctionPoint otherPoint = otherFunc.getPoint(i);

                if (!thisPoint.equals(otherPoint)) {
                    return false;
                }
            }
        }

        return true;
    }

    public int hashCode() {
        int hash = pointsCount; // Включаем количество точек в хэш

        // Вычисляем XOR всех хэш-кодов точек
        for (int i = 0; i < pointsCount; i++) {
            hash ^= points[i].hashCode();
        }

        return hash;
    }

    public TabulatedFunction clone() {
        try {
            // Создаём копию массива точек (глубокое копирование)
            FunctionPoint[] clonedPoints = new FunctionPoint[points.length];
            for (int i = 0; i < pointsCount; i++) {
                clonedPoints[i] = new FunctionPoint(points[i]); // Конструктор копирования
            }

            // Создаём новый объект с скопированными данными
            ArrayTabulatedFunction cloned = (ArrayTabulatedFunction) super.clone();
            cloned.points = clonedPoints;
            cloned.pointsCount = this.pointsCount;

            return cloned;
        } catch (CloneNotSupportedException e) {
            // Альтернативный способ клонирования через конструктор
            FunctionPoint[] pointsCopy = new FunctionPoint[pointsCount];
            for (int i = 0; i < pointsCount; i++) {
                pointsCopy[i] = new FunctionPoint(points[i]);
            }
            return new ArrayTabulatedFunction(pointsCopy);
        }
    }

    public Iterator<FunctionPoint> iterator() {
        return new Iterator<FunctionPoint>() {
            private int currentIndex = 0;

            public boolean hasNext() {
                return currentIndex < pointsCount;
            }

            public FunctionPoint next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException("No more elements");
                }
                // Возвращаем копию точки для защиты инкапсуляции
                return new FunctionPoint(points[currentIndex++]);
            }

            public void remove() {
                throw new UnsupportedOperationException("Remove operation is not supported");
            }
        };
    }

    public static class ArrayTabulatedFunctionFactory implements TabulatedFunctionFactory {
        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
            return new ArrayTabulatedFunction(leftX, rightX, pointsCount);
        }

        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) {
            return new ArrayTabulatedFunction(leftX, rightX, values);
        }

        public TabulatedFunction createTabulatedFunction(FunctionPoint[] points) {
            return new ArrayTabulatedFunction(points);
        }
    }
}
