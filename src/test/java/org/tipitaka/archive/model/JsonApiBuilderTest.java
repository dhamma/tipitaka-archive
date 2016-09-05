package org.tipitaka.archive.model;

import org.junit.Before;
import org.junit.Test;

import javax.print.Doc;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by christian on 03.09.16.
 */
public class JsonApiBuilderTest {

    private Menu menu;
    private Menu menuiVRI;
    private Menu menuMula;
    private Menu menuVinayapitaka;
    private Menu menuParajikapali;
    private ToC toc;
    private ToC tocThai;
    private Document document;

    @Before
    public void setupMenu() {
        Map<String, String> items = new LinkedHashMap<>();
        items.put("mula", "Mūla");
        items.put("atthakatha", "Aṭṭhakathā");
        items.put("tika", "Tīkā");
        items.put("anya", "Anya");
        menu = new Menu(Script.roman, null, null, false, items);
        menuiVRI = new Menu(Script.roman, null, "vri", false, items);

        items = new LinkedHashMap<>();
        items.put("vinayapitaka", "Vinayapiṭaka");
        items.put("suttapitaka", "Suttapiṭaka");
        items.put("abhidhammapitaka", "Abhidhammapiṭaka");
        menuMula = new Menu(Script.roman, "/mula", null, false, items);

        items = new LinkedHashMap<>();
        items.put("parajikapali", "Pārājikapāḷi");
        items.put("pacittiyapali", "Pācittiyapāḷi");
        items.put("mahavaggapali", "Mahāvaggapāḷi");
        items.put("culavaggapali", "Cūḷavaggapāḷi");
        items.put("parivarapali", "Parivārapāḷi");
        menuVinayapitaka = new Menu(Script.roman, "/mula/vinayapitaka", null, false, items);

        items = new LinkedHashMap<>();
        items.put("veranjakandam", "Verañjakaṇḍaṃ");
        items.put("1. parajikakandam", "1. Pārājikakaṇḍaṃ");
        items.put("2. sanghadisesakandam", "2. Saṅghādisesakaṇḍaṃ");
        items.put("3. aniyatakandam", "3. Aniyatakaṇḍaṃ");
        items.put("4. nissaggiyakandam", "4. Nissaggiyakaṇḍaṃ");
        menuParajikapali = new Menu(Script.roman, "/mula/vinayapitaka/parajikapali", null, false, items);

        toc = new ToC(Script.roman, "/mula/vinayapitaka/parajikapali", null, Arrays.asList("Pārājikapāḷi","Vinayapiṭaka","Mūla"), items, true,
                menu, menuMula, menuVinayapitaka);
        tocThai= new ToC(Script.roman, "/mula/vinayapitaka/parajikapali", "thai", Arrays.asList("Pārājikapāḷi","Vinayapiṭaka","Mūla"), items, true,
                menu, menuMula, menuVinayapitaka);

        document = new Document(Script.roman, "/mula/vinayapitaka/parajikapali/veranjakandam",
                null, Arrays.asList("Verañjakaṇḍaṃ","Pārājikapāḷi","Vinayapiṭaka","Mūla"),
                "http://tipitaka.org/romn/cscd/vin01m.mul0.xml", "/roman/mula/vinayapitaka/parajikapali/veranjakandam.xml",
                Arrays.asList("vri"), menu, menuMula, menuVinayapitaka, menuParajikapali);
    }

    @Test
    public void testDocument() throws IOException {
        StringBuilder result = new StringBuilder();
        JsonApiBuilder builder = new JsonApiBuilder(result);
        builder.build(document);
        String reference = getReferenceFile("veranjakandam");
        assertThat(reference, is(result.toString()));
    }

    @Test
    public void testToc() throws IOException {
        StringBuilder result = new StringBuilder();
        JsonApiBuilder builder = new JsonApiBuilder(result);
        builder.build(toc);
        String reference = getReferenceFile("parajikapali");
        assertThat(reference, is(result.toString()));

        StringBuilder resultCompact = new StringBuilder();
        builder = new JsonApiBuilder(resultCompact, "");
        builder.build(toc);
        assertThat(result.toString().replaceAll("\\s", ""),
                is(resultCompact.toString().replaceAll("\\.\\s", ".")));

        reference = getReferenceFile("tocCompact");
        assertThat(reference, is(resultCompact.toString() + "\n"));
    }

    @Test
    public void testMenu() throws IOException {
        StringBuilder result = new StringBuilder();
        JsonApiBuilder builder = new JsonApiBuilder(result);
        builder.build(menu);
        String reference = getReferenceFile("root");
        assertThat(reference, is(result.toString()));

        StringBuilder resultCompact = new StringBuilder();
        builder = new JsonApiBuilder(resultCompact, "");
        builder.build(menu);
        assertThat(result.toString().replaceAll("\\s", ""), is(resultCompact.toString()));
        reference = getReferenceFile("menuCompact");
        assertThat(reference, is(resultCompact.toString() + "\n"));

        StringBuilder resultVRI = new StringBuilder();
        builder = new JsonApiBuilder(resultVRI);
        builder.build(menuiVRI);
        assertThat(resultVRI.toString(), not(result.toString()));
        assertThat(resultVRI.toString().replaceAll("\"vri\"", "null"), not(result.toString()));
        assertThat(resultVRI.toString().replaceAll("\"vri\"", "null").replaceAll("[?]version=vri", ""),
                is(result.toString()));

        StringBuilder resultMula = new StringBuilder();
        builder = new JsonApiBuilder(resultMula);
        builder.build(menuMula);
        reference = getReferenceFile("mula");
        assertThat(reference, is(resultMula.toString()));

    }

    private static String getReferenceFile(String name) throws IOException {
        return new String(Files.readAllBytes(new File("src/test/resources/" + name + ".json").toPath()));
    }
}
