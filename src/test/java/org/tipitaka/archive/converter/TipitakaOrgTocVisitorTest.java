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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.hamcrest.Matchers.is;

/**
 * Created by christian on 06.09.17.
 */
public class TipitakaOrgTocVisitorTest {

    static TipitakaOrgTocVisitor visitor;
    static DirectoryStructure directory;

    @Before
    public void setup() throws Exception {
        if (visitor == null) {
            visitor = TipitakaOrgTocVisitor.mirror();
            directory = new DirectoryStructure(visitor);
        }
    }

    @Test
    public void testVisitor() throws Exception {
        assertThat(visitor.map().size(), is(2698));
    }

    @Test
    public void testDirectory() throws Exception {
        int count = 0;
        Set paths = new TreeSet();
        for(Node node: directory.nodes) {
            paths.add(node.path());
            //            System.out.println(node);
            if (node.normativePath() != null) count++;
        }
        assertThat(count, is(2698));
        assertThat(paths.size(), is(2699));
        assertThat(directory.nodes.contains(directory.root), is(true));
    }
}
