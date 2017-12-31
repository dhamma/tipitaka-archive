# tipitaka-archive

deliver tipitaka.org contents in a different way:

* browsable without javascript
* for search engine indexable
* referencable archive, i.e. all pages have a nice url, you have an anchor on each line/paragraph which can be used to share you finding with others.
* delivers content as xml and json - can be used by webservices via CORS or directly for mobile apps
* each page downloaded carries enought context information that you can find the page either here or even on tipitaka.org

# API on local filesystem

* all files are encoded in UTF-8
* start point is: [archive/api](../archive/api)

## Common Context inside all Files

  * path - null means root, all others are relative to the root. the path is using only ascii characters.
  * baseUrl - on local filesystem this is 'file:' but from an online source
              the respective base-url will be there
  * version - null mean tiptiaka.org version. this has no real meaning for the ilfe itself but can be used to consstructed versioned url for new links. this allows to keep navigation within the version selected by user without parsing http parameters.
  * titlePath - reversed path names in roman script, can be used for page title.
  * menus - these are all menus from the root menu until the one for this directory/document

## Directory and their Files

Each directory has an json with its context, i.e.
  * [archive/api/roman](../archive/api/roman) has [archive/api/roman.json](../archive/api/roman.json)
  * [archive/api/roman/mula](../archive/api/roman/mula) has [archive/api/roman/mula.json](../archive/api/roman/mula.json)
  * ...

The directory files only contains the common context described above.

## Document Files

Each document carries the same context as all directories do and add a few extra info to it
  * normativeSource - the path on tipitaka.org to find the original source for his document
  * source - the xml source of the document. xml is much better to markup textual information then json.
  * versions - all possible version variants for the document

The XML document has the actual document embedded add the under the `document` tag.

## Note on Directory Structure from Tipitaka.org

The original directory structure from tipitaka.org has duplicate names which is not possible when storing on filesystem or mapping to an url. In case there are duplicates the second one got the postfix ` _2_` added to the name or the ` _3_` for third 'duplicate'. In some cases in could be resolved by adding another sub-directory. This subdirectory can happen sometime in the future.

# old stuff might be obsolete

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
