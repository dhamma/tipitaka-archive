package org.tipitaka.archive.model;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.hamcrest.Matchers.is;

/**
 * Created by christian on 06.09.16.
 */
public class JsonProcessorTest {

    @Test
    public void test() throws IOException {
        String[] excludes = new String[]{"", "data", "attributes", "items", "relationships", "included"};
        for (String exclude : excludes) {
            System.out.println(exclude);
            JsonProcessor processor = new JsonProcessor("src/test/resources/parajikapali.json", exclude);

            StringWriter out = new StringWriter();
            processor.accept(new JsonVisitor(out));
            String reference = getReferenceFile("parajikapali", "json", exclude);
            assertThat(reference, is(out.toString()));

            out = new StringWriter();
            processor.accept(new XmlVisitor(out));
            reference = getReferenceFile("parajikapali", "xml", exclude);
            assertThat(reference, is(out.toString().replaceAll("\\n", "")));
        }
    }

    private static String getReferenceFile(String name, String ext, String exclude) throws IOException {
        return new String(Files.readAllBytes(new File("src/test/resources/" +
                name + "." + ext + "?excludes=" + exclude).toPath()))
                .replaceAll("\\s+([\\{\\}\\]\"<])", "$1").replaceAll("\\n", "");
    }
}
