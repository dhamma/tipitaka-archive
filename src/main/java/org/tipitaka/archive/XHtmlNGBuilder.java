package org.tipitaka.archive;

import java.io.IOException;
import java.io.Writer;

/**
 * Created by cmeier on 4/1/16.
 */
public class XHtmlNGBuilder
    extends AbstractNGBuilder
{
  static public class BuilderFactory
      extends NGBuilderFactory {

    public BuilderFactory() throws IOException {
      this(new Layout());
    }

    public BuilderFactory(Layout layout) throws IOException {
      super(layout);
    }

    @Override
    public XHtmlNGBuilder create(final Writer writer) {
      return new XHtmlNGBuilder(writer, this);
    }
  }

  public XHtmlNGBuilder(Writer writer, NGBuilderFactory factory) {
    super(writer, factory);
  }

  protected void pStart(Writer writer, String clazz, String number) throws IOException {
    state.append("<p class=\"").append(clazz).append("\">");
    String lineNumber = state.nextNumber();
    state.append("<a class=\"line-number\" name=\"line").append(lineNumber).append("\" href=\"")
  //      .append("/").append(state.builder.script.name)
    //    .append(state.builder.path).append(".html#line").append(lineNumber).append("\">")
        .append(lineNumber).append("</a>");
    if (number != null) {
      //state.isAnchor = true;
      state.append("<a name=\"para").append(number).append("\" href=\"")
        //  .append("/").append(state.builder.script.name)
          //.append(state.builder.path)
          .append(".html#para").append(number).append("\">");
    }
  }

  protected void pEnd(Writer writer, final String clazz) throws IOException {
    writer.append("</p>");
  }

  @Override
  public void init(final String... args) {

  }

  @Override
  public void startDocument() throws IOException {

  }

  @Override
  public void endDocument() throws IOException {

  }

  @Override
  public void startMetadata() throws IOException {

  }

  @Override
  public void endMetadata() throws IOException {

  }

  @Override
  public void normativeSource(final String url) {

  }

  @Override
  public void archivePath(final String path) {

  }

  @Override
  public void title(final String title) {

  }

  @Override
  public void startContent() throws IOException {

  }

  @Override
  public void endContent() throws IOException {

  }

  @Override
  public void startNikaya() throws IOException {

  }

  @Override
  public void endNikaya() throws IOException {

  }

  @Override
  public void startBook() throws IOException {

  }

  @Override
  public void endBook() throws IOException {

  }

  @Override
  public void startChapter() throws IOException {

  }

  @Override
  public void endChapter() throws IOException {

  }

  @Override
  public void startCentered() throws IOException {

  }

  @Override
  public void endCentered() throws IOException {

  }

  @Override
  public void startSubhead() throws IOException {

  }

  @Override
  public void endSubhead() throws IOException {

  }

  @Override
  public void startIndent() throws IOException {

  }

  @Override
  public void endIndent() throws IOException {

  }

  @Override
  public void startBold() throws IOException {

  }

  @Override
  public void endBold() throws IOException {

  }

  @Override
  public void startParagraph(final String number) throws IOException {

  }

  @Override
  public void endParagraph() throws IOException {

  }

  @Override
  public void startHangnum(final String number) throws IOException {

  }

  @Override
  public void endHangnum() throws IOException {

  }

  @Override
  public void startGatha(final String number) throws IOException {

  }

  @Override
  public void endGatha(final String substring) throws IOException {

  }

  @Override
  public void pageBreak(final Edition edition, final String number) throws IOException {

  }

  @Override
  public void startNote() throws IOException {

  }

  @Override
  public void endNote() throws IOException {

  }

  @Override
  public void startAlternatives(final String extra, final boolean hasSeparator) throws IOException {

  }

  @Override
  public void endAlternatives() throws IOException {

  }

  @Override
  public void beginAlternative(final String abbr, final String source) {

  }

  @Override
  public void finalizeAlternative(final String text) {

  }

  @Override
  public void startTitle() throws IOException {

  }

  @Override
  public void endTitle() throws IOException {

  }

  @Override
  public void startVersions() {

  }

  @Override
  public void addVersion(final String abbr, final String source) {

  }

  @Override
  public void endVersions() {

  }

  @Override
  public void flush() throws IOException {

  }

  @Override
  public void text(final String text) throws IOException {

  }

  @Override
  public void close() throws IOException {

  }
}
