package functions.meta;

import functions.Function;

public class Composition implements Function{
    private Function outer;
    private Function inner;

    public Composition(Function outer, Function inner) {
        if (outer == null || inner == null) {
            throw new IllegalArgumentException("Функции не могут быть null");
        }
        this.outer = outer;
        this.inner = inner;
    }

    public double getLeftDomainBorder() {
        return inner.getLeftDomainBorder();
    }

    public double getRightDomainBorder() {
        return inner.getRightDomainBorder();
    }

    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }

        double innerValue = inner.getFunctionValue(x);

        if (Double.isNaN(innerValue)) {
            return Double.NaN;
        }

        if (innerValue < outer.getLeftDomainBorder() || innerValue > outer.getRightDomainBorder()) {
            return Double.NaN;
        }

        return outer.getFunctionValue(innerValue);
    }
}
