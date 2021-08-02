package services;

public abstract class Service<ResultType> extends Thread {

    private volatile OnServiceDone<ResultType> onServiceDone;

    protected volatile ServiceManager<?> serviceManager;

    public Service(OnServiceDone<ResultType> onServiceDone) {
        setOnServiceDone(onServiceDone);
    }

    @Override
    public void run() {
        System.err.println("Starting service n°" + getId());
        ResultType res = execute();
        if (res != null) {
            System.err.println("Service success n°" + getId());
            this.onServiceDone.done(res);
        }
    }

    protected abstract ResultType execute();

    public synchronized void setOnServiceDone(OnServiceDone<ResultType> onServiceDone) {
        this.onServiceDone = onServiceDone;
    }

    public void setServiceManager(ServiceManager<?> serviceManager) {
        this.serviceManager = serviceManager;
    }

    public ServiceManager<?> getServiceManager() {
        return this.serviceManager;
    }
}