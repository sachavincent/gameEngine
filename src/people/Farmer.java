package people;

import java.util.EnumMap;
import resources.ResourceManager.Resource;

public class Farmer extends Person {

    private final static SocialClass SOCIAL_CLASS = SocialClass.FARMER;

    //TODO: Determine depletion rate
    private final static EnumMap<Resource, Integer> RESOURCES_NEEDED = new EnumMap<>(Resource.class);

    static {
        RESOURCES_NEEDED.put(Resource.BREAD, 5);
        RESOURCES_NEEDED.put(Resource.FISH, 5);
    }

    public Farmer() {
        super(SOCIAL_CLASS, RESOURCES_NEEDED);
    }
}
