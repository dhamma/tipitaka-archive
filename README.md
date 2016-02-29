# tipitaka-archive

deliver tipitaka.org contents in a different way:

* browsable without javascript
* for search engine indexable
* referencable archive, i.e. all pages have a nice url, you have an anchor on each line/paragraph which can be used to share you finding with others.
* delivers also content as xml and json - can be used by webservices via CORS or directly for mobile apps
* each page downloaded carries enought context information that you can find the page only either here or even on tipitaka.org

## first phase

**done** and live on http://tipitaka.de

* use the sources from http://www.tipitaka.org/romn/cscd to generate html files
* organize the files in a hierachical structure as found in http://www.tipitaka.org/romn/toc1.xml, http://www.tipitaka.org/romn/toc2.xml, http://www.tipitaka.org/romn/toc3.xml, http://www.tipitaka.org/romn/toc4.xml and follow its tree structure
* all pages do have anchors on each *line* which appears on mouse hovering over that line and can be linked at - just click the line number to get the link in your browser location.
* all pages do set clickable anchors for numbered paragraphs.


## second phase

**pending**

* produce a simple xml format from the http://www.tipitaka.org/romn/cscd sources and store them in this git repository. same with the directory listing as *index.xml*
* ensure the round trip tipitaka.org-format -> xml-format -> tipitaka.org-format produces the same files
* deliver the content as html in the same format as the website of the first phase
* deliver additional formats like the xml or json or the original tipitaka.org tei-format and a printerfriendly html view
* all stored xml will use UTF-8 encoding so all those wonderfull commandline text processing tools under linux and macos just work

## third phase

**pending**

* for tipitaka.org the devanagari files are normative. use those files to produce roman script files and offer all files for both roman and devanagari scripts.
* ensure the round trip: devanagari-tei-format -> roman-xml-format -> devanagari-xml-format -> devenagari-tei-format
* encode alternative snippets in xml - not sure how to 
* see if the round trip ending in roman-tei-format matches as well: devanagari-tei-format -> roman-xml-format -> devanagari-xml-format -> roman-tei-format
* see if the references like [saṃ. ni. 1.35] or [dha. pa. 307 dhammapadepi] etc can be resolved with proper reference on the file level

# running a simple server for testing

```
cd src/main/webap
bash httpd.sh
```

this will deliver the archive on http://localhost:8888/
