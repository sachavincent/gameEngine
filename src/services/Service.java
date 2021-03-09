package services;

public abstract class Service<ResultType> extends Thread {

    private volatile OnServiceDone<ResultType> onServiceDone;

    protected volatile boolean running;
    private volatile   boolean singleton;

    public Service(boolean singleton, OnServiceDone<ResultType> onServiceDone) {
        this.singleton = singleton;
        this.running = false;

        setOnServiceDone(onServiceDone);
    }

    @Override
    public void run() {
        running = true;

        onServiceDone.done(execute());

        running = false;
    }

    protected abstract ResultType execute();

    public synchronized void setRunning(boolean running) {
        this.running = running;
    }

    public synchronized void setOnServiceDone(OnServiceDone<ResultType> onServiceDone) {
        this.onServiceDone = onServiceDone;
    }

    public synchronized boolean isRunning() {
        return this.running;
    }

    public synchronized void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    public synchronized boolean isSingleton() {
        return this.singleton;
    }
}

