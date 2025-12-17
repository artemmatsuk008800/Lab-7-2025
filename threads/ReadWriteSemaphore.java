package threads;

public class ReadWriteSemaphore {
    private int readers = 0;
    private int writers = 0;
    private int writeRequests = 0;

    public synchronized void lockRead() throws InterruptedException {
        while (writers > 0 || writeRequests > 0) {
            wait();
        }
        readers++;
    }

    public synchronized void unlockRead() {
        readers--;
        notifyAll();
    }

    public synchronized void lockWrite() throws InterruptedException {
        writeRequests++;

        while (readers > 0 || writers > 0) {
            wait();
        }

        writeRequests--;
        writers++;
    }

    public synchronized void unlockWrite() {
        writers--;
        notifyAll();
    }

    // Метод для проверки, можно ли читать
    public synchronized boolean canRead() {
        return writers == 0 && writeRequests == 0;
    }

    // Метод для проверки, можно ли писать
    public synchronized boolean canWrite() {
        return readers == 0 && writers == 0;
    }
}
