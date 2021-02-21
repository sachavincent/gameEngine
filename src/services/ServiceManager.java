package services;

import java.util.LinkedList;
import java.util.Queue;

public class ServiceManager<S extends Service<?>> {

    protected S currentService;

    protected Queue<S> serviceQueue = new LinkedList<>();

    public void executeAll() {
        while (!serviceQueue.isEmpty())
            execute();
    }

    public void execute() {
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
}
