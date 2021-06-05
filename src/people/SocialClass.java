package people;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import resources.ResourceManager.Resource;
import util.TimeSystem;
import util.Utils;

public enum SocialClass {
    FARMER(Color.RED, new HashSet<>() {{
        add(new PersonalResourceInfos(Resource.BREAD, 5, 60 * TimeSystem.TICK_RATE));
        add(new PersonalResourceInfos(Resource.FISH, 5, 60 * TimeSystem.TICK_RATE));
    }});

    private final Color                      color;
    private final Set<PersonalResourceInfos> personalResourceInfos;

    SocialClass(Color color, Set<PersonalResourceInfos> personalResourceInfos) {
        this.color = color;
        this.personalResourceInfos = personalResourceInfos;
    }

    public Color getColor() {
        return this.color;
    }

    public static int getNbClasses() {
        return values().length;
    }

    public String getName() {
        return Utils.formatText(name());
    }

    public Set<PersonalResourceInfos> getPersonalResourceInfos() {
        return this.personalResourceInfos;
    }

    public static class PersonalResourceInfos {

        protected final Resource resource;
        protected final int      nbResourcesNeeded;
        protected final double   depletionRate;

        public PersonalResourceInfos(Resource resource, int nbResourcesNeeded, int nbTicksForDepletion) {
            this.resource = resource;
            this.nbResourcesNeeded = nbResourcesNeeded;
            this.depletionRate = 1f / nbTicksForDepletion;
        }

        public Resource getResource() {
            return this.resource;
        }

        public int getNbResourcesNeeded() {
            return this.nbResourcesNeeded;
        }

        public double getDepletionRate() {
            return this.depletionRate;
        }
    }
}