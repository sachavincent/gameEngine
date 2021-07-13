package util.parsing.colladaParser.xmlParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlParser {

    private static final Pattern DATA      = Pattern.compile(">(.+?)<");
    private static final Pattern START_TAG = Pattern.compile("<(.+?)>");
    private static final Pattern ATTR_NAME = Pattern.compile("(.+?)=");
    private static final Pattern ATTR_VAL  = Pattern.compile("\"(.+?)\"");
    private static final Pattern CLOSED    = Pattern.compile("(</|/>)");

    /**
     * Reads an XML file and stores all the data in {@link XmlNode} objects,
     * allowing for easy access to the data contained in the XML file.
     *
     * @param file - the XML file
     * @return The root node of the XML structure.
     */
    public static XmlNode loadXmlFile(File file) {
        XmlNode node = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            node = loadNode(reader);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Can't find the XML file: " + file.getPath());
        }
        return node;
    }

    private static XmlNode loadNode(BufferedReader reader) throws IOException {
        String line = reader.readLine().trim();
        if (line.startsWith("</")) {
            return null;
        }
        String[] startTagParts = getStartTag(line).split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        XmlNode node = new XmlNode(startTagParts[0].replace("/", ""));
        addAttributes(startTagParts, node);
        addData(line, node);
        if (CLOSED.matcher(line).find()) {
            return node;
        }
        XmlNode child;
        while ((child = loadNode(reader)) != null) {
            node.addChild(child);
        }
        return node;
    }

    private static void addData(String line, XmlNode node) {
        Matcher matcher = DATA.matcher(line);
        if (matcher.find()) {
            node.setData(matcher.group(1));
        }
    }

    private static void addAttributes(String[] titleParts, XmlNode node) {
        for (int i = 1; i < titleParts.length; i++) {
            if (titleParts[i].contains("=")) {
                addAttribute(titleParts[i], node);
            }
        }
    }

    private static void addAttribute(String attributeLine, XmlNode node) {
        Matcher nameMatch = ATTR_NAME.matcher(attributeLine);
        nameMatch.find();
        Matcher valMatch = ATTR_VAL.matcher(attributeLine);
        valMatch.find();
        try {
            node.addAttribute(nameMatch.group(1), valMatch.group(1));
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private static String getStartTag(String line) {
        Matcher match = START_TAG.matcher(line);
        match.find();
        return match.group(1);
    }

}