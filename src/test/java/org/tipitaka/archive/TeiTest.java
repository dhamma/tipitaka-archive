package org.tipitaka.archive;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.tipitaka.search.Script;
import org.tipitaka.search.ScriptFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by cmeier on 3/7/16.
 */
public class TeiTest
{

  BuilderFactory factory;
  NGVisitor visitor;
  Script script;
  @Before
  public void setup() throws Exception {
    factory = new TeiNGBuilder.BuilderFactory();
    visitor = new NGVisitor(factory);
    script = new ScriptFactory().script("romn");
  }

  @Test @Ignore("pending")
  public void testSingle() throws Exception {
    assertTei("/tipitaka (mula)/vinayapitaka/parivarapali/antarapeyyalam");
  }

  @Test
  public void testMulaVinayaCulavaggapali() throws Exception {
    // TODO missed note
    assertTeiDirectory("/tipitaka (mula)/vinayapitaka/culavaggapali","3. samuccayakkhandhakam", "5. khuddakavatthukkhandhakam",
        "9. patimokkhatthapanakkhandhakam");
  }

  @Test
  public void testMulaVinayaMahavaggapali() throws Exception {
    // TODO hangnum indent and missed notes and wrong alternatives splitting
    assertTeiDirectory("/tipitaka (mula)/vinayapitaka/mahavaggapali", "1. mahakhandhako", "2. uposathakkhandhako",
        "4. pavaranakkhandhako", "7. kathinakkhandhako", "8. civarakkhandhako" );
  }

  @Test
  public void testMulaVinayaPacittiyapali() throws Exception {
    // TODO note with nested PB
    assertTeiDirectory("/tipitaka (mula)/vinayapitaka/pacittiyapali", "5. pacittiyakandam" );
  }

  @Test
  public void testMulaVinayaParajikapali() throws Exception {
    assertTeiDirectory("/tipitaka (mula)/vinayapitaka/parajikapali");
  }

  @Test
  public void testMulaVinayaParivarapali() throws Exception {
    // TODO hangnum
    assertTeiDirectory("/tipitaka (mula)/vinayapitaka/parivarapali", "antarapeyyalam", "aparagathasanganikam",
        "codanakandam", "culasangamo", "dutiyagathasanganikam", "gathasanganikam", "kathinabhedo", "khandhakapucchavaro",
        "mahasangamo", "samutthanasisasankhepo", "sedamocanagatha", "solasamahavaro");
  }

  private void assertTeiDirectory(String path, String... expeceptions) throws IOException {
    File dir = new File(factory.getArchiveDirectory().getCanonicalFile(), "roman/"+ path);
    for (File file: dir.listFiles()) {
      if (!Arrays.asList(expeceptions).contains(file.getName().replace(".xml", ""))) {
        assertTei(file.getPath().replace(".xml", "").replace(factory.getArchiveDirectory().getCanonicalPath(), "")
            .replace("roman/", ""));
      }
    }
  }

  private String normalize(String string) {
    return string
        // notes inside quotes
        .replaceAll("’’<note>", "<note>")
        .replaceAll("</note>’’", "</note>")
        // white spaces and dots before and after notes
        .replaceAll("</note>\\.? ", "</note>")
        .replaceAll("\\.? <note>", "<note>")
        // notes nested with pb
        .replaceAll("</note><pb[^>]+/>", "</note>")
        .replaceAll("<pb[^>]+/><note>", "<note>")
        // white spaces and dots before and after notes
        .replaceAll("</note>\\.? ", "</note>")
        .replaceAll("\\.? <note>", "<note>")
        // notes nested with hi
        .replaceAll("</hi><note>", "<note>")
        .replaceAll("</note></hi>", "</note>")
        // notes inside quotes
        .replaceAll("–<note>", "<note>")
        .replaceAll("</note>–", "</note>")
        // white spaces and dots before and after notes
        .replaceAll("</note> +", "</note>")
        .replaceAll(" +<note>", "<note>")
        // notes inside quotes
        .replaceAll("’’<note>", "<note>")
        .replaceAll("</note>\\s*’’", "</note>")
        // special cases :(
        .replaceAll("syā\\)", "syā.)")
        .replaceAll("ka\\)", "ka.)")
        .replaceAll("syā.,", "syā.")
        .replaceAll("ṃ\\(ka.", "ṃ (ka.")
        ;
  }

  private void assertTei(final String path, Integer... skip) throws IOException {StringWriter writer = new StringWriter();
    visitor.accept(writer, script, path);

    URL source = factory.getUrlFactory().sourceURL(script, factory.getDirectory().fileOf(path));

    List<String> expected = IOUtils.readLines(source.openStream(), "utf-16");
    int index = 0;
    String[] result = writer.toString().split("\n");
    for(String exp : expected) {
      index++;
      if (Arrays.asList(skip).indexOf(index) == -1) {
        String res = normalize(result[index - 1]);
        assertThat(path + " line " + index, res, is(normalize(exp)));
      }
    }
    if (skip.length > 0) System.err.println("\t" + path + "\n\t" + Arrays.asList(skip));
  }
}
