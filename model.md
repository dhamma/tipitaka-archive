Base {
 "id"      : String
 "path"    : String
}

ExtendedBase < Base {
 "script"  : Enum[roman]
 "version" : String
 "baseUrl" : String
 "titlePath: Array[String]
}

Document < Folder {
  "normativeSource" : String
  "xmlSource"       : String
  "versions"        : Array[String]
}

Menu < Base {
  "items": HashMap
}

Folder < ExtendedBase {
  "menus" : Array[Menu]
}
