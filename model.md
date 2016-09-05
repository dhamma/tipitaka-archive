Base {
 "script":"Enum[roman]"
 "path":"String"
}

ExtendedBase < Base {
  "titlePath":"String"
}

Document < ExtendedBase {
  "normativeSource":"String"
  "xmlSource":"String"
  "versions":"Array[String]"
}

Menu < Base {
  "hasLeaves":"Boolean"
  "items":"Hash"
}

ToC < ExtendedBase {
  "version":"String"
  "items":"Hash"
  "menus":"Array[Menu]"
}

jsonapi ToC

```
{
  "data": [{
    "type": "toc",
    "id": "/roman/tipitaka (mula)/vinayapitaka/parajikapali",
    "attributes": {
      "script":"roman",
      "path": "/tipitaka (mula)/vinayapitaka/parajikapali",
      "titlePath":["Pārājikapāḷi","Vinayapiṭaka","Tipiṭaka (Mūla)"], 
      "hasLeaves": true,
      "items": {
	"/roman/tipitaka (mula)/vinayapitaka/parajikapali/veranjakandam": "Verañjakaṇḍaṃ",
	"/roman/tipitaka (mula)/vinayapitaka/parajikapali/1. parajikakandam.html": "1. Pārājikakaṇḍaṃ",
	"/roman/tipitaka (mula)/vinayapitaka/parajikapali/2. sanghadisesakandam": "2. Saṅghādisesakaṇḍaṃ",
 	"/roman/tipitaka (mula)/vinayapitaka/parajikapali/3. aniyatakandam", "3. Aniyatakaṇḍaṃ",
	"/roman/tipitaka (mula)/vinayapitaka/parajikapali/4. nissaggiyakandam": "4. Nissaggiyakaṇḍaṃ"
      }
    },
    "relationships": {
      "menus": {
	  { "type": "menu", "id": "/roman" },
          { "type": "menu", "id": "/roman/tipitaka (mula)" },
          { "type": "menu", "id": "/roman/tipitaka (mula)/vinayapitaka" }
	}]
      }
    },
    "links": {
      "self": "http://example.com/roman/tipitaka (mula)/vinayapitaka/parajikapali.json"
      "json":  "http://example.com/roman/tipitaka (mula)/vinayapitaka/parajikapali.json"
      "html":  "http://example.com/roman/tipitaka (mula)/vinayapitaka/parajikapali.html"
      "xml":  "http://example.com/roman/tipitaka (mula)/vinayapitaka/parajikapali.xml"
    }
  }],
  "included": [{
    "type": "menu",
    "id": "/roman",
    "attributes": {
      "script": "roman",
      "path": null,
      "hasLeaves": false,
      "items": {
         "/roman/tipitaka (mula)": "Tipiṭaka (Mūla)",
	 "/roman/atthakatha": "Aṭṭhakathā",
         "/roman/tika": "Tīkā",
         "/roman/anya": "Anya"
      }
    },
    "links": {
      "self": "http://example.com/roman.json"
      "json":  "http://example.com/roman.json"
      "html":  "http://example.com/roman.html"
      "xml":  "http://example.com/roman.xml"
    }
  },
  {
    "type": "menu",
    "id": "/roman/tipitaka (mula)",
    "attributes": {
      "script": "roman",
      "path": "/tipitaka (mula)",
      "hasLeaves": false,
      "items": {
        "/roman/tipitaka (mula)/vinayapitaka": "Vinayapiṭaka",
	"/roman/tipitaka (mula)/suttapitaka": "Suttapiṭaka",
	"/roman/tipitaka (mula)/abhidhammapitaka": "Abhidhammapiṭaka"
      }
    },
    "links": {
      "self": "http://example.com/roman/tipitaka (mula).json"
      "json":  "http://example.com/roman/tipitaka (mula).json"
      "html":  "http://example.com/roman/tipitaka (mula).html"
      "xml":  "http://example.com/roman/tipitaka (mula).xml"
    }
  }]
}
```

jsonapi Document
```
  "data": {
    "type": "document",
    "id": "/roman/tipitaka (mula)/vinayapitaka/parajikapali/veranjakandam",
    "attributes": {
      "normativeSource":"http://tipitaka.org/romn/cscd/vin01m.mul0.xml",
      "source":"roman/tipitaka (mula)/vinayapitaka/parajikapali/veranjakandam.xml",
      "script":"roman",
      "path":"/tipitaka (mula)/vinayapitaka/parajikapali/veranjakandam",
      "titlePath":["Verañjakaṇḍaṃ","Pārājikapāḷi","Vinayapiṭaka","Tipiṭaka (Mūla)"],
      "items": {
	"/roman/tipitaka (mula)/vinayapitaka/parajikapali/veranjakandam": "Verañjakaṇḍaṃ",
	"/roman/tipitaka (mula)/vinayapitaka/parajikapali/1. parajikakandam.html": "1. Pārājikakaṇḍaṃ",
	"/roman/tipitaka (mula)/vinayapitaka/parajikapali/2. sanghadisesakandam": "2. Saṅghādisesakaṇḍaṃ",
 	"/roman/tipitaka (mula)/vinayapitaka/parajikapali/3. aniyatakandam", "3. Aniyatakaṇḍaṃ",
	"/roman/tipitaka (mula)/vinayapitaka/parajikapali/4. nissaggiyakandam": "4. Nissaggiyakaṇḍaṃ"
      }
    },
    "relationships": {
      "menus": [
	  { "type": "menu", "id": "/roman" },
          { "type": "menu", "id": "/roman/tipitaka (mula)" },
          { "type": "menu", "id": "/roman/tipitaka (mula)/vinayapitaka" }
	]
      }
    },
    "links": {
      "self": "http://example.com/roman/tipitaka (mula)/vinayapitaka/parajikapali.json"
      "json":  "http://example.com/roman/tipitaka (mula)/vinayapitaka/parajikapali.json"
      "html":  "http://example.com/roman/tipitaka (mula)/vinayapitaka/parajikapali.html"
      "xml":  "http://example.com/roman/tipitaka (mula)/vinayapitaka/parajikapali.xml"
    }
  }
}
```