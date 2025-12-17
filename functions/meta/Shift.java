package functions.meta;

import functions.Function;

public class Shift implements Function{
    private Function original;
    private double shiftX;
    private double shiftY;

    public Shift(Function original, double shiftX, double shiftY){
        if (original == null){
            throw new IllegalArgumentException("Исходная функция не может быть null");
        }
        this.original = original;
        this.shiftX = shiftX;
        this.shiftY = shiftY;
    }

    public double getLeftDomainBorder() {
        double left = original.getLeftDomainBorder();
        if (Double.isInfinite(left)) {
            return left;
        }
        return left + shiftX;
    }

    public double getRightDomainBorder() {
        double right = original.getRightDomainBorder();
        if (Double.isInfinite(right)) {
            return right;
        }
        return right + shiftX;
    }

    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }

        double originalX = x - shiftX;

        if (originalX < original.getLeftDomainBorder() || originalX > original.getRightDomainBorder()) {
            return Double.NaN;
        }

        double originalValue = original.getFunctionValue(originalX);

        if (Double.isNaN(originalValue)) {
            return Double.NaN;
        }

        return originalValue + shiftY;
    }

    public double getShiftX() {
        return shiftX;
    }

    public double getShiftY() {
        return shiftY;
    }
}
