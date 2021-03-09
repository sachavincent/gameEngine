package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class Utils {

    public static void main(String[] args) {
        cleanOBJFile("market.obj");
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
                                newLineBuilder.append(Integer.parseInt(p[0]) - nbVertices).append(" ");
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
}
