package org.tipitaka.archive.converter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import org.tipitaka.archive.StandardException;

public class TipitakaOrgTocVisitor {

    public static final String TIPITAKA_ORG = "http://tipitaka.org";
    public static final String TIPITAKA_ORG_MIRROR = "file:../tipitaka-mirror";

    private final XmlPullParserFactory factory;
    private Stack<String> stack;
    private Map<String, String[]> map;
    private final String baseUrl;

    public static TipitakaOrgTocVisitor mirror() throws StandardException {
        return new TipitakaOrgTocVisitor(TIPITAKA_ORG_MIRROR);
    }

    public static TipitakaOrgTocVisitor live() throws StandardException {
        return new TipitakaOrgTocVisitor(TIPITAKA_ORG);
    }

    public TipitakaOrgTocVisitor(String baseUrl) throws StandardException {
        try {
            this.factory = XmlPullParserFactory.newInstance();
        }
        catch(XmlPullParserException e) {
            throw new StandardException("can instantiate pull-parser", e);
        }
        this.baseUrl = baseUrl;
    }

    public Map<String, String[]> map(){
        return map;
    }

    public void accept() throws XmlPullParserException, IOException{
        accept("romn");
    }

    public void accept(String script) throws XmlPullParserException, IOException{
        this.stack = new Stack<String>();
        this.map = new LinkedHashMap<String, String[]>();

        acceptPath(script, "tipitaka_toc.xml");
    }

    protected void acceptPath(String script, String path) throws XmlPullParserException, IOException{
        XmlPullParser xpp = factory.newPullParser();
        URL url = new URL(baseUrl + '/' + script + '/' + path);

        if (!url.toString().endsWith(".toc.xml")) System.out.println("parsing " + url);

        Reader reader = null;
        try {
            reader = new InputStreamReader(url.openStream(), "UTF-16" );
            xpp.setInput( reader );
            accept(script, xpp);
        }
        catch(XmlPullParserException e){
            if( reader != null ){
                reader.close();
            }
            reader = new InputStreamReader(url.openStream(), "UTF-8" );
            xpp.setInput( reader );
            accept(script, xpp);
        }
    }

    private void accept(String script, XmlPullParser xpp) throws XmlPullParserException, IOException{
        int eventType = xpp.getEventType();
        if(eventType == XmlPullParser.END_DOCUMENT) {
            return;
        } else if (eventType == XmlPullParser.START_TAG) {
            visitTree(script, xpp);
        } else if (eventType == XmlPullParser.END_TAG) {
            if(!stack.isEmpty()) stack.pop();
        }
        xpp.next();
        accept(script, xpp);
    }

    private void visitTree(String script, XmlPullParser xpp) throws XmlPullParserException, IOException {
        switch(xpp.getAttributeCount()){
            case 1:
                stack.push(normalize(xpp.getAttributeValue(0)));
                break;
            case 2:
                stack.push(normalize(xpp.getAttributeValue(0)));
                acceptPath(script, xpp.getAttributeValue(1));
                break;
            case 3:
                stack.push(normalize(xpp.getAttributeValue(0)));
                map.put(xpp.getAttributeValue(1), stack.toArray(new String[stack.size()]));
                break;
            default:
                stack.push(null);
        }
    }

    private String normalize(String text) {
        return text.trim().replaceAll("[,.]$", "")
            .replace("Tipiṭaka (Mūla)", "Mūla")
            .replace(" (ṭīkā)", "")
            .replace(" (aṭṭhakathā)", "");
    }
}
