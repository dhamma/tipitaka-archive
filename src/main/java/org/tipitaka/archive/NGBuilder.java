package org.tipitaka.archive;

import java.io.IOException;

/**
 * Created by cmeier on 3/6/16.
 */
public interface NGBuilder extends BaseBuilder
{

  void init(final String extension, String version) throws IOException;

  void startDocument() throws IOException;
  void endDocument() throws IOException;

  void startMetadata() throws IOException;
  void endMetadata() throws IOException;

  void normativeSource(String url) throws IOException;
  void source(String source) throws IOException;
  void script(String script) throws IOException;
  void directory(String dir) throws IOException;
  void basename(String basename) throws IOException;
  void title(String title) throws IOException;

  void startContent() throws IOException;
  void endContent() throws IOException;

  void startNikaya() throws IOException;
  void endNikaya() throws IOException;

  void startBook() throws IOException;
  void endBook() throws IOException;

  void startChapter() throws IOException;
  void endChapter() throws IOException;

  void startCentered() throws IOException;
  void endCentered() throws IOException;

  void startSubhead() throws IOException;
  void endSubhead() throws IOException;

  void startIndent() throws IOException;
  void endIndent() throws IOException;

  void startBold() throws IOException;
  void endBold() throws IOException;

  void startParagraph(String number) throws IOException;
  void endParagraph() throws IOException;

  void startHangnum(String number) throws IOException;
  void endHangnum() throws IOException;

  void startGatha(String number) throws IOException;
  void endGatha(String substring) throws IOException;

  void pageBreak(Edition edition, String number) throws IOException;

  void startNote() throws IOException;
  void endNote() throws IOException;

  void startAlternatives(final String extra, final boolean hasSeparator) throws IOException;
  void endAlternatives() throws IOException;

  void beginAlternative(final String abbr, String source) throws IOException;
  void finalizeAlternative(String text) throws IOException;

  void startTitle() throws IOException;
  void endTitle() throws IOException;

  void startVersions();
  void addVersion(String abbr, String source);
  void endVersions();

}

