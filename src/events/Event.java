package events;

import java.util.TimerTask;

public abstract class Event extends TimerTask {

//    protected final long delay;
//    protected final Item item;
//
//    public Event(Item item, long delay) {
//        this.delay = delay;
//        this.item = item;
//
//        Timer.scheduleEvent(this);
//    }
//
//    public long getDelay() {
//        return this.delay;
//    }
//
//    public abstract void run();
//
//    public void next() {
//        Timer.removeMisc(this);
//        Constructor<? extends Event> constructor;
//        try {
//            constructor = getClass().getDeclaredConstructor(Item.class, long.class);
//            constructor.newInstance(item, delay);
//        } catch (ReflectiveOperationException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public boolean cancel() {
//        // Timer.removeMisc(this);
//
//        return super.cancel();
//    }
}
