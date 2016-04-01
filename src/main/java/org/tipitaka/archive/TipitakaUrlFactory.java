/**
 * 
 */
package org.tipitaka.archive;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class TipitakaUrlFactory
{

    public static final String TIPITAKA_ORG = "http://tipitaka.org";

    private final String baseUrl;

    public TipitakaUrlFactory(){
        this((URL) null);
    }
    
    public TipitakaUrlFactory(File mirrorUrl){
        this(mirrorUrl == null ? null : mirrorUrl.toURI().toString());
    }

    public TipitakaUrlFactory(URL url){
        this(url == null ? TIPITAKA_ORG : url.toString());
    }

    public TipitakaUrlFactory(String url){
        baseUrl  = url;
    }

    public URL sourceURL(Script script, String path) throws MalformedURLException {
        return new URL(baseUrl + "/" + script.tipitakaOrgName + "/" + path);
    }

    public URL normativeURL(Script script, String path)  throws MalformedURLException {
        return new URL(TIPITAKA_ORG + "/" + script.tipitakaOrgName + "/" + path);
    }
    
}