package functions;

import java.util.Iterator;

public interface TabulatedFunction extends Function, Cloneable, Iterable<FunctionPoint> {
    int getPointsCount();
    double getPointX(int index);
    double getPointY(int index);
    void setPointX(int index, double x) throws InappropriateFunctionPointException;
    void setPointY(int index, double y);
    void addPoint(FunctionPoint point) throws InappropriateFunctionPointException;
    void deletePoint(int index);
    FunctionPoint getPoint(int index);
    void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException;
    TabulatedFunction clone();
}