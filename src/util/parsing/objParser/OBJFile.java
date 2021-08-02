package util.parsing.objParser;

import java.io.File;
import java.util.Objects;

public class OBJFile extends File {

    private final String name;

    private MTLFile MTLFile;

    public OBJFile(File parent, String name) {
        super(parent, name);

        this.name = name;
    }

    public OBJFile(File file) {
        this(file.getParentFile(), file.getName());
    }

    public String getName() {
        return this.name;
    }

    public MTLFile getMTLFile() {
        return this.MTLFile;
    }

    public void setMTLFile(MTLFile MTLFile) {
        this.MTLFile = MTLFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        OBJFile objFile = (OBJFile) o;
        return Objects.equals(this.name, objFile.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }
}
