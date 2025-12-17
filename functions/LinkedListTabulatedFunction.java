package functions;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Iterator;

public class LinkedListTabulatedFunction implements TabulatedFunction, Serializable {
    private FunctionNode head;
    private int pointcount;
    private static final double EPSILON = 1e-10;

    private static class FunctionNode implements Serializable {
        private FunctionPoint point;
        private FunctionNode prev;
        private FunctionNode next;

        public FunctionNode(FunctionPoint point) {
            this.point = point;
            this.prev = null;
            this.next = null;
        }

        public FunctionPoint getPoint() {
            return point;
        }

        public void setPoint(FunctionPoint point) {
            this.point = point;
        }

        public FunctionNode getPrev() {
            return prev;
        }

        public void setPrev(FunctionNode prev) {
            this.prev = prev;
        }

        public FunctionNode getNext() {
            return next;
        }

        public void setNext(FunctionNode next) {
            this.next = next;
        }
    }

    public LinkedListTabulatedFunction() {
        this.head = new FunctionNode(null); // Создаем голову
        head.setPrev(head);
        head.setNext(head);
        this.pointcount = 0; // Начинаем с нуля точек
    }

    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        this(); // Вызов конструктора по умолчанию

        if (leftX >= rightX - EPSILON) { // Используем EPSILON вместо числового значения
            throw new IllegalArgumentException("Некорректные границы области определения");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Недостаточное количество точек");
        }

        double interval = rightX - leftX;
        double step = interval / (pointsCount - 1);

        for (int i = 0; i < pointsCount; i++) {
            double currentX = leftX + i * step;
            addNodeToTail(new FunctionPoint(currentX, 0));
        }
    }

    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        this(); // Вызов конструктора по умолчанию

        if (leftX >= rightX - EPSILON) { // Используем EPSILON вместо числового значения
            throw new IllegalArgumentException("Некорректные границы области определения");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("Недостаточное количество точек");
        }

        double interval = rightX - leftX;
        double step = interval / (values.length - 1);

        for (int i = 0; i < values.length; i++) {
            double currentX = leftX + i * step;
            addNodeToTail(new FunctionPoint(currentX, values[i]));
        }
    }

    // Конструктор, получающий массив точек
    public LinkedListTabulatedFunction(FunctionPoint[] points) {
        this(); // Вызов конструктора по умолчанию для инициализации головы

        if (points.length < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }

        // Проверка упорядоченности точек по X
        for (int i = 0; i < points.length - 1; i++) {
            if (points[i].getX() >= points[i + 1].getX() - EPSILON) { // Используем EPSILON
                throw new IllegalArgumentException("Точки должны быть упорядочены по возрастанию X");
            }
        }

        // Добавляем точки в список, создавая копии для инкапсуляции
        for (FunctionPoint point : points) {
            addNodeToTail(new FunctionPoint(point));
        }
    }

    private FunctionNode getNodeByIndex(int index) {
        if (index < 0 || index >= pointcount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс выходит за границы");
        }

        FunctionNode current;
        if (index < pointcount / 2) {
            current = head.getNext();
            for (int i = 0; i < index; i++) {
                current = current.getNext();
            }
        } else {
            current = head.getPrev();
            for (int i = pointcount - 1; i > index; i--) {
                current = current.getPrev();
            }
        }
        return current;
    }

    private FunctionNode addNodeToTail(FunctionPoint point) {
        FunctionNode newNode = new FunctionNode(point); // Создаем новый узел
        FunctionNode tail = head.getPrev(); // Получаем хвост списка

        newNode.setPrev(tail);
        newNode.setNext(head);
        tail.setNext(newNode);
        head.setPrev(newNode);

        pointcount++;
        return newNode; // Возвращаем новый узел
    }

    private FunctionNode addNodeByIndex(int index, FunctionPoint point) {
        if (index < 0 || index > pointcount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс выходит за границы");
        }

        if (index == pointcount) {
            return addNodeToTail(point); // Добавляем в конец
        }

        FunctionNode currentNode = getNodeByIndex(index); // Получаем текущий узел
        FunctionNode newNode = new FunctionNode(point); // Создаем новый узел
        FunctionNode prevNode = currentNode.getPrev(); // Получаем предыдущий узел

        newNode.setPrev(prevNode);
        newNode.setNext(currentNode);
        prevNode.setNext(newNode);
        currentNode.setPrev(newNode);

        pointcount++;
        return newNode; // Возвращаем новый узел
    }

    private FunctionNode deleteNodeByIndex(int index) {
        if (index < 0 || index >= pointcount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс выходит за границы");
        }

        FunctionNode nodeToDelete = getNodeByIndex(index); // Узел для удаления
        FunctionNode prevNode = nodeToDelete.getPrev(); // Предыдущий узел
        FunctionNode nextNode = nodeToDelete.getNext(); // Следующий узел

        prevNode.setNext(nextNode);
        nextNode.setPrev(prevNode);

        pointcount--;
        return nodeToDelete; // Возвращаем удаленный узел
    }

    public double getLeftDomainBorder() {
        if (pointcount == 0) return Double.NaN;
        return head.getNext().getPoint().getX(); // x первой точки
    }

    public double getRightDomainBorder() {
        if (pointcount == 0) return Double.NaN;
        return head.getPrev().getPoint().getX(); // x последней точки
    }

    public double getFunctionValue(double x) {
        if (pointcount == 0) {
            return Double.NaN;
        }

        double leftBorder = getLeftDomainBorder();
        double rightBorder = getRightDomainBorder();

        if (x < leftBorder - EPSILON || x > rightBorder + EPSILON) { // Используем EPSILON
            return Double.NaN;
        }

        // Ищем точку с точно таким же x
        FunctionNode current = head.getNext();
        while (current != head) {
            if (Math.abs(current.getPoint().getX() - x) < EPSILON) { // Используем EPSILON вместо 1e-10
                return current.getPoint().getY();
            }
            current = current.getNext();
        }

        // Совпадения нет - ищем интервал для интерполяции
        current = head.getNext();
        while (current != head && current.getNext() != head) {
            double x1 = current.getPoint().getX();
            double x2 = current.getNext().getPoint().getX();

            if (x >= x1 - EPSILON && x <= x2 + EPSILON) { // Используем EPSILON
                double y1 = current.getPoint().getY();
                double y2 = current.getNext().getPoint().getY();
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
            current = current.getNext();
        }

        return Double.NaN;
    }

    public int getPointsCount() {
        return pointcount; // Возвращаем количество точек
    }

    public FunctionPoint getPoint(int index) {
        if (index < 0 || index >= pointcount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс выходит за границы");
        }
        // Возвращаем копию чтобы защитить исходные данные
        FunctionPoint original = getNodeByIndex(index).getPoint();
        return new FunctionPoint(original.getX(), original.getY());
    }

    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        if (index < 0 || index >= pointcount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс выходит за границы");
        }

        double newX = point.getX();
        if (index > 0 && newX <= getNodeByIndex(index - 1).getPoint().getX() + EPSILON) { // Используем EPSILON
            throw new InappropriateFunctionPointException("Нарушение порядка точек");
        }
        if (index < pointcount - 1 && newX >= getNodeByIndex(index + 1).getPoint().getX() - EPSILON) { // Используем EPSILON
            throw new InappropriateFunctionPointException("Нарушение порядка точек");
        }

        getNodeByIndex(index).setPoint(new FunctionPoint(point));
    }

    public double getPointX(int index) {
        if (index < 0 || index >= pointcount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс выходит за границы");
        }
        return getNodeByIndex(index).getPoint().getX(); // Возвращаем x точки
    }

    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        FunctionPoint currentPoint = getNodeByIndex(index).getPoint();
        FunctionPoint newPoint = new FunctionPoint(x, currentPoint.getY());
        setPoint(index, newPoint);
    }

    public double getPointY(int index) {
        if (index < 0 || index >= pointcount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс выходит за границы");
        }
        return getNodeByIndex(index).getPoint().getY(); // Возвращаем y точки
    }

    public void setPointY(int index, double y) {
        if (index < 0 || index >= pointcount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс выходит за границы");
        }
        FunctionPoint currentPoint = getNodeByIndex(index).getPoint();
        FunctionPoint newPoint = new FunctionPoint(currentPoint.getX(), y);
        getNodeByIndex(index).setPoint(newPoint);
    }

    public void deletePoint(int index) {
        if (index < 0 || index >= pointcount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс выходит за границы");
        }
        if (pointcount < 3) {
            throw new IllegalStateException("Невозможно удалить точку");
        }
        deleteNodeByIndex(index);
    }

    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        FunctionNode current = head.getNext();
        int insertIndex = 0;

        while (current != head && current.getPoint().getX() < point.getX() - EPSILON) { // Используем EPSILON
            insertIndex++;
            current = current.getNext();
        }

        if (current != head && Math.abs(current.getPoint().getX() - point.getX()) < EPSILON) { // Используем EPSILON вместо 1e-10
            throw new InappropriateFunctionPointException("Точка с таким x уже существует");
        }

        addNodeByIndex(insertIndex, new FunctionPoint(point));
    }

    public String toString() {
        if (pointcount == 0) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{");

        FunctionNode current = head.getNext();
        boolean first = true;

        while (current != head) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(current.getPoint().toString());
            current = current.getNext();
            first = false;
        }

        sb.append("}");
        return sb.toString();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TabulatedFunction)) return false;

        TabulatedFunction otherFunc = (TabulatedFunction) o;

        // Сначала проверяем количество точек
        if (this.pointcount != otherFunc.getPointsCount()) {
            return false;
        }

        // Оптимизация: если другой объект тоже LinkedListTabulatedFunction
        if (o instanceof LinkedListTabulatedFunction) {
            LinkedListTabulatedFunction other = (LinkedListTabulatedFunction) o;

            // Прямой обход обоих списков для ускорения
            FunctionNode currentThis = this.head.getNext();
            FunctionNode currentOther = other.head.getNext();

            while (currentThis != this.head && currentOther != other.head) {
                if (!currentThis.getPoint().equals(currentOther.getPoint())) {
                    return false;
                }
                currentThis = currentThis.getNext();
                currentOther = currentOther.getNext();
            }

            // Проверяем, что оба списка закончились одновременно
            return currentThis == this.head && currentOther == other.head;
        } else {
            // Общий случай для любой TabulatedFunction
            for (int i = 0; i < pointcount; i++) {
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
        int hash = pointcount; // Включаем количество точек в хэш

        // Вычисляем XOR всех хэш-кодов точек
        FunctionNode current = head.getNext();
        while (current != head) {
            hash ^= current.getPoint().hashCode();
            current = current.getNext();
        }

        return hash;
    }

    public TabulatedFunction clone() {
        try {
            // Создаём новый объект без использования методов добавления
            LinkedListTabulatedFunction cloned = new LinkedListTabulatedFunction();

            if (pointcount == 0) {
                return cloned;
            }

            // Создаём новый список узлов "вручную"
            FunctionNode current = head.getNext();
            FunctionNode[] nodes = new FunctionNode[pointcount];

            // Создаём узлы и копируем точки
            for (int i = 0; i < pointcount; i++) {
                // Создаём копию точки
                FunctionPoint pointCopy = new FunctionPoint(current.getPoint());
                nodes[i] = new FunctionNode(pointCopy);
                current = current.getNext();
            }

            // Связываем узлы между собой
            for (int i = 0; i < pointcount; i++) {
                if (i > 0) {
                    nodes[i].setPrev(nodes[i - 1]);
                }
                if (i < pointcount - 1) {
                    nodes[i].setNext(nodes[i + 1]);
                }
            }

            // Связываем первый и последний узлы с головой
            if (pointcount > 0) {
                cloned.head.setNext(nodes[0]);
                cloned.head.setPrev(nodes[pointcount - 1]);
                nodes[0].setPrev(cloned.head);
                nodes[pointcount - 1].setNext(cloned.head);

                // Связываем остальные узлы
                for (int i = 1; i < pointcount - 1; i++) {
                    nodes[i].setPrev(nodes[i - 1]);
                    nodes[i].setNext(nodes[i + 1]);
                }
            }

            cloned.pointcount = this.pointcount;
            return cloned;

        } catch (Exception e) {
            // Альтернативный способ клонирования через массив точек
            FunctionPoint[] pointsArray = new FunctionPoint[pointcount];
            FunctionNode current = head.getNext();

            for (int i = 0; i < pointcount; i++) {
                pointsArray[i] = new FunctionPoint(current.getPoint());
                current = current.getNext();
            }

            return new LinkedListTabulatedFunction(pointsArray);
        }
    }

    public Iterator<FunctionPoint> iterator() {
        return new Iterator<FunctionPoint>() {
            private FunctionNode currentNode = head.getNext();
            private FunctionNode lastReturned = null;

            public boolean hasNext() {
                return currentNode != head;
            }

            public FunctionPoint next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException("No more elements");
                }
                lastReturned = currentNode;
                FunctionPoint point = currentNode.getPoint();
                currentNode = currentNode.getNext();
                // Возвращаем копию точки для защиты инкапсуляции
                return new FunctionPoint(point);
            }

            public void remove() {
                throw new UnsupportedOperationException("Remove operation is not supported");
            }
        };
    }

    public static class LinkedListTabulatedFunctionFactory implements TabulatedFunctionFactory {
        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
            return new LinkedListTabulatedFunction(leftX, rightX, pointsCount);
        }

        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) {
            return new LinkedListTabulatedFunction(leftX, rightX, values);
        }

        public TabulatedFunction createTabulatedFunction(FunctionPoint[] points) {
            return new LinkedListTabulatedFunction(points);
        }
    }
}