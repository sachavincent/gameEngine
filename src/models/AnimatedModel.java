package models;

import animation.Animation;
import animation.Animator;
import animation.Joint;
import renderEngine.Vao;
import renderEngine.shaders.structs.Material;
import util.math.Matrix4f;

import java.util.List;

public class AnimatedModel extends AbstractModel {

    private final Joint rootJoint;
    private final int   jointCount;

    private final Animator animator;
    private final List<Material> materials;

    public AnimatedModel(Vao vao, Joint rootJoint, int jointCount, List<Material> materials) {
        super(vao);
        this.materials = materials;
        this.rootJoint = rootJoint;
        this.jointCount = jointCount;
        this.animator = new Animator(this);
        rootJoint.calcInverseBindTransform(new Matrix4f());
    }

    @Override
    public List<Material> getMaterials() {
        return this.materials;
    }

    public Joint getRootJoint() {
        return this.rootJoint;
    }    /**
     * Instructs this entity to carry out a given animation. To do this it
     * basically sets the chosen animation as the current animation in the
     * {@link Animator} object.
     *
     * @param animation - the animation to be carried out.
     */
    public void doAnimation(Animation animation) {
        this.animator.doAnimation(animation);
    }

    /**
     * Updates the animator for this entity, basically updating the animated
     * pose of the entity. Must be called every frame.
     */
    public void update() {
        this.animator.update();
    }
    /**
     * Gets an array of the all important model-space transforms of all the
     * joints (with the current animation pose applied) in the entity. The
     * joints are ordered in the array based on their joint index. The position
     * of each joint's transform in the array is equal to the joint's index.
     *
     * @return The array of model-space transforms of the joints in the current
     * animation pose.
     */
    public List<Matrix4f> getJointTransforms() {
        Matrix4f[] jointMatrices = new Matrix4f[this.jointCount];
        addJointsToArray(this.rootJoint, jointMatrices);
        return List.of(jointMatrices);
    }

    /**
     * This adds the current model-space transform of a joint (and all of its
     * descendants) into an array of transforms. The joint's transform is added
     * into the array at the position equal to the joint's index.
     *
     * @param headJoint - the current joint being added to the array. This method also
     * adds the transforms of all the descendents of this joint too.
     * @param jointMatrices - the array of joint transforms that is being filled.
     */
    private void addJointsToArray(Joint headJoint, Matrix4f[] jointMatrices) {
        jointMatrices[headJoint.getIndex()] = headJoint.getAnimatedTransform();
        for (Joint childJoint : headJoint.getChildren()) {
            addJointsToArray(childJoint, jointMatrices);
        }
    }
}