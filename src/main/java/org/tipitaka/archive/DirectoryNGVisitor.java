package org.tipitaka.archive;

import java.io.IOException;
import java.io.Writer;

import org.tipitaka.archive.BuilderFactory;
import org.tipitaka.archive.NGBuilder;
import org.tipitaka.archive.NGVisitor;
import org.tipitaka.archive.Notes.Version;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Created by cmeier on 4/15/16.
 */
public class DirectoryNGVisitor
    extends NGVisitor
{

  public DirectoryNGVisitor(final BuilderFactory<NGBuilder> builders)
      throws XmlPullParserException
  {
    super(builders);
  }

  @Override
  public void accept(final Writer writer, final String path, final String version) throws IOException {
    if (path.endsWith("index.html")) {
      try (NGBuilder builder = getBuilders().create(writer)) {
        String extension = path.substring(path.lastIndexOf('.'));
        builder.init(extension, version);
        builder.startDocument();
        builder.startMetadata();
        final String script;
        if (path.startsWith("/index")){
          builder.script("roman");
          //builder.directory("/");
        }
        else {
          script = path.substring(1, path.indexOf('/', 1));
          builder.script(script);
          builder.directory(path.substring(script.length() + 1, path.lastIndexOf('/')));
        }
        builder.basename(path.substring(path.lastIndexOf('/') + 1, path.length() - extension.length()));

        builder.title(null);
        builder.startVersions();
        Notes.Version ver = version == null ? Version.VIPASSANA_RESEARCH_INSTITUT: Notes.Version.toVersion(version);
        if (ver != null) {
          builder.addVersion(ver.getAbbrevation(), ver.getName());
        }
        else {
          builder.addVersion(version, version);
        }
        builder.endVersions();
        builder.endMetadata();
        builder.startContent();

        builder.endContent();
        builder.endDocument();
      }
    }
    else {
      super.accept(writer, path, version);
    }
  }
}
