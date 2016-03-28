package org.tipitaka.archive;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
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

  @Test
  public void test1() throws Exception {
    assertTei("/tipitaka (mula)/vinayapitaka/parajikapali/veranjakandam");
  }

  @Test
  public void test2() throws Exception {
    assertTei("/tipitaka (mula)/vinayapitaka/parajikapali/1. parajikakandam");
  }

  @Test
  public void test3() throws Exception {
    assertTei("/tipitaka (mula)/vinayapitaka/parajikapali/2. sanghadisesakandam", 847, 1321, 1543);
  }

  @Test
  public void test4() throws Exception {
    assertTei("/tipitaka (mula)/vinayapitaka/parajikapali/3. aniyatakandam");
  }

  @Test
  public void test5() throws Exception {
    assertTei("/tipitaka (mula)/vinayapitaka/parajikapali/4. nissaggiyakandam");
  }

  private String normalize(String string) {
    return string
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
        .replaceAll("</note></hi>", "</note>");
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
