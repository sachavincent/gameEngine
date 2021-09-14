package util.parsing.objParser;

import static util.Utils.MODELS_PATH;

import java.io.File;
import models.AnimatedModel;
import models.BoundingBox;
import models.Model;
import renderEngine.AnimatedModelLoader;
import renderEngine.structures.Vao;
import scene.gameObjects.GameObjectData;
import util.exceptions.MissingFileException;
import util.parsing.ModelType;
import util.parsing.colladaParser.colladaLoader.AnimationLoader;

public class ModelLoader {

    public static GameObjectData loadModel(String folder, ModelType modelType) {
        return loadModel(folder, "vertices", modelType);
    }

    public static GameObjectData loadModel(String folder, String modelFileName, ModelType modelType) {
        GameObjectData gameObjectData = new GameObjectData();
        String extension = modelType.getExtension();
        File modelFile = new File(
                MODELS_PATH + (folder == null ? "/" : ("/" + folder + "/")) + modelFileName + extension);
        File boundingBoxFile = new File(
                MODELS_PATH + (folder == null ? "/" : ("/" + folder + "/")) + "boundingbox.obj");
        if (!modelFile.exists())
            throw new MissingFileException(modelFile);

        if (modelType.isAnimated()) {
            AnimatedModel animatedTexturedModel = AnimatedModelLoader.loadAnimatedModel(modelFile, modelType);
            animatedTexturedModel.doAnimation(AnimationLoader.loadAnimation(modelFile));
//            animatedTexturedModel.getModelTexture().setUseFakeLighting(true);
            gameObjectData.setTexture(animatedTexturedModel);
            gameObjectData.setPreviewTexture(animatedTexturedModel);
            if (boundingBoxFile.exists()) {
//                gameObjectData.setBoundingBox(BoundingBox.parseBoundingBox(boundingBoxFile));
            }
            return gameObjectData;
        }

        OBJFile objFile = OBJFile.parseOBJFile(modelFile, modelType);
        MTLFile mtlFile = objFile.getMTLFile();
        if (mtlFile != null) {
            Vao vao = Vao.createVao(mtlFile.getMeshData(), mtlFile.getMeshData().getVaoType());

            gameObjectData.setTexture(new Model(vao, objFile));
        }
        gameObjectData.setPreviewTexture(gameObjectData.getTexture());
        if (boundingBoxFile.exists()) {
//            gameObjectData.setBoundingBox(BoundingBox.parseBoundingBox(boundingBoxFile));
            OBJFile bbOBJFile = OBJFile.parseOBJFile(boundingBoxFile, ModelType.INSTANCED);
            MTLFile bbMTLFile = bbOBJFile.getMTLFile();
            Vao vao = Vao.createVao(bbMTLFile.getMeshData(), bbMTLFile.getMeshData().getVaoType());
            BoundingBox boundingBox = new BoundingBox(vao, bbOBJFile.getData(), folder);
            gameObjectData.setBoundingBox(boundingBox);
        }

        return gameObjectData;
    }
}