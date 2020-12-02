package events;

import guis.prefabs.GuiHouseDetails;
import items.Item;
import items.buildings.houses.HouseItem;
import people.Farmer;

public class ImmigrationEvent extends Event {

    public ImmigrationEvent(Item house, long delay) {
        super(house, delay);
    }

    @Override
    public void run() {
        if (((HouseItem) (this.item)).addPerson(new Farmer())) {
            System.out.println("Immigration de " + this.item.getId() + ", nbPersonnes : " +
                    ((HouseItem) this.item).getNumberOfPeople());
            if (GuiHouseDetails.getHouseDetailsGui() != null &&
                    GuiHouseDetails.getHouseDetailsGui().getHouseItem() != null &&
                    GuiHouseDetails.getHouseDetailsGui().getHouseItem().equals(this.item))
                GuiHouseDetails.getHouseDetailsGui().update();
        }


        next();
    }
}
