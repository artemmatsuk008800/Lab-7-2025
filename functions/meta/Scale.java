package functions.meta;

import functions.Function;

public class Scale implements Function {
    private Function original;
    private double scaleX;
    private double scaleY;

    public Scale(Function original, double scaleX, double scaleY){
        if (original == null){
            throw new IllegalArgumentException("Исходная функция не может быть null");
        }
        if (Math.abs(scaleX) < 1e-10){
            throw new IllegalArgumentException("Коэффициент масштабирования по X не может быть нулевыым");
        }

        this.original = original;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public double getLeftDomainBorder(){
        double left = original.getLeftDomainBorder();
        if (Double.isInfinite(left)){
            return left;
        }

        if (scaleX > 0){
            return left * scaleX;
        } else {
            double right = original.getRightDomainBorder();
            if (Double.isInfinite(right)){
                return -Double.POSITIVE_INFINITY;
            }
            return right * scaleX;
        }
    }

    public double getRightDomainBorder(){
        double right = original.getRightDomainBorder();
        if (Double.isInfinite(right)){
            return right;
        }

        if (scaleX > 0){
            return right * scaleX;
        } else {
            double left = original.getLeftDomainBorder();
            if (Double.isInfinite(left)){
                return -Double.POSITIVE_INFINITY;
            }
            return left * scaleX;
        }
    }

    public double getFunctionValue(double x){
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()){
            return Double.NaN;
        }

        double originalX = x / scaleX;

        if (originalX < original.getLeftDomainBorder() || originalX > original.getRightDomainBorder()){
            return Double.NaN;
        }

        double originalValue = original.getFunctionValue(originalX);

        if (Double.isNaN(originalValue)){
            return Double.NaN;
        }

        return scaleY * originalValue;
    }

    public double getScaleX(){
        return scaleX;
    }

    public double getScaleY(){
        return scaleY;
    }
}
