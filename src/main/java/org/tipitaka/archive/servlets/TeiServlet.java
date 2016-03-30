package org.tipitaka.archive.servlets;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tipitaka.archive.BuilderFactory;
import org.tipitaka.archive.NGVisitor;
import org.tipitaka.archive.TeiNGBuilder;
import org.tipitaka.search.Script;
import org.tipitaka.search.ScriptFactory;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Created by cmeier on 3/30/16.
 */
public class TeiServlet extends HttpServlet
{

  NGVisitor visitor;
  ScriptFactory scripts;

  @Override
  public void init() throws ServletException {
    try {
      visitor = new NGVisitor(new TeiNGBuilder.BuilderFactory());
      scripts = new ScriptFactory();
    }
    catch (IOException | XmlPullParserException e) {
      throw new ServletException(e);
    }
  }

  static Pattern PATH = Pattern.compile("^/?([^/]+)(/.*).tei$");

  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
      throws ServletException, IOException
  {
    Matcher matcher = PATH.matcher(req.getServletPath());
    if (!matcher.matches()) {
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
    else {
      resp.setCharacterEncoding("UTF-8");
      resp.setContentType("application/xml");
      visitor.accept(resp.getWriter(),
          scripts.script(matcher.group(1)),
          matcher.group(2));
    }
  }
}
