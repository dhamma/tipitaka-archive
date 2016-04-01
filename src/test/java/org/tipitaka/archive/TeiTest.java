package org.tipitaka.archive;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

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
    script = factory.script("romn");
  }

  //@Test
  //public void testSingle() throws Exception {
  //  assertTei("/tipitaka (mula)/vinayapitaka/culavaggapali/3. samuccayakkhandhakam");
  //}

  @Test
  public void testMulaVinayaCulavaggapali() throws Exception {
    assertTeiDirectory("/tipitaka (mula)/vinayapitaka/culavaggapali");
  }

  @Test
  public void testMulaVinayaMahavaggapali() throws Exception {
    assertTeiDirectory("/tipitaka (mula)/vinayapitaka/mahavaggapali");
  }

  @Test
  public void testMulaVinayaPacittiyapali() throws Exception {
    // TODO note with nested PB
    assertTeiDirectory("/tipitaka (mula)/vinayapitaka/pacittiyapali");//, "5. pacittiyakandam" );
  }

  @Test
  public void testMulaVinayaParajikapali() throws Exception {
    assertTeiDirectory("/tipitaka (mula)/vinayapitaka/parajikapali");
  }

  @Test
  public void testMulaVinayaParivarapali() throws Exception {
    assertTeiDirectory("/tipitaka (mula)/vinayapitaka/parivarapali");
  }

  @Test
  public void testMulaSuttapitakaDighanikayaSilakkhandhavaggapali() throws Exception {
    assertTeiDirectory("/tipitaka (mula)/suttapitaka/dighanikaya/silakkhandhavaggapali", "3. ambatthasuttam");
  }

  @Test
  public void testMulaSuttapitakaDighanikayaMahavaggapali() throws Exception {
    assertTeiDirectory("/tipitaka (mula)/suttapitaka/dighanikaya/mahavaggapali");
  }

  @Test
  public void testMulaSuttapitakaDighanikayaPathikavaggapali() throws Exception {
    assertTeiDirectory("/tipitaka (mula)/suttapitaka/dighanikaya/pathikavaggapali");
  }

  @Test
  public void testMulaSuttapitakaMajjhimanikayaMulapannasapali() throws Exception {
    assertTeiDirectory("/tipitaka (mula)/suttapitaka/majjhimanikaya/mulapannasapali");
  }

  @Test
  public void testMulaSuttapitakaMajjhimanikayaMajjhimapannasapali() throws Exception {
    assertTeiDirectory("/tipitaka (mula)/suttapitaka/majjhimanikaya/majjhimapannasapali", "1. gahapativaggo", "4. rajavaggo");
  }

  @Test
  public void testMulaSuttapitakaMajjhimanikayaUparipannasapali() throws Exception {
    assertTeiDirectory("/tipitaka (mula)/suttapitaka/majjhimanikaya/uparipannasapali");
  }

  private void assertTeiDirectory(String path, String... exceptions) throws IOException {
    File dir = new File(factory.getArchiveDirectory().getCanonicalFile(), "roman/"+ path);
    for (File file: dir.listFiles()) {
      if (!Arrays.asList(exceptions).contains(file.getName().replace(".xml", ""))) {
        assertTei(file.getPath().replace(".xml", "").replace(factory.getArchiveDirectory().getCanonicalPath(), "")
            .replace("roman/", ""));
      }
    }
    if (exceptions.length > 0) System.err.println("\t" + path + "\n\t" + Arrays.asList(exceptions));
  }

  private String normalize(String string) {
    return string
        .replace("/tipitaka-latn.xs", "tipitaka-latn.xs")
        // notes inside quotes
        .replaceAll("’’<note>", "<note>")
        .replaceAll("</note>’’", "</note>")
        // white spaces and dots before and after notes
        .replaceAll("</note>\\.? ", "</note>")
        .replaceAll("\\.? <note>", "<note>")
        // notes nested with pb
        .replaceAll("</note><pb[^>]+>", "</note>")
        .replaceAll("<pb[^>]+><note>", "<note>")
        // white spaces and dots before and after notes
        .replaceAll("</note>\\.? ", "</note>")
        .replaceAll("\\.? <note>", "<note>")
            // notes nested with hi
        .replaceAll("</hi><note>", "<note>")
        .replaceAll("</note>\\.?</hi>", "</note>")
            // notes inside quotes
        .replaceAll("–<note>", "<note>")
        .replaceAll("</note>–", "</note>")
            // notes nested with pb
        .replaceAll("</note><pb[^>]+>", "</note>")
        .replaceAll("<pb[^>]+><note>", "<note>")
            // white spaces and dots before and after notes
        .replaceAll("</note>[., ]+", "</note>")
        .replaceAll("[., ]+<note>", "<note>")
            // notes inside quotes
        .replaceAll("’’<note>", "<note>")
        .replaceAll("</note>\\s*’’", "</note>")
            // notes nested with hi
        .replaceAll("</hi><note>", "<note>")
        .replaceAll("</note>\\.?</hi> ?", "</note>")
            // special cases :(
        .replaceAll("syā\\)", "syā.)")
        .replaceAll("ka\\)", "ka.)")
        .replaceAll("syā.,", "syā.")
        .replaceAll("ṃ\\(ka.", "ṃ (ka.")
        // /tipitaka (mula)/vinayapitaka/mahavaggapali/1. mahakhandhako
        .replace("<note>nissayā ", "<note>")
        // /tipitaka (mula)/vinayapitaka/mahavaggapali/8. civarakkhandhako
        .replace("uttarāḷupaṃ (yojanā), uttarāḷupaṃ (syā.)", "uttarāḷupaṃ (yojanā syā.)")
            // /tipitaka (mula)/vinayapitaka/pacittiyapali/5. pacittiyakandam
        .replace("<pb ed=\"T\" n=\"5.1080\" />", "</note><note>")
        // /tipitaka (mula)/suttapitaka/dighanikaya/silakkhandhavaggapali/2. samannaphalasuttam
        .replace("samaggatā (ka. syā.)", "samaggatā (ka.), samaggatā (syā.)")
        // /tipitaka (mula)/suttapitaka/dighanikaya/mahavaggapali/1. mahapadanasuttam
        .replace("(pī.) cattāro", "(pī.), cattāro")
        // /tipitaka (mula)/suttapitaka/dighanikaya/mahavaggapali/4. mahasudassanasuttam
        .replace("dukūlasandanāni(pī.)", "dukūlasandanāni (pī.)")
        .replace("(ka. sī. pī.) velāmikā", "(ka. sī. pī.), velāmikā")
            // /tipitaka (mula)/suttapitaka/dighanikaya/mahavaggapali/6. mahagovindasuttam
        .replace("(sī. pī.),sukha", "(sī. pī.), sukha")
            // /tipitaka (mula)/suttapitaka/dighanikaya/mahavaggapali/9. mahasatipatthanasuttam
        .replace("<hi rend=\"paranum\">389</hi>.", "<hi rend=\"paranum\">389</hi><hi rend=\"dot\">.</hi>.")
        // /tipitaka (mula)/suttapitaka/dighanikaya/pathikavaggapali/9. atanatiyasuttam
        .replace("(sī. syā. pī)", "(sī. syā. pī.)")
        // /tipitaka (mula)/suttapitaka/majjhimanikaya/mulapannasapali/2. sihanadavaggo
        .replace("koti (?)", "ko’’ti (?)")
        // /tipitaka (mula)/suttapitaka/majjhimanikaya/mulapannasapali/3. opammavaggo
        .replace("hoti</note>", "ho’’ti</note>")
        .replace("udakassā’’ti", "udakassāti")
        .replace("toti (?)", "to’’ti (?)")
        // /tipitaka (mula)/suttapitaka/majjhimanikaya/majjhimapannasapali/3. paribbajakavaggo
        .replace("(sī. pī.) ( )", "(sī. pī.)")
        .replace("(ka.) abhidoti", "(ka.), abhidoti")
        //  /tipitaka (mula)/suttapitaka/majjhimanikaya/majjhimapannasapali/5. brahmanavaggo
        .replace("(sī. ka.), kathaṃ (syā. kaṃ. pī.)", "(sī. ka. syā. kaṃ. pī.)")
        .replace("(sī. syā. kaṃ. pī)", "(sī. syā. kaṃ. pī.)")
        // /tipitaka (mula)/suttapitaka/majjhimanikaya/uparipannasapali/2. anupadavaggo
        .replace("(pī. ka.) ( )", "(pī. ka.)")
        //  /tipitaka (mula)/suttapitaka/majjhimanikaya/uparipannasapali/5. salayatanavaggo
        .replace("hutvā’’ti ṭīkāya", "hutvāti ṭīkāya")
        ;
  }

  private void assertTei(final String path, Integer... skip) throws IOException {
    StringWriter writer = new StringWriter();
    visitor.accept(writer, script, path);

    URL source = factory.getUrlFactory().sourceURL(script, factory.getDirectory().fileOf(path));

    int index = 0;
    try (InputStream is = source.openStream()) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-16"));
      String[] result = writer.toString().split("\n");
      for (String exp = reader.readLine(); exp != null; exp = reader.readLine()) {//expected) {
        index++;
        if (Arrays.asList(skip).indexOf(index) == -1) {
          String res = normalize(result[index - 1]);
          assertThat(path + " line " + index, res, is(normalize(exp)));
        }
      }
      if (skip.length > 0) System.err.println("\t" + path + "\n\t" + Arrays.asList(skip));
    }
  }
}
