package functions.basic;

import functions.Function;

public class Exp implements Function {
    public double getFunctionValue(double x){
        return Math.exp(x);
    }


    public double getLeftDomainBorder(){
        return Double.NEGATIVE_INFINITY;
    }

    public double getRightDomainBorder(){
        return Double.POSITIVE_INFINITY;
    }
}
