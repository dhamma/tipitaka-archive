package org.tipitaka.archive.notes;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class NotesLocator
{

  private NotesLocator(){}

  private final static File BASE = new File("archive/notes/roman");
  private final static ObjectMapper MAPPER = new XmlMapper();

  public static File toFile(String path) {
    return new File(BASE, path + ".xml");
  }

  public static Notes toNotes(String path) throws IOException {
    try {
      return MAPPER.readValue(new FileReader(toFile(path)), Notes.class);
    }
    catch(IOException e) {
      // this is happens if the file does not exists or has no nodes
      // where the parsing fails as there are no nested 'note' elements
      Notes notes = new Notes();
      notes.archivePath = path;
      return notes;
    }
  }
}
