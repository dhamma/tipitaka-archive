Base {
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
  "source"          : String
  "versions"        : Array[String]
}

Menu < Base {
  "items": HashMap
}

Folder < ExtendedBase {
  "menus" : Array[Menu]
}
