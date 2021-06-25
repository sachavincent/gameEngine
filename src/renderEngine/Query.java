package renderEngine;

import entities.Entity;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import scene.Scene;
import scene.components.PreviewComponent;
import scene.gameObjects.GameObject;

public class Query {

    private final Queue<Integer> lastUpdateTimes;
    private final int            id;
    private final int            type;

    private boolean inUse = false;
    private int     candidateId;
    private Entity  candidateEntity;

    public Query(int type) {
        this.lastUpdateTimes = new ArrayDeque<>();
        this.type = type;
        this.id = GL15.glGenQueries();
    }

    /**
     * Adds new gameObjects to the rendering Queue
     * Ignores previewed gameObjects
     *
     * @param gameObjects all gameObjects
     * @return all the new gameObjects
     */
    public Set<GameObject> initNewGameObjects(Set<GameObject> gameObjects) {
        Set<GameObject> newGameObjects = gameObjects.stream()
                .filter(gameObject -> !this.lastUpdateTimes.contains(gameObject.getId()))
                .collect(Collectors.toSet());
        newGameObjects.stream()
                .filter(gameObject -> !gameObject.hasComponent(PreviewComponent.class) ||
                        gameObject.getComponent(PreviewComponent.class).getPreviewPosition() == null)
                .map(GameObject::getId).forEach(this.lastUpdateTimes::add);

        return newGameObjects;
    }

    public GameObject getBestUpdateCandidate() {
        if (this.lastUpdateTimes == null || this.lastUpdateTimes.isEmpty())
            return null;

        int id = this.lastUpdateTimes.poll();
        GameObject gameObjectFromId = Scene.getInstance().getGameObjectFromId(id);
        if (gameObjectFromId != null) {
            this.lastUpdateTimes.add(id);
        }
        return gameObjectFromId;
    }

    public void start(int candidateId, Entity candidateEntity) {
        GL15.glBeginQuery(this.type, this.id);
        this.inUse = true;
        this.candidateId = candidateId;
        this.candidateEntity = candidateEntity;
    }

    public void stop() {
        GL15.glEndQuery(this.type);
    }

    public boolean isResultReady() {
        return GL15.glGetQueryObjecti(this.id, GL15.GL_QUERY_RESULT_AVAILABLE) == GL11.GL_TRUE;
    }

    public int getCandidateId() {
        return this.candidateId;
    }

    public Entity getCandidateEntity() {
        return this.candidateEntity;
    }

    public boolean isInUse() {
        return this.inUse;
    }

    public int getResult() {
        this.inUse = false;
        return GL15.glGetQueryObjecti(this.id, GL15.GL_QUERY_RESULT);
    }

    public void delete() {
        GL15.glDeleteQueries(this.id);
    }

    public Queue<Integer> getLastUpdateTimes() {
        return this.lastUpdateTimes;
    }
}