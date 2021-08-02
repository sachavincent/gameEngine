package util;

import java.io.File;

public class ResourceFile extends File {

    public ResourceFile(String fileName) {
        super(Utils.RES_PATH + "/" + fileName);
    }
}
