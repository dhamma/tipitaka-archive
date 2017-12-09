package org.tipitaka.archive.model;

import org.junit.Before;
import org.junit.Test;

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
public class JsonBuilderTest {

    private Menu menu;
    private Menu menuiVRI;
    private Menu menuMula;
    private Menu menuVinayapitaka;
    private Menu menuParajikapali;
    private Folder folder;
    private Folder folderThai;
    private Document document;

    @Before
    public void setupMenu() {
        Map<String, String> items = new LinkedHashMap<>();
        items.put("mula", "Mūla");
        items.put("atthakatha", "Aṭṭhakathā");
        items.put("tika", "Tīkā");
        items.put("anya", "Anya");
        menu = new Menu(null, items);

        items = new LinkedHashMap<>();
        items.put("vinayapitaka", "Vinayapiṭaka");
        items.put("suttapitaka", "Suttapiṭaka");
        items.put("abhidhammapitaka", "Abhidhammapiṭaka");
        menuMula = new Menu("/mula", items);

        items = new LinkedHashMap<>();
        items.put("parajikapali", "Pārājikapāḷi");
        items.put("pacittiyapali", "Pācittiyapāḷi");
        items.put("mahavaggapali", "Mahāvaggapāḷi");
        items.put("culavaggapali", "Cūḷavaggapāḷi");
        items.put("parivarapali", "Parivārapāḷi");
        menuVinayapitaka = new Menu("/mula/vinayapitaka", items);

        items = new LinkedHashMap<>();
        items.put("veranjakandam", "Verañjakaṇḍaṃ");
        items.put("1. parajikakandam", "1. Pārājikakaṇḍaṃ");
        items.put("2. sanghadisesakandam", "2. Saṅghādisesakaṇḍaṃ");
        items.put("3. aniyatakandam", "3. Aniyatakaṇḍaṃ");
        items.put("4. nissaggiyakandam", "4. Nissaggiyakaṇḍaṃ");
        menuParajikapali = new Menu("/mula/vinayapitaka/parajikapali", items);

        folder = new Folder("/mula/vinayapitaka/parajikapali", Script.roman, null, "http://example.com",
                            Arrays.asList("Pārājikapāḷi","Vinayapiṭaka","Mūla"), menu, menuMula, menuVinayapitaka);
        folderThai = new Folder("/mula/vinayapitaka/parajikapali", Script.roman, "thai", "https://example.com",
                                Arrays.asList("Pārājikapāḷi","Vinayapiṭaka","Mūla"), menu, menuMula, menuVinayapitaka);

        document = new Document("/mula/vinayapitaka/parajikapali/veranjakandam", Script.roman,
                                null, "https://example.com", Arrays.asList("Verañjakaṇḍaṃ","Pārājikapāḷi","Vinayapiṭaka","Mūla"),
                                "http://tipitaka.org/romn/cscd/vin01m.mul0.xml", "/roman/mula/vinayapitaka/parajikapali/veranjakandam.xml",
                                Arrays.asList("vri"), menu, menuMula, menuVinayapitaka, menuParajikapali);
    }

    @Test
    public void testDocument() throws IOException {
        StringBuilder result = new StringBuilder();
        JsonBuilder builder = new JsonBuilder(result);
        builder.build(document);
        String reference = getReferenceFile("veranjakandam");
        assertThat(result.toString(), is(reference));
    }

    @Test
    public void testFolder() throws IOException {
        StringBuilder result = new StringBuilder();
        JsonBuilder builder = new JsonBuilder(result);
        builder.build(folder);
        String reference = getReferenceFile("parajikapali");
        assertThat(result.toString(), is(reference));

        StringBuilder resultCompact = new StringBuilder();
        builder = new JsonBuilder(resultCompact, "");
        builder.build(folder);
        assertThat(result.toString().replaceAll("\\s", ""),
                is(resultCompact.toString().replaceAll("\\.\\s", ".")));

        reference = getReferenceFile("parajikapali-compact");
        assertThat(resultCompact.toString() + "\n", is(reference));
    }

    private static String getReferenceFile(String name) throws IOException {
        return new String(Files.readAllBytes(new File("src/test/resources/" + name + ".json").toPath()));
    }
}
