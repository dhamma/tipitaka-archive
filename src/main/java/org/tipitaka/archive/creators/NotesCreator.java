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
import org.tipitaka.archive.notes.Notes;
import org.tipitaka.archive.notes.NotesBuilder;
import org.tipitaka.archive.notes.NotesLocator;
import org.tipitaka.archive.notes.XmlBuilder;
import org.tipitaka.archive.legacy.Visitor;

import org.tipitaka.archive.StandardException;

public class NotesCreator {

    private final ModelIterator model;
    private final String sourceUrl;
    private final Visitor visitor;

    public NotesCreator() throws IOException, StandardException {
        this(TipitakaOrgTocVisitor.mirror());
    }

    public NotesCreator(TipitakaOrgTocVisitor tocVisitor) throws IOException, StandardException {
        this.model = new ModelIterator(tocVisitor);
        this.sourceUrl = tocVisitor.getBaseUrl() + "/romn";
        this.visitor = new Visitor();
    }

    public void create(final File path) throws IOException {
        path.mkdirs();
        final File base = new File(path, Script.roman.name());
        base.mkdir();
        model.eachFolder(folder -> { buildFolder(base, folder); });
        model.eachDocument(doc -> { buildDocument(base, doc); });
    }

    private void buildFolder(final File base, final Folder folder) throws IOException {
        File fpath = path(base, folder);
        fpath.mkdir();
    }

    private void buildDocument(final File base, final Document doc) throws IOException {
        File file = new File(base, doc.getPath() + ".xml");
        String source = sourceUrl + "/" + doc.getNormativeSource();

        Notes notes = NotesLocator.toNotes(doc.getPath());
        NotesBuilder builder = new NotesBuilder(notes);
        visitor.accept(builder, source, doc);
        Notes newNotes = builder.get();
        if (!newNotes.isEmpty()) {
          try (Writer writer = new FileWriter(file)) {
            new XmlBuilder(writer).build(newNotes);
          }
        }
    }

    private File path(final File base, final Folder folder) {
        if (folder.getPath() != null) {
            return new File(base, folder.getPath());
        }
        return base;
    }
}
