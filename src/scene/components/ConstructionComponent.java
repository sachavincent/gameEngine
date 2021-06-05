package scene.components;

import java.util.Collections;
import java.util.List;
import models.TexturedModel;
import util.TimeSystem;

public class ConstructionComponent extends TextureComponent {

    protected List<ConstructionTier> constructionTiers;

    protected int currentTier;

    public ConstructionComponent(List<ConstructionTier> constructionTiers) {
        super(constructionTiers.isEmpty() ? null : constructionTiers.get(0).model);

        this.constructionTiers = constructionTiers;
    }

    public boolean isFinishedBuilding() {
        return this.currentTier == this.constructionTiers.size() - 1;
    }

    public void setCurrentTier(int currentTier) {
        this.currentTier = currentTier;
        if (currentTier < this.constructionTiers.size())
            this.texture = this.constructionTiers.get(currentTier).model;
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

        private final TexturedModel model;
        private final int           numTier;

        private final TimeSystem startTime;
        private       TimeSystem endTime;
        private       double     duration;

        public ConstructionTier(TexturedModel model, int numTier, TimeSystem startTime, double duration) {
            this.model = model;
            this.numTier = numTier;
            this.startTime = startTime;
            this.duration = duration;
        }

        public TexturedModel getModel() {
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