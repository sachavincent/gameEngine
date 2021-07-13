package scene.components;

import java.util.Collections;
import java.util.List;
import models.Model;
import util.TimeSystem;

public class ConstructionComponentSingle extends SingleModelComponent {

    protected List<ConstructionTier> constructionTiers;

    protected int currentTier;

    public ConstructionComponentSingle(List<ConstructionTier> constructionTiers) {
        super(constructionTiers.isEmpty() ? null : constructionTiers.get(0).model);

        this.constructionTiers = constructionTiers;
    }

    public boolean isFinishedBuilding() {
        return this.currentTier == this.constructionTiers.size() - 1;
    }

    public void setCurrentTier(int currentTier) {
        this.currentTier = currentTier;
        if (currentTier < this.constructionTiers.size())
            setModel(this.constructionTiers.get(currentTier).model.toModelEntity());
    }

    public ConstructionTier getCurrentConstructionTier() {
        if (this.currentTier < this.constructionTiers.size())
            return this.constructionTiers.get(this.currentTier);
        return null;
    }

    public List<ConstructionTier> getConstructionTiers() {
        return Collections.unmodifiableList(this.constructionTiers);
    }

    public int getCurrentTier() {
        return this.currentTier;
    }

    public static class ConstructionTier {

        private final Model model;
        private final int   numTier;

        private final TimeSystem startTime;
        private       TimeSystem endTime;
        private       double     duration;

        public ConstructionTier(Model model, int numTier, TimeSystem startTime, double duration) {
            this.model = model;
            this.numTier = numTier;
            this.startTime = startTime;
            this.duration = duration;
        }

        public Model getModel() {
            return this.model;
        }

        public int getNumTier() {
            return this.numTier;
        }

        public TimeSystem getStartTime() {
            return this.startTime;
        }

        public TimeSystem getEndTime() {
            return this.endTime;
        }

        public double getDuration() {
            return this.duration;
        }

        public void setEndTime(TimeSystem endTime) {
            this.endTime = endTime;
        }

        public void setDuration(double duration) {
            this.duration = duration;
        }
    }
}