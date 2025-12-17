package functions;

import java.io.Serializable;

public class FunctionPoint implements Serializable, Cloneable{
    private double x;
    private double y;

    public FunctionPoint(double x, double y){ // Конструктор с заданными координатами
        this.x = x;
        this.y = y;
    }

    public FunctionPoint(FunctionPoint point){ // Конструктор копирования
        this.x = point.x;
        this.y = point.y;
    }

    public FunctionPoint(){ // Конструктор по умолчанию в 0,0
        this(0.0, 0.0);
    }
    // Геттеры для доступа к приватным полям
    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }
    // Сеттеры для изменения приватных полей
    public void setX(double x){
        this.x = x;
    }

    public void setY(double y){
        this.y = y;
    }

    public String toString() {
        return "(" + x + "; " + y + ")";
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FunctionPoint that = (FunctionPoint) o;

        // Сравнение чисел с плавающей точкой с учётом погрешности
        final double EPSILON = 1e-10;
        return Math.abs(this.x - that.x) < EPSILON &&
                Math.abs(this.y - that.y) < EPSILON;
    }

    public int hashCode() {
        // Преобразуем double в long, затем разбиваем на два int
        long xBits = Double.doubleToLongBits(x);
        long yBits = Double.doubleToLongBits(y);

        int x1 = (int)(xBits & 0xFFFFFFFFL); // Младшие 4 байта x
        int x2 = (int)(xBits >>> 32);        // Старшие 4 байта x
        int y1 = (int)(yBits & 0xFFFFFFFFL); // Младшие 4 байта y
        int y2 = (int)(yBits >>> 32);        // Старшие 4 байта y

        // Вычисляем хэш-код с помощью XOR
        return x1 ^ x2 ^ y1 ^ y2;
    }

    public FunctionPoint clone() {
        try {
            // Создаём новый объект с теми же координатами
            FunctionPoint cloned = (FunctionPoint) super.clone();
            cloned.x = this.x;
            cloned.y = this.y;
            return cloned;
        } catch (CloneNotSupportedException e) {
            // В случае ошибки возвращаем копию через конструктор
            return new FunctionPoint(this);
        }
    }
}
