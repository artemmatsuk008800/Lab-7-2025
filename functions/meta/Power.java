package functions.meta;

import functions.Function;

public class Power implements Function {
    private Function baseFunction;
    private double power;

    public Power(Function baseFunction, double power){
        if (baseFunction == null){
            throw new IllegalArgumentException("Базовая функция не может быть null");
        }
        this.baseFunction = baseFunction;
        this.power = power;
    }

    public double getLeftDomainBorder(){
        return baseFunction.getLeftDomainBorder();
    }

    public double getRightDomainBorder(){
        return baseFunction.getRightDomainBorder();
    }

    public double getFunctionValue(double x){
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()){
            return Double.NaN;
        }

        double baseValue = baseFunction.getFunctionValue(x);

        if (Double.isNaN(baseValue)){
            return Double.NaN;
        }

        if (baseValue < 0 && power != (int)power){
            return Double.NaN;
        }

        if (Math.abs(baseValue) < 1e-10 && Math.abs(power) < 1e-10){
            return Double.NaN;
        }

        return Math.pow(baseValue, power);
    }

    public double getPower(){
        return power;
    }
}
