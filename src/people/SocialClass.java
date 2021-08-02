package people;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import resources.ResourceManager.Resource;
import util.TimeSystem;
import util.Utils;

public enum SocialClass {
    FARMER(Color.RED,
            new HashSet<>() {{
                add(new PersonalResourceInfos(Resource.BREAD, 5, 60 * TimeSystem.TICK_RATE));
                add(new PersonalResourceInfos(Resource.FISH, 5, 60 * TimeSystem.TICK_RATE));
            }},
            new HashMap<>() {{
                put(Resource.GOLD, 1.0);
            }});

    private final Color                      color;
    private final Set<PersonalResourceInfos> personalResourceInfos;
    private final Map<Resource, Double>      resourceProduction; // Resources produced in 1 second

    SocialClass(Color color, Set<PersonalResourceInfos> personalResourceInfos,
            Map<Resource, Double> resourceProduction) {
        this.color = color;
        this.personalResourceInfos = personalResourceInfos;
        this.resourceProduction = resourceProduction;
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
        return Collections.unmodifiableSet(this.personalResourceInfos);
    }

    public Map<Resource, Double> getResourceProduction() {
        return Collections.unmodifiableMap(this.resourceProduction);
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