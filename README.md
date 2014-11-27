CSV-Extension
===

This NetLogo extension adds CSV parsing capabilities to models.

## Primitives

### Reading

#### to-strings

`csv:to-strings <string>`

`(csv:to-strings <string> <delimiter>)`

Parses the given string as though it were a row from a CSV file and returns it as a list of strings. For example:

    observer> show csv:to-strings "one,two,three"
    observer: ["one" "two" "three"]

Quotes can be used when items contain commas:

    observer> show csv:to-strings "there's,a,comma,\"in,here\""
    observer: ["there's" "a" "comma" "in,here"]

You can put two quotes in a row to put an actual quote in an entry. If the entry is not quoted, you can just use one quote:

    observer> foreach (csv:to-strings "he said \"hi there\",\"afterwards, she said \"\"hello\"\"\"") print
    he said "hi there"
    afterwards, she said "hello"

The list will keep all items as strings:

    observer> show csv:to-strings "1,2,3"
    observer: ["1" "2" "3"]

You can use `map` and `read-from-string` to convert NetLogo literals into actual literals. This technique is only useful if you're dealing with actual NetLogo literals. [`csv:to-string-and-numbers`](#to-strings-and-numbers) should cover most use cases:

    observer> show map read-from-string csv:from-string "1,2,3"
    observer: [1 2 3]
    observer> show map read-from-string csv:from-string "\"1\",\"\"\"one\"\"\""
    observer: [1 "one"]

To use a different delimiter, you can specify a second, optional argument. Only single character delimiters are supported:

    observer> show (csv:to-strings "1;2;3" ";")
    observer: ["1" "2" "3"]

#### to-strings-and-numbers

`csv:to-strings-and-numbers <string>`

`(csv:to-strings-and-numbers <string> <delimiter>)`

Parses the given string as though it were a row from a CSV file and returns it as a list of strings and numbers. This is exactly like `csv:to-strings`, but any number-like entries are converted to numbers. Anything that can't be turned into a number is left as a string. For example:

    observer> show csv:to-strings-and-numbers "1,one,\"2\",1.5e10"
    observer: [1 "one" 2 15000000000]

To use a different delimiter, you can specify a second, optional argument. Only single character delimiters are supported:

    observer> show (csv:to-strings-and-numbers "1;2;3" ";")
    observer: [1 2 3]

### Writing

#### from-list-to-string

`csv:from-list-to-string <list>`

`(csv:from-list-to-string <list> <delimiter>)`

Creates a CSV representation of the given list.

#### from-list-to-readable-string

`csv:from-list-to-readable-string <list>`

`(csv:from-list-to-string <list> <delimiter>)`

Creates a CSV representation of the given list. The elements of the CSV are represented such that they may be read with `read-from-string`. This is similar to how BehaviorSpace formats CSV.

## Reading a file

You can combine the functions here with NetLogo's [file handling primitives](http://ccl.northwestern.edu/netlogo/docs/dictionary.html#fileiogroup) to read in CSV files. For example, the following reporter will parse most data files out there:

    to-report read-csv-file [ file ]
      let rows []
      file-open file
      while [ not file-at-end? ] [
        set rows lput (csv:to-strings-and-numbers file-read-line) rows
      ]
      file-close
      report rows
    end
