package services;

import java.util.LinkedList;
import java.util.Queue;

public class ServiceManager<S extends Service<?>> {

    protected volatile S currentService;

    protected Queue<S> serviceQueue = new LinkedList<>();

    private Thread thread;

    public void executeAll() {
        thread = new Thread(() -> {
            while (!serviceQueue.isEmpty())
                exec();
        });

        thread.start();
    }

    public void execute() {
        if (currentService != null && currentService.isSingleton() && currentService.isRunning()) {
            currentService.setRunning(false);
            currentService = null;
        }
        S service = serviceQueue.poll();
        if (currentService != null)
            while (currentService.isRunning()) {
            }
        currentService = service;
        if (service != null) {
            service.start();
        }
//        thread = new Thread(this::exec);
//        thread.start();
    }

    private synchronized void exec() {
        if (currentService != null && currentService.isSingleton() && currentService.isRunning()) {
            currentService.setRunning(false);
            currentService = null;
        }

        S service = serviceQueue.poll();
        if (currentService != null)
            while (currentService.isRunning()) {
            }
        currentService = service;
        if (service != null) {
            service.start();
        }
    }

    public void addService(S service) {
        serviceQueue.add(service);
    }

    public void clear() {
        if (currentService != null)
            currentService.setRunning(false);
        currentService = null;
        serviceQueue.clear();
    }
}
