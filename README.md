CSV-Extension
===

This NetLogo extension adds CSV parsing capabilities to model.

## Primitives

### Reading

#### from-string

`csv:from-string` _string_

Parses the given string as though it were a row from a CSV file and returns it as a list of strings. For example:

    observer> show csv:from-string "one,two,three"
    observer: ["one" "two" "three"]

Quotes can be used when items contain commas:

    observer> show csv:from-string "there's,a,comma,\"in,here\""
    observer: ["there's" "a" "comma" "in,here"]

You can put two quotes in a row to put an actual quote in an entry. If the entry is not quoted, you can just use one quote:

    observer> foreach (csv:from-string "he said \"hi there\",\"afterwards, she said \"\"hello\"\"\"") print
    he said "hi there"
    afterwards, she said "hello"

The list will keep all items as strings:

    observer> show csv:from-string "1,2,3"
    observer: ["1" "2" "3"]

You can use `map` and `read-from-string` to convert NetLogo literals into actual literals:

    observer> show map read-from-string csv:from-string "1,2,3"
    observer: [1 2 3]
    observer> show map read-from-string csv:from-string "\"1\",\"\"\"one\"\"\""
    observer: [1 "one"]

By combining this with NetLogo [file reading primitive](http://ccl.northwestern.edu/netlogo/docs/dictionary.html#fileiogroup), you can read a data file easily. Consider the following data file:

    time,x,y
    0,0,0
    1,1,1
    2,4,0
    3,9,1
    4,16,0

This could be read with the following reporter:

    to-report read-data [ file ]
      file-open file
      let header csv:from-string file-read-line
      let data []
      while [ not file-at-end? ] [
        let row map read-from-string csv:from-string file-read-line
        set data lput row data
      ]
      report fput header data
    end
