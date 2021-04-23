package renderEngine;

import util.math.Maths;
import util.math.Matrix4f;
import util.math.Vector3f;
import util.math.Vector4f;

public class FrustumCullingFilter {

    private static final int NUM_PLANES = 6;

    private static final Vector4f[] frustumPlanes = new Vector4f[NUM_PLANES];

    public static void updateFrustum() {
//        if(GuiItemSelection.getItemSelectionGui().isDisplayed())
//            return;

        Matrix4f projMatrix = MasterRenderer.getInstance().getProjectionMatrix();
        Matrix4f viewMatrix = Maths.createViewMatrix();
        projMatrix = Matrix4f.mul(projMatrix, viewMatrix, null);

        for (int i = 0; i < NUM_PLANES; i++) {
            frustumPlanes[i] = projMatrix.frustumPlane(i);
        }

//        MasterRenderer.getInstance().getItemRenderer().setUpdateNeeded(true);
    }

    public static boolean isPosInsideFrustum(Vector3f pos, float boundingRadius) {
        if (frustumPlanes[0] == null)
            return false;
        for (int i = 0; i < NUM_PLANES; i++) {
            Vector4f plane = frustumPlanes[i];
            if (plane.x * pos.getX() + plane.y * pos.getY() + plane.z * pos.getZ() + plane.w <= -boundingRadius) {
                return false;
            }
        }
        return true;
    }
}