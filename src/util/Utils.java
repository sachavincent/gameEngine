package util;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import terrains.TerrainPosition;

public class Utils {

    public static void main(String[] args) {
        cleanOBJFile("insula.obj");
    }

    public static void cleanOBJFile(String fileName) {
        List<String> lines = new ArrayList<>();

        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;

        State state = State.DEFAULT;
        int nbVertices = 0;
        int nbVerticesBoundingBox = 0;
        int nbVerticesSelectionBox = 0;
        try {
            fileReader = new FileReader("res/" + fileName);
            bufferedReader = new BufferedReader(fileReader);

            String line;
            boolean firstLine = false;
            while ((line = bufferedReader.readLine()) != null) {
                if (!firstLine) {
                    if (!line.startsWith("#")) {
                        System.err.println("File '" + fileName + "' already cleaned!");

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

            bufferedReader.close();
            fileReader.close();

            fileWriter = new FileWriter("res/" + fileName);
            bufferedWriter = new BufferedWriter(fileWriter);
            for (String l : lines) {
                bufferedWriter.write(l);
                bufferedWriter.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null)
                    bufferedReader.close();
                if (fileReader != null)
                    fileReader.close();
                if (bufferedWriter != null)
                    bufferedWriter.close();
                if (fileWriter != null)
                    fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
}
