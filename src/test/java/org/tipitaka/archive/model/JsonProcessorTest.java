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
    public void testFolder() throws IOException {
        JsonProcessor processor = new JsonProcessor("src/test/resources/parajikapali.json");

        StringWriter out = new StringWriter();
        processor.accept(new JsonVisitor(out));
        String reference = getReferenceFile("parajikapali", "json");
        assertThat(reference, is(out.toString()));

        out = new StringWriter();
        processor.accept(new XmlVisitor(out));
        reference = getReferenceFile("parajikapali", "xml");
        assertThat(reference, is(out.toString().replaceAll("\\n", "")));
    }

    @Test
    public void testDocument() throws IOException {
        JsonProcessor processor = new JsonProcessor("src/test/resources/veranjakandam.json");

        StringWriter out = new StringWriter();
        processor.accept(new JsonVisitor(out));
        String reference = getReferenceFile("veranjakandam", "json");
        assertThat(reference, is(out.toString()));

        out = new StringWriter();
        processor.accept(new XmlVisitor(out));
        System.out.println(out.toString());
        reference = getReferenceFile("veranjakandam", "xml");
        assertThat(reference, is(out.toString().replaceAll("\\n", "")));
    }

    private static String getReferenceFile(String name, String ext) throws IOException {
        return new String(Files.readAllBytes(new File("src/test/resources/" +
                name + "." + ext).toPath()))
                .replaceAll("\\s+([\\{\\}\\]\"<])", "$1").replaceAll("\\n", "");
    }
}
