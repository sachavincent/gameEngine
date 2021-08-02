package util.parsing.colladaParser.dataStructures;

import java.util.ArrayList;
import java.util.List;

public class VertexSkinData {

    public final List<Integer> jointIds = new ArrayList<>();
    public final List<Float> weights = new ArrayList<>();

    public void addJointEffect(int jointId, float weight) {
        for (int i = 0; i < this.weights.size(); i++) {
            if (weight > this.weights.get(i)) {
                this.jointIds.add(i, jointId);
                this.weights.add(i, weight);
                return;
            }
        }
        this.jointIds.add(jointId);
        this.weights.add(weight);
    }

    public void limitJointNumber(int max) {
        if (this.jointIds.size() > max) {
            float[] topWeights = new float[max];
            float total = saveTopWeights(topWeights);
            refillWeightList(topWeights, total);
            removeExcessJointIds(max);
        } else if (this.jointIds.size() < max) {
            fillEmptyWeights(max);
        }
    }

    private void fillEmptyWeights(int max) {
        while (this.jointIds.size() < max) {
            this.jointIds.add(0);
            this.weights.add(0f);
        }
    }

    private float saveTopWeights(float[] topWeightsArray) {
        float total = 0;
        for (int i = 0; i < topWeightsArray.length; i++) {
            topWeightsArray[i] = this.weights.get(i);
            total += topWeightsArray[i];
        }
        return total;
    }

    private void refillWeightList(float[] topWeights, float total) {
        this.weights.clear();
        for (float topWeight : topWeights) {
            this.weights.add(Math.min(topWeight / total, 1));
        }
    }

    private void removeExcessJointIds(int max) {
        while (this.jointIds.size() > max) {
            this.jointIds.remove(this.jointIds.size() - 1);
        }
    }
}