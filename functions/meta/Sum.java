package functions.meta;

import functions.Function;

public class Sum implements Function{
    private Function f1;
    private Function f2;

    public Sum(Function f1, Function f2){
        if (f1 == null || f2 == null){
            throw new IllegalArgumentException("Функции не могут быть null");
        }
        this.f1 = f1;
        this.f2 = f2;
    }

    public double getLeftDomainBorder(){
        return Math.max(f1.getLeftDomainBorder(), f2.getLeftDomainBorder());
    }

    public double getRightDomainBorder(){
        return Math.min(f1.getRightDomainBorder(), f2.getRightDomainBorder());
    }

    public double getFunctionValue(double x){
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()){
            return Double.NaN;
        }
        double value1 = f1.getFunctionValue(x);
        double value2 = f2.getFunctionValue(x);

        if (Double.isNaN(value1) || Double.isNaN(value2)){
            return Double.NaN;
        }

        return value1 + value2;
    }
}
