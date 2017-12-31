package org.tipitaka.archive;

import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by cmeier on 3/7/16.
 */
public class FuzzyTest
{
  @Test
  public void testSimple() {
    assertThat(Fuzzy.findMatchingText("asd", "asd"), is("asd"));
    assertThat(Fuzzy.findMatchingText("asd","qwe zxc asdq"), is("asdq"));
  }

  @Test
  public void testMatching() {
    assertThat(Fuzzy.fuzzy("asd", "asd"), is(0));
    assertThat(Fuzzy.fuzzy("asd", "asdqwe"), is(50));
    assertThat(Fuzzy.fuzzy("asd", "qweasd"), is(50));
    assertThat(Fuzzy.fuzzy("asd", "qweasdqwe"), is(200));
  }

  @Test
  public void test1() {
    assertThat(Fuzzy.fuzzy("bhummajakā", "mettiyabhūmajakā"), lessThan(100));
    assertThat(Fuzzy.findMatchingText("bhummajakā", "Tena kho pana samayena mettiyabhūmajakā"), is("mettiyabhūmajakā"));
  }

  @Test @Ignore(value = "it is not possible to match second last word meaningfully")
  public void test2() {
    assertThat(Fuzzy.fuzzy("tadāpakkantova", "pakkantova"), greaterThan(0));
    assertThat(Fuzzy.findMatchingText("tadāpakkantova", "pakkāmi. Tathā pakkantova"), is("Tathā pakkantova"));
  }

  @Test
  public void test3() {
    assertThat(Fuzzy.fuzzy("byābāhissantīti", "byābādhissantīti"), greaterThan(0));
    assertThat(Fuzzy.findMatchingText("na byābāhissantīti", "te aññamaññaṃ na byābādhissantī’’ti "), is("na byābādhissantīti"));
  }

  @Test
  public void test4() {
    assertThat(Fuzzy.findMatchingText("channo so kumārako imissā kumārikāyāti",
            "medhāvī dakkho analaso. Channāyaṃ kumārikā tassa kumārakassā’’ti "),
        is("Channāyaṃ kumārikā tassa kumārakassāti"));
  }

  @Test
  public void test5() {
    assertThat(Fuzzy.findMatchingText("tassa mocanāmippāyassa", "bhikkhuno aṅgajāte vaṇo hoti. Mocanādhippāyassa "),
        is("Mocanādhippāyassa"));
  }

  @Test
  public void test6() {
    assertThat(Fuzzy.findMatchingText("tassa bhesajjena", "bhikkhuno aṅgajāte vaṇo hoti. Bhesajjena "), is("Bhesajjena"));
  }

  @Test
  public void test7() {
    assertThat(Fuzzy.fuzzy("nīlaṃ", "nīlañca"), lessThan(100));
    assertThat(Fuzzy.fuzzy("ārogyatthaṃ", "Ārogyatthañca"), lessThan(100));
    assertThat(Fuzzy.findMatchingText("ārogyatthaṃ nīlaṃ", " Ārogyatthañca nīlañca "), is("Ārogyatthañca nīlañca"));
  }

  @Test
  public void test9() {
    assertThat(Fuzzy.fuzzy("dīghapāvāro", "pāvāro"), greaterThan(0));
    assertThat(Fuzzy.findMatchingText("dīghapāvāro", "Sā na paṭivijāni. Āmāyya, pāvāro’’ "), is("pāvāro"));
  }

  @Test
  public void test10() {
    assertThat(Fuzzy.findMatchingText("itthī taṃ passitvā etadavoca muhuttaṃ", "gacchati. Aññatarā itthī – ‘muhuttaṃ "),
        is("itthī muhuttaṃ"));
  }

  @Test
  public void test11() {
    assertThat(Fuzzy.findMatchingText("yo kho bhikkhave bhikkhu", " kathaṃ katvā bhikkhū āmantesi – yo, bhikkhave "),
        is("yo bhikkhave"));
  }

  @Test
  public void test12() {
    assertThat(Fuzzy.findMatchingText("kalandagāmo nāma hoti", "avidūre kalandagāmo nāma atthi "),
        is("kalandagāmo nāma atthi"));
  }

  @Test @Ignore(value = "broken")
  public void test13() {
    assertThat(Fuzzy.findMatchingText("honti te ānanda", " bhagavato etamatthaṃ ārocesi. Honti ye te, ānanda"),
        is("honti ye te ānanda"));
    assertThat(Fuzzy.findMatchingText("honti yevānanda", " bhagavato etamatthaṃ ārocesi. Honti ye te, ānanda"),
        is("honti ye te ānanda"));
  }

  @Test @Ignore(value = "broken")
  public void test14() {
    assertThat(Fuzzy.findMatchingText("ummattakassa khittacittassa vedanāṭṭassa",
        "asañcicca ajānantassa namaraṇādhippāyassa ummattakassa "),
        is("ummattakassa"));
  }

  @Test @Ignore(value = "broken")
  public void test15() {
    assertThat(Fuzzy.findMatchingText("sūlaṃvā laguḷaṃvā", "laguḷaṃ vā "), is("laguḷaṃ vā"));
  }

  @Test @Ignore(value = "broken")
  public void test16() {
    assertThat(Fuzzy.findMatchingText("ācikkheyyāsīti so kālamakāsi", "hoti pasanno tassa ācikkheyyāsī’’ti"),
        is("ācikkheyyāsīti"));
  }

  @Test @Ignore(value = "better match this manually")
  public void test17() {
    assertThat(Fuzzy.findMatchingText("tvampi yāca", "tena hi, vadhu, tvaṃ piyā ca manāpā ca "),
        is("tvaṃ piyā ca manāpā ca "));
  }

  //@Test
  //public void test6() {
  //  assertThat(Fuzzy.findMatchingText("", ""), is(""));
  //}
}
