package org.tipitaka.archive.converter;

import org.junit.Test;
import org.junit.Before;

import java.io.File;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.tipitaka.archive.model.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.hamcrest.Matchers.is;

/**
 * Created by christian on 06.09.17.
 */
public class ModelBuilderTest {

    static ModelBuilder builder;

    private int count = 0;

    @Before
    public void setup() throws Exception {
        if (builder == null) {
            builder = new ModelBuilder();
        }
    }

    private void increment() {
        count++;
    }

    @Test
    public void testMenu() throws Exception {
        count = 0;
        builder.eachMenu(menu -> { count++; });
        assertThat(count, is(2953));
        assertThat(builder.menus.size(), is(2953));
    }

    @Test
    public void testFolder() throws Exception {
        count = 0;
        builder.eachFolder(folder -> {
                count++;
                assertFolder(folder);
            });
        assertThat(count, is(255));
        assertThat(builder.menus.size(), is(2953));
    }

    private void assertFolder(Folder folder) {
        String path = folder.getPath();
        if (path != null) {
            assertThat(path.replaceAll("[^/]", "").length() + 1,
                       is(folder.getMenus().size()));
        }
        else {
            assertThat(1, is(folder.getMenus().size()));
        }
    }

    @Test
    public void testDocument() throws Exception {
        int count = 0;
        Set<String> paths = new TreeSet<String>();
        Set<String> normatives = new TreeSet<String>();
        for(Node node: builder.directory.nodes) {
            Document doc = builder.buildDocument(node);
            if (doc != null) {
                count++;
                paths.add(doc.getPath());
                normatives.add(doc.getNormativeSource());
            }
        }
        assertThat(count, is(2698));
        assertThat(paths.size(), is(2698));
        assertThat(normatives.size(), is(2698));
    }
}
