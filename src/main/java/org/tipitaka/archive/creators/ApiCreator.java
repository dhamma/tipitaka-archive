package org.tipitaka.archive.creators;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.tipitaka.archive.model.*;

import org.tipitaka.archive.StandardException;

public class ApiCreator {

    final ModelIterator model;

    public ApiCreator() throws IOException, StandardException {
        this(TipitakaOrgTocVisitor.mirror());
    }

    public ApiCreator(TipitakaOrgTocVisitor visitor) throws IOException, StandardException {
        this.model = new ModelIterator(visitor);
    }

    public void create(File path) throws IOException {
        path.mkdirs();
        final File base = new File(path, Script.roman.name());
        base.mkdir();
        model.eachFolder(folder -> { buildFolder(base, folder); });
        model.eachDocument(doc -> { buildDocument(base, doc); });
    }

    private void buildFolder(File base, Folder folder) throws IOException {
        File fpath = jsonPath(base, folder);
        fpath.mkdir();

        File json = new File(fpath.getPath() + ".json");
        try (FileWriter out = new FileWriter(json)) {
            new JsonBuilder(out).build(folder);
        }
    }

    private void buildDocument(File base, Document doc) throws IOException {
        File json = new File(base, doc.getPath() + ".json");
        try (FileWriter out = new FileWriter(json)) {
            new JsonBuilder(out).build(doc);
        }
    }

    private File jsonPath(File base, Folder folder) {
        if (folder.getPath() != null) {
            return new File(base, folder.getPath());
        }
        return base;
    }
}
