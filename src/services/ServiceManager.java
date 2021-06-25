package services;

import java.util.LinkedList;
import java.util.Queue;

public class ServiceManager<S extends Service<?>> {

    protected volatile S currentService;

    protected Queue<S> serviceQueue = new LinkedList<>();

    protected volatile boolean serviceRunning;

    public synchronized void execute() {
        Thread thread = new Thread(() -> {
            if (this.currentService != null && this.serviceRunning) {
                this.serviceRunning = false;
                while (this.currentService.isAlive()) {

                }
                this.currentService = null;
            }
            S service = serviceQueue.poll();
            if (this.currentService != null)
                while (this.serviceRunning) {
                }
            this.currentService = service;
            if (service != null) {
                service.setServiceManager(this);
                this.serviceRunning = true;
                service.setPriority(Thread.MAX_PRIORITY);
                service.start();
            }
        });
        thread.start();
    }

    public S getCurrentService() {
        return this.currentService;
    }

    public synchronized boolean isServiceRunning() {
        return this.serviceRunning;
    }

    public synchronized void setServiceRunning(boolean serviceRunning) {
        this.serviceRunning = serviceRunning;
    }

    public void addService(S service) {
        this.serviceQueue.add(service);
    }

    public void clear() {
        if (this.currentService != null)
            this.serviceRunning = false;
        this.currentService = null;
        this.serviceQueue.clear();
    }
}