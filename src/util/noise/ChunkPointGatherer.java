package util.noise;

import java.util.List;

/**
 * Credits to KdotJPG
 * https://github.com/KdotJPG/Scattered-Biome-Blender
 */
public class ChunkPointGatherer<TTag> {

    private static final double CHUNK_RADIUS_RATIO = Math.sqrt(1.0 / 2.0);

    private final int halfChunkWidth;
    private final double maxPointContributionRadius;
    private final double maxPointContributionRadiusSq;
    private final double radiusPlusHalfChunkWidth;
    private final UnfilteredPointGatherer<TTag> unfilteredPointGatherer;

    public ChunkPointGatherer(double frequency, double maxPointContributionRadius, int chunkWidth) {
        this.halfChunkWidth = chunkWidth / 2;
        this.maxPointContributionRadius = maxPointContributionRadius;
        this.maxPointContributionRadiusSq = maxPointContributionRadius * maxPointContributionRadius;
        this.radiusPlusHalfChunkWidth = maxPointContributionRadius + this.halfChunkWidth;
        this.unfilteredPointGatherer = new UnfilteredPointGatherer<>(frequency,
                maxPointContributionRadius + chunkWidth * CHUNK_RADIUS_RATIO);
    }

    public List<GatheredPoint<TTag>> getPointsFromChunkBase(long seed, int chunkBaseWorldX, int chunkBaseWorldZ) {
        // Technically, the true minimum is between coordinates. But tests showed it was more efficient to add before converting to doubles.
        return getPointsFromChunkCenter(seed, chunkBaseWorldX +
                this.halfChunkWidth, chunkBaseWorldZ + this.halfChunkWidth);
    }

    public List<GatheredPoint<TTag>> getPointsFromChunkCenter(long seed, int chunkCenterWorldX, int chunkCenterWorldZ) {
        List<GatheredPoint<TTag>> worldPoints =
                this.unfilteredPointGatherer.getPoints(seed, chunkCenterWorldX, chunkCenterWorldZ);
        for (int i = 0; i < worldPoints.size(); i++) {
            GatheredPoint<TTag> point = worldPoints.get(i);

            // Check if point contribution radius lies outside any coordinate in the chunk
            double axisCheckValueX = Math.abs(point.getX() - chunkCenterWorldX) - this.halfChunkWidth;
            double axisCheckValueZ = Math.abs(point.getZ() - chunkCenterWorldZ) - this.halfChunkWidth;
            if (axisCheckValueX >= this.maxPointContributionRadius ||
                    axisCheckValueZ >= this.maxPointContributionRadius ||
                    (axisCheckValueX > 0 && axisCheckValueZ > 0 &&
                            axisCheckValueX * axisCheckValueX + axisCheckValueZ *
                                    axisCheckValueZ >= this.maxPointContributionRadiusSq)) {

                // If so, remove it.
                // Copy the last value to this value, and remove the last,
                // to avoid shifting because order doesn't matter.
                int lastIndex = worldPoints.size() - 1;
                worldPoints.set(i, worldPoints.get(lastIndex));
                worldPoints.remove(lastIndex);
                i--;
            }
        }

        return worldPoints;
    }
}