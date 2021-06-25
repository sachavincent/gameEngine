package scene.components.requirements;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import scene.components.Component;

public class RequirementComponent extends Component {

    private Map<Requirement<?, ?>, Boolean> placingRequirements; // Requirement to place

    private Map<Requirement<?, ?>, Boolean> tier1Requirements; // Moving in and moving out
    private Map<Requirement<?, ?>, Boolean> tier2Requirements; // Only moving in
    private Map<Requirement<?, ?>, Boolean> tier3Requirements; // Leveling up

    public RequirementComponent(Set<Requirement<?, ?>> placingRequirements,
            Set<Requirement<?, ?>> tier1Requirements,
            Set<Requirement<?, ?>> tier2Requirements,
            Set<Requirement<?, ?>> tier3Requirements) {
        setPlacingRequirements(placingRequirements);
        setTier1Requirements(tier1Requirements);
        setTier2Requirements(tier2Requirements);
        setTier3Requirements(tier3Requirements);
    }

    public void setPlacingRequirements(Set<Requirement<?, ?>> placingRequirements) {
        this.placingRequirements = placingRequirements.stream().collect(Collectors.toMap(r -> r, r -> false));
    }

    public void setTier1Requirements(Set<Requirement<?, ?>> tier1Requirements) {
        this.tier1Requirements = tier1Requirements.stream().collect(Collectors.toMap(r -> r, r -> false));
    }

    public void setTier2Requirements(Set<Requirement<?, ?>> tier2Requirements) {
        this.tier2Requirements = tier2Requirements.stream().collect(Collectors.toMap(r -> r, r -> false));
    }

    public void setTier3Requirements(Set<Requirement<?, ?>> tier3Requirements) {
        this.tier3Requirements = tier3Requirements.stream().collect(Collectors.toMap(r -> r, r -> false));
    }

    public Map<Requirement<?, ?>, Boolean> getTier1Requirements() {
        return Collections.unmodifiableMap(this.tier1Requirements);
    }

    public Map<Requirement<?, ?>, Boolean> getTier2Requirements() {
        return Collections.unmodifiableMap(this.tier2Requirements);
    }

    public Map<Requirement<?, ?>, Boolean> getTier3Requirements() {
        return Collections.unmodifiableMap(this.tier3Requirements);
    }

    public Map<Requirement<?, ?>, Boolean> getPlacingRequirements() {
        return this.placingRequirements;
    }

    public boolean arePlacingRequirementsMet() {
        if (this.placingRequirements.isEmpty())
            return true;
        return this.placingRequirements.entrySet().stream().allMatch(Entry::getValue);
    }

    public boolean areTier1RequirementsMet() {
        if (this.tier1Requirements.isEmpty())
            return true;
        return this.tier1Requirements.entrySet().stream().allMatch(Entry::getValue);
    }

    public boolean areTier2RequirementsMet() {
        if (this.tier2Requirements.isEmpty())
            return true;
        return this.tier2Requirements.entrySet().stream().allMatch(Entry::getValue);
    }

    public boolean areTier3RequirementsMet() {
        if (this.tier3Requirements.isEmpty())
            return true;
        return this.tier3Requirements.entrySet().stream().allMatch(Entry::getValue);
    }

    public Map<? extends Requirement<?, ?>, Boolean> getAllRequirements() {
        Stream<Entry<? extends Requirement<?, ?>, Boolean>> concat = Stream
                .concat(this.tier1Requirements.entrySet().stream(), this.tier2Requirements.entrySet().stream());
        concat = Stream.concat(concat, this.placingRequirements.entrySet().stream());
        return Stream.concat(concat, this.tier3Requirements.entrySet().stream())
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    public <C> Set<C> getRequirementsOfType(Class<C> clazz) {
        return getAllRequirements().keySet().stream().filter(clazz::isInstance)
                .map(clazz::cast).collect(Collectors.toSet());
    }

    public void meetRequirement(Requirement<?, ?> requirement) {
        this.placingRequirements.entrySet().stream().filter(entry -> entry.getKey().equals(requirement))
                .forEach(entry -> entry.setValue(true));
        this.tier1Requirements.entrySet().stream().filter(entry -> entry.getKey().equals(requirement))
                .forEach(entry -> entry.setValue(true));
        this.tier2Requirements.entrySet().stream().filter(entry -> entry.getKey().equals(requirement))
                .forEach(entry -> entry.setValue(true));
        this.tier3Requirements.entrySet().stream().filter(entry -> entry.getKey().equals(requirement))
                .forEach(entry -> entry.setValue(true));

        requirement.meet();
    }

    public void clearRequirement(Requirement<?, ?> requirement) {
        getAllRequirements().entrySet().stream().filter(entry -> entry.getKey().equals(requirement))
                .forEach(entry -> entry.setValue(false));

        requirement.clear();
    }
}
