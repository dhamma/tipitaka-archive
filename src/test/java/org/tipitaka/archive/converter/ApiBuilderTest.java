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
public class ApiBuilderTest {

    static ApiBuilder builder;

    @Before
    public void setup() throws Exception {
        if (builder == null) {
            builder = new ApiBuilder();
        }
    }

    @Test
    public void test() throws Exception {
        File api = new File("target/api");
        builder.build(api);
        int count = countDirectories(0, new File(api, "roman"));
        assertThat(count, is(255));
        count = countFiles(0, api);
        assertThat(count, is(2953));
    }

    private int countDirectories(int count, File dir) {
        for (File file: dir.listFiles()) {
            if (file.isDirectory()) count = countDirectories(count, file);
        }
        return count + 1;
    }

    private int countFiles(int count, File file) {
        for (File item: file.listFiles()) {
            if (item.isDirectory()) {
                count = countFiles(count, item);
            }
            else {
                count++;
            }
        }
        return count;
    }
}
