package util.colladaParser.colladaLoader;

import animation.Animation;
import animation.JointTransform;
import animation.KeyFrame;
import java.io.File;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.lwjgl.BufferUtils;
import util.colladaParser.dataStructures.AnimationData;
import util.colladaParser.dataStructures.JointTransformData;
import util.colladaParser.dataStructures.KeyFrameData;
import util.colladaParser.xmlParser.XmlNode;
import util.math.Matrix4f;
import util.math.Quaternion;
import util.math.Vector3f;

public class AnimationLoader {

    private static final Matrix4f CORRECTION = new Matrix4f()
            .rotate((float) Math.toRadians(-90), new Vector3f(1, 0, 0));

    private XmlNode animationData;
    private XmlNode jointHierarchy;

    /**
     * Loads up a collada animation file, and returns and animation created from
     * the extracted animation data from the file.
     *
     * @param colladaFile - the collada file containing data about the desired
     * animation.
     * @return The animation made from the data in the file.
     */
    public static Animation loadAnimation(File colladaFile) {
        AnimationData animationData = ColladaLoader.loadColladaAnimation(colladaFile);
        if (animationData == null)
            return null;

        KeyFrame[] frames = new KeyFrame[animationData.keyFrames.length];
        for (int i = 0; i < frames.length; i++) {
            frames[i] = createKeyFrame(animationData.keyFrames[i]);
        }
        return new Animation(animationData.lengthSeconds, frames);
    }

    /**
     * Creates a keyframe from the data extracted from the collada file.
     *
     * @param data - the data about the keyframe that was extracted from the
     * collada file.
     * @return The keyframe.
     */
    private static KeyFrame createKeyFrame(KeyFrameData data) {
        Map<String, JointTransform> map = new HashMap<>();
        for (JointTransformData jointData : data.getJointTransforms()) {
            JointTransform jointTransform = createTransform(jointData);
            map.put(jointData.getJointId(), jointTransform);
        }
        return new KeyFrame(data.getTime(), map);
    }

    /**
     * Creates a joint transform from the data extracted from the collada file.
     *
     * @param data - the data from the collada file.
     * @return The joint transform.
     */
    private static JointTransform createTransform(JointTransformData data) {
        Matrix4f mat = data.getJointLocalTransform();
        Vector3f translation = new Vector3f(mat.m30, mat.m31, mat.m32);
        Quaternion rotation = Quaternion.fromMatrix(mat);
        return new JointTransform(translation, rotation);
    }

    public AnimationLoader(XmlNode animationData, XmlNode jointHierarchy) {
        this.animationData = animationData.getChild("animation");
        this.jointHierarchy = jointHierarchy;
    }

    public AnimationData extractAnimation() {
        String rootNode = findRootJointName();
        float[] times = getKeyTimes();
        float duration = times[times.length - 1];
        KeyFrameData[] keyFrames = initKeyFrames(times);
        List<XmlNode> animationNodes = animationData.getChildren("animation");
        for (XmlNode jointNode : animationNodes) {
            loadJointTransforms(keyFrames, jointNode, rootNode);
        }
        return new AnimationData(duration, keyFrames);
    }

    private float[] getKeyTimes() {
        XmlNode timeData = animationData.getChild("animation").getChild("source").getChild("float_array");
        String[] rawTimes = timeData.getData().split(" ");
        float[] times = new float[rawTimes.length - 1];
        for (int i = 1; i < times.length; i++) {
            times[i] = Float.parseFloat(rawTimes[i]);
        }
        return times;
    }

    private KeyFrameData[] initKeyFrames(float[] times) {
        KeyFrameData[] frames = new KeyFrameData[times.length];
        for (int i = 0; i < frames.length; i++) {
            frames[i] = new KeyFrameData(times[i]);
        }
        return frames;
    }

    private void loadJointTransforms(KeyFrameData[] frames, XmlNode jointData, String rootNodeId) {
        String jointId = getJointId(jointData);
        String dataId = getDataId(jointData);
        XmlNode transformData = jointData.getChildWithAttribute("source", "id", dataId);
        String[] rawData = transformData.getChild("float_array").getData().split(" ");
        processTransforms(jointId, rawData, frames, jointId.equals(rootNodeId));
    }

    private String getDataId(XmlNode jointData) {
        XmlNode node = jointData.getChild("sampler").getChildWithAttribute("input", "semantic", "OUTPUT");
        return node.getAttribute("source").substring(1);
    }

    private String getJointId(XmlNode jointData) {
        XmlNode channelNode = jointData.getChild("channel");
        String data = channelNode.getAttribute("target");
        return data.split("/")[0];
    }

    private void processTransforms(String jointId, String[] rawData, KeyFrameData[] keyFrames, boolean root) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        float[] matrixData = new float[16];
        for (int i = 0; i < keyFrames.length; i++) {
            for (int j = 0; j < 16; j++) {
                matrixData[j] = Float.parseFloat(rawData[i * 16 + j]);
            }
            buffer.clear();
            buffer.put(matrixData);
            buffer.flip();
            Matrix4f transform = new Matrix4f();
            transform.load(buffer);
            transform.transpose();
            if (root) {
                //because up axis in Blender is different to up axis in game
                Matrix4f.mul(CORRECTION, transform, transform);
            }
            keyFrames[i].addJointTransform(new JointTransformData(jointId, transform));
        }
    }

    private String findRootJointName() {
        XmlNode skeleton = jointHierarchy.getChild("visual_scene").getChildWithAttribute("node", "id", "Armature");
        return skeleton.getChild("node").getAttribute("id");
    }
}