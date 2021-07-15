package util;

import guis.presets.Background;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import scene.gameObjects.DirtRoad;
import scene.gameObjects.GameObject;
import scene.gameObjects.Insula;
import scene.gameObjects.Market;
import terrains.TerrainPosition;
import util.exceptions.MissingFileException;
import util.math.Maths;
import util.math.Vector;

public class Utils {

    public final static String RES_PATH    = "res";
    public final static String ASSETS_PATH = "assets";
    public final static String MODELS_PATH = "models";

    public static void main(String[] args) {
        cleanOBJFile(new File(MODELS_PATH + "/Windmill/boundingbox.obj"));
    }

    public static Map<Class<? extends GameObject>, TerrainPosition[]> getPositionsFromConsoleLogs(String fileName) {
        Map<Class<? extends GameObject>, TerrainPosition[]> map = new HashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(
                new FileReader(ASSETS_PATH + "/" + fileName))) {
            String line;
            Class<? extends GameObject> clazz = null;
            List<TerrainPosition> positions = new ArrayList<>();

            whileloop:
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("selected")) {
                    if (clazz != null)
                        map.put(clazz, positions.toArray(new TerrainPosition[0]));
                    String objName = line.split(" ")[0];
                    switch (objName) {
                        case "DirtRoad":
                            clazz = DirtRoad.class;
                            positions = new ArrayList<>();
                            break;
                        case "Market":
                            clazz = Market.class;
                            positions = new ArrayList<>();
                            break;
                        case "Insula":
                            clazz = Insula.class;
                            positions = new ArrayList<>();
                            break;
                        default:
                            continue whileloop;
                    }
                } else if (line.contains("Placing")) {
                    String[] coords = line.split("\\{")[1].split("}")[0].split(", ");
                    String x = coords[0].split("=")[1];
                    String z = coords[1].split("=")[1];
                    positions.add(new TerrainPosition(Integer.parseInt(x), Integer.parseInt(z)));
                } else {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return map;
    }

    public static void cleanOBJFile(File file) {
        if (!file.exists())
            throw new MissingFileException(file);

        List<String> lines = new ArrayList<>();

        State state = State.DEFAULT;
        int nbVertices = 0;
        int nbVerticesBoundingBox = 0;
        int nbVerticesSelectionBox = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;
            boolean firstLine = false;
            while ((line = reader.readLine()) != null) {
                if (!firstLine) {
                    if (!line.startsWith("#")) {
                        System.err.println("File '" + file.getName() + "' already cleaned!");

                        return;
                    }
                    firstLine = true;
                }

                // Comments
                if (line.startsWith("#") || line.startsWith("s"))
                    continue;

                String[] parts = line.split(" ");
                if (parts.length == 0)
                    continue;

                // Object name
                if (line.startsWith("o")) {
                    lines.add(line);

                    if (line.contains("SelectionBox")) {
                        state = State.SELECTION_BOX;
                        nbVertices += nbVerticesBoundingBox;
                    } else if (line.contains("BoundingBox")) {
                        state = State.BOUNDING_BOX;
                        nbVertices += nbVerticesSelectionBox;
                    }
                    continue;
                }

                // vertices
                if (line.startsWith("v ")) {
                    switch (state) {
                        case DEFAULT:
                            nbVertices++;
                            break;
                        case BOUNDING_BOX:
                            nbVerticesBoundingBox++;
                            break;
                        case SELECTION_BOX:
                            nbVerticesSelectionBox++;
                            break;
                    }
                }

                if ((state == State.SELECTION_BOX || state == State.BOUNDING_BOX) && !line.startsWith("v ")) {
                    if (line.startsWith("vt") || line.startsWith("vn"))
                        continue;

                    if (line.startsWith("f")) {
                        StringBuilder newLineBuilder = new StringBuilder();
                        for (String part : parts) {
                            if (part.equals("f"))
                                newLineBuilder.append(part).append(" ");
                            else {
                                String[] p = part.split("/");
                                newLineBuilder.append(Integer.parseInt(p[0])).append(" ");
                            }
                        }
                        String newLine = newLineBuilder.toString().trim();
                        lines.add(newLine);
                    }
                    continue;
                }

                StringBuilder newLineBuilder = new StringBuilder();
                for (String part : parts) {
                    part = part.trim();
                    if (part.matches("^-?[0-9]+\\.[0-9]+$"))
                        part = String.format(Locale.ROOT, "%.4f", Float.parseFloat(part));

                    newLineBuilder.append(part).append(" ");
                }
                String newLine = newLineBuilder.toString().trim();
                lines.add(newLine);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String l : lines) {
                writer.write(l);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Background<File> importResourceTexture(String textureName) {
        return new Background<>(new File(RES_PATH + "/" + textureName + ".png"));
    }

    /**
     * Parses values as string and creates a vector
     */
    public static <V extends Vector> V parseVector(Class<V> vectorClass, String[] values) {
        Object[] floatValues = new Object[values.length];
        Class<?>[] floatClasses = new Class[values.length];
        for (int i = 0; i < values.length; i++) {
            floatClasses[i] = Float.class;
            floatValues[i] = Float.parseFloat(values[i]);
        }
        try {
            return vectorClass.getConstructor(floatClasses).newInstance(floatValues);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }

    enum State {
        DEFAULT,
        SELECTION_BOX,
        BOUNDING_BOX
    }

    public static <T> boolean listContentEquals(Collection<T> list1, Collection<T> list2) {
        return list1.size() == list2.size() && list1.containsAll(list2) && list2.containsAll(list1);
    }

    /**
     * Changes alpha value of color
     *
     * @param color original color
     * @param alpha must be between 0 and 255
     * @return new color
     */
    public static Color setAlphaColor(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static String formatText(String text) {
        if (text.isEmpty())
            return "";

        UnaryOperator<String> capitalize = str ->
                str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();

        return Stream.of(text.split(" ")).map(capitalize).collect(Collectors.joining(" "));
    }

    /**
     * Color to hex
     */
    public static String encodeColor(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * @param position must has form (x,y)
     * @return TerrainPosition
     */
    public static TerrainPosition decodePositionCommand(String position) {
        position = position.substring(1, position.length() - 1);
        String[] split = position.split(",");
        return new TerrainPosition(Integer.parseInt(split[0].trim()), Integer.parseInt(split[1].trim()));
    }

    public static double formatDoubleToNDecimals(double value, int N) {
        if (N <= 0)
            return value;

        StringBuilder pattern = new StringBuilder("#.");
        for (int i = 0; i < N; i++)
            pattern.append("#");
        return Double.parseDouble(new DecimalFormat(pattern.toString()).format(value).replace(',', '.'));
    }

    /**
     * Healthy  TPS : > 15 => GREEN
     * OK       TPS : > 10 => YELLOW
     * BAD      TPS : > 5 => RED
     * MEDIOCRE TPS : > 0 => DARK RED
     *
     * @param TPS must be between 0 and 20
     * @return resulting Color
     */
    public static Color getColorForTPS(double TPS) {
        if (TPS > 20 || TPS < 0)
            return Color.BLACK;

        if (TPS > 15)
            return Color.decode("#4CAF50");
        if (TPS > 10)
            return Color.decode("#FFA726");
        if (TPS > 5)
            return Color.decode("#F44336");
        else
            return Color.decode("#D50000");
    }

    /**
     * MEDIOCRE MSPT : > 50 => DARK RED
     * BAD      MSPT : > 40 => RED
     * OK       MSPT : < 40 & > 15 => YELLOW
     * Healthy  MSPT : < 15 => GREEN
     *
     * @param MSPT must be between 0 and 20
     * @return resulting Color
     */
    public static Color getColorForMSPT(double MSPT) {
        if (MSPT < 0)
            return Color.BLACK;

        if (MSPT >= 50)
            return Color.decode("#D50000");
        if (MSPT > 40)
            return Color.decode("#F44336");
        if (MSPT <= 40 && MSPT > 15)
            return Color.decode("#FFA726");

        return Color.decode("#4CAF50");
    }


    /**
     * Healthy  amount : > 50% of maxAmount => GREEN
     * OK       amount : > 30% of maxAmount => YELLOW
     * BAD      amount : > 10% of maxAmount => RED
     * MEDIOCRE amount : < 10% of maxAmount => DARK RED
     *
     * @param amount current amount for the given resource
     * @param maxAmount max amount possible
     * @return resulting Color
     * if maxAmount = Integer.MAX_VALUE, return WHITE
     */
    public static Color getColorForResource(double amount, int maxAmount) {
        if (maxAmount == Integer.MAX_VALUE)
            return Color.WHITE;

        if (amount > 0.5 * maxAmount)
            return Color.decode("#4CAF50");
        if (amount > 0.3 * maxAmount)
            return Color.decode("#FFA726");
        if (amount > 0.1 * maxAmount)
            return Color.decode("#F44336");

        return Color.decode("#D50000");
    }


    public static <T> T pickRandomWeightedMap(Map<T, Integer> map) {
        AtomicInteger totalWeight = new AtomicInteger();
        List<T> randomList = new ArrayList<>();
        map.forEach((obj, probability) -> {
            for (int i = 0; i < probability; i++)
                randomList.add(obj);
            totalWeight.addAndGet(probability);
        });

        int idx = 0;
        for (double r = Math.random() * (totalWeight.get() / 100d); idx < map.size() - 1; ++idx) {
            T obj = randomList.get(idx);
            r -= map.get(obj) / 100d;
            if (r <= 0.0)
                break;
        }
        return (T) map.keySet().toArray()[idx];
    }

    public static boolean isPowerOfTwo(int number) {
        return number > 0 && ((number & (number - 1)) == 0);
    }

    public static int findPositionOf1(int n) {
        if (!isPowerOfTwo(n))
            return 0;

        return Maths.log(n, 2) + 1;
    }

    public static void reverseArray(int[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            int temp = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = temp;
        }
    }
}
