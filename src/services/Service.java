package services;

public abstract class Service<ResultType> extends Thread {

    private OnServiceDone<ResultType> onServiceDone;

    protected boolean running = false;

    public Service(OnServiceDone<ResultType> onServiceDone) {
        setOnServiceDone(onServiceDone);
    }

    @Override
    public void run() {
        running = true;

        onServiceDone.done(execute());

        running = false;
    }

    protected abstract ResultType execute();

    public void setOnServiceDone(OnServiceDone<ResultType> onServiceDone) {
        this.onServiceDone = onServiceDone;
    }

    public boolean isRunning() {
        return this.running;
    }
}

