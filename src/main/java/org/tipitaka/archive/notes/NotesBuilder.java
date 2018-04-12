package org.tipitaka.archive.notes;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import org.tipitaka.archive.Fuzzy;
import org.tipitaka.archive.legacy.NoopBuilder;
import org.tipitaka.archive.model.Document;

public class NotesBuilder extends NoopBuilder
{

  public static final String NOTE = "__NOTE__";

  private final static Pattern ALTERNATIVE = Pattern.compile(",? ?;?([^()+0-9]*[(]((kaṃ|ka|sī|syā|pī|ṭī|[?]),?[.]?[ ]?)+[)]),? ?");

  private final static Pattern VERSIONS = Pattern.compile("(kaṃ|ka|sī|syā|pī|ṭī|[?])");

  private final static Pattern HINT = Pattern.compile(",? ?(([^…()-+- ]+[ ][+=][ ])+[^…()-+- ]+),? ?");

  private final static Pattern REFERENCES = Pattern.compile("(\\s|^|;|\\))+(([^0-9 ]+\\.\\s)+[0-9]+([.][0-9]+)*);?");

  private final Notes oldNotes;
  private final Notes newNotes;
  private Note note;

  public NotesBuilder(Notes notes) {
    this.oldNotes = notes;
    this.newNotes = new Notes();
  }

  public Notes get() {
    return this.newNotes;
  }

  @Override
  public void paraStart(String name, String number) throws IOException {
    state.nextLineNumber();
  }

  @Override
  public void documentStart(final Document document) throws IOException {
    newNotes.archivePath = document.getPath();
  }

  @Override
  public void noteStart() throws IOException {
    if (this.note != null) throw new RuntimeException();
    state.push(NOTE);
    this.note = new Note();
    this.note.id = state.nextId();
    this.note.referenceLine = state.getLineNumber();
  }

  @Override
  public void noteEnd() throws IOException {
    state.pop();
    this.newNotes.addNote(this.note);
    this.note = null;
  }

  private boolean hint(String text, String previous) throws IOException {
    Matcher matcher = HINT.matcher(text);
    if (matcher.matches()) {
      String hint = matcher.group(1);
      this.note.hint = hint;
      String match = matcher.group(1).replaceFirst(".*=\\s", "");
      if (!previous.contains(match)) {
        return false;
      }
      if (hint.contains("=") && !match.isEmpty()) {
        this.note.match = match;
      }
      return true;
    }
    return false;
  }

  private boolean alternative(String item) throws IOException {
    String text = item.replaceFirst("\\(.*", "").trim();
    Matcher matcher = VERSIONS.matcher(item.replaceFirst(".*\\(", "("));
    while(matcher.find()) {
      Version version = Version.toVersion(matcher.group(1));
      Alternative alt = new Alternative();
      alt.setVersion(version);
      alt.text = text;
      this.note.addAlternative(alt);
    }
    return !text.contains(" ");
  }

  private boolean alternatives(String text, String previous) throws IOException {
    Matcher matcher = ALTERNATIVE.matcher(text);
    List<String> found = new LinkedList<String>();
    int end = 0;
    int start = 0;
    while(matcher.find()) {
      if (found.isEmpty()) start = matcher.start();
      found.add(matcher.group(1));
      end = matcher.end();
    }
    if (!found.isEmpty() && start == 0) {
      String subtext = text.substring(end);
      if (text.length() != end && !hint(subtext, previous) && !references(subtext)) {
        return false;
      }
      boolean single = true;
      for(String item: found) {
        single &= alternative(item);
      }
      if (single) {
        String match = previous.trim().replaceFirst(".*\\ ", "");
        this.note.match = match;
      }
      return true;
    }
    return false;
  }

  private boolean references(String text) throws IOException {
    Matcher matcher = REFERENCES.matcher(text);
    List<String> found = new LinkedList<String>();
    int end = 0;
    int start = 0;
    while(matcher.find()) {
      if (found.isEmpty()) start = matcher.start();
      found.add(matcher.group(2));
      end = matcher.end();
    }
    if (!found.isEmpty() && start == 0 && text.length() == end) {
      this.note.references = found;
      return true;
    }
    return false;
  }

  @Override
  public void text(String text) throws IOException {
    if (NOTE.equals(state.peek())) {
      String previous = state.getPreviousText();
      this.note.original = text;
      this.note.snippet = previous;
      this.note.type = Type.raw;
      if (alternatives(text, previous)) {
        this.note.type = Type.auto;
      }
      else {
        if (hint(text, previous) || references(text)) {
          this.note.type = Type.auto;
        }
        //        else if (text.matches(".*[(][^ ]*[)]$")) {
        //System.out.println(previous + ":::::" + text);
        //}
      }

      if (this.note.type == Type.auto) {
        Note oldNote = oldNotes.get(this.note.id);
        if (oldNote != null) {
          if (oldNote.type == Type.manual) {
            this.note.type = Type.manual;
            this.note.match = oldNote.match;
          }
          else if (oldNote.type == Type.confirmed) {
            this.note.type = Type.confirmed;
          }
        }
        if (this.note.match == null) {
          this.note.type = Type.no_match;
        }
      }
      if (this.note.type == Type.raw) {
        System.err.println("[WARN] (create.raw) " + text);
      }
      else if (this.note.type == Type.auto) {
        System.err.println("[WARN] (create.match) " + this.note.match + " <> " + this.note.original + "\n\t" + this.note.snippet);
      }
    }
    else {
      state.setPreviousText(text);
    }
  }

}
