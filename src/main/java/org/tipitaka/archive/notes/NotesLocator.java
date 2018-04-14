package org.tipitaka.archive.notes;

import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.JsonMappingException;

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
    catch(FileNotFoundException e) {
      Notes notes = new Notes();
      notes.archivePath = path;
      return notes;
    }
    catch(JsonMappingException e) {
      throw new IOException(toFile(path) + "\n" + e.getMessage(), e);
    }
  }
}
