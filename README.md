CSV-Extension
===

This NetLogo extension adds CSV parsing capabilities to models.

## Common use cases and examples

### Read a file all at once

Just use `csv:from-file "/path/to/myfile.csv"`! See [from-file](#from-file) for more information.

### Read a file one line at a time

For really big files, you may not want to store the entire file in memory, but rather just process it a line at a
time. For instance, if you want to sum each of the columns of a numeric CSV file, you can do:

    to-report sum-columns [ file ]
      file-open file
      set result csv:from-line file-read-line
      while [ not file-at-end? ] [
        let row csv:from-line file-read-line
        set result (map [?1 + ?2] result row)
      ]
      file-close
      report result
    end

You can also use this technique to...

### Read a file one line per tick

Here's an example model that reads in a file one line per tick:

    globals [ data ]

    to setup
      clear-all
      file-close-all % Close any files open from last run
      file-open "data.csv"
      % other setup goes here
      reset-ticks
    end

    to go
      if file-at-end? [ stop ]
      set data csv:from-line file-read-line
      % model update goes here
      tick
    end

### Write a file

Just use `csv:to-file "/path/to/myfile.csv" my-data`! See [to-file](#to-file) for more information.

## Primitives

### Reading

#### from-line

`csv:from-line <string>`

`(csv:from-line <string> <delimiter>)`

Parses the given string as though it were a row from a CSV file and returns it as a list of values. For example:

    observer> show csv:csv-row-to-strings "one,two,three"
    observer: ["one" "two" "three"]

Quotes can be used when items contain commas:

    observer> show csv:csv-row-to-strings "there's,a,comma,\"in,here\""
    observer: ["there's" "a" "comma" "in,here"]

You can put two quotes in a row to put an actual quote in an entry. If the entry is not quoted, you can just use one quote:

    observer> foreach (csv:from-line "he said \"hi there\",\"afterwards, she said \"\"hello\"\"\"") print
    he said "hi there"
    afterwards, she said "hello"

Number-like-entries will be parsed as numbers:

    observer> show csv:from-line "1,-2.5,1e3"
    observer: [1 -2.5 1000]

`true` and `false` with any capitalization will be parsed as booleans:

    observer> show csv:from-line "true,TRUE,False,falsE"
    observer: [true true false false]

To use a different delimiter, you can specify a second, optional argument. Only single character delimiters are supported:

    observer> show (csv:csv-row-to-strings "1;2;3" ";")
    observer: [1 2 3]

Different types of values can be mixed freely:

    observer> show csv:from-line "one,2,true"
    observer: ["one" 2 true]

#### from-string

`csv:from-string <string>`

`(csv:from-string <string> <delimiter>)`

Parses a string representation of one or more CSV rows and returns it as a list of lists of values. For example:

    observer> show csv:csv-to-strings "1,two,3\nfour,5,true"
    observer: [[1 "two" 3] ["four" 5 true]]

#### from-file

`csv:from-file <string>`

`(csv:from-file <string> <delimiter)`

Parses an entire CSV file to a list of lists of values. For example, if we have a file `example.csv` that contains:

    1,2,3
    4,5,6
    7,8,9
    10,11,12

Then, we get:

    observer> show csv:file-to-strings "example.csv"
    observer: [[1 2 3] [4 5 6] [7 8 9] [10 11 12]]

The parser doesn't care if the rows have different numbers of items on them. The number of items in the rows list
will always be `<number of delimiters> + 1`, though blank lines are skipped. This makes handling files with headers
quite easy. For instance, if we have `header.csv` that contains:

    My Data
    2/1/2015

    Parameters:
    start,stop,resolution,population,birth?
    0,4,1,100,true

    Data:
    time,x,y
    0,0,0
    1,1,1
    2,4,8
    3,9,27


This gives:

    observer> foreach csv:file-to-strings "header.csv" show
    observer: ["My Data"]
    observer: ["2/1/2015"]
    observer: ["Parameters:"]
    observer: ["start" "stop" "resolution" "population" "birth?"]
    observer: [0 4 1 100 true]
    observer: ["Data:"]
    observer: ["time" "x" "y"]
    observer: [0 0 0]
    observer: [1 1 1]
    observer: [2 4 8]
    observer: [3 9 27]

### Writing

#### to-line

`csv:to-line <list>`

`(csv:to-line <list> <delimiter>)`

Reports the given list as a CSV row. For example:

    observer> show csv:to-line ["one" 2 true]
    observer: "one,2,true"

#### to-string

`csv:to-string <list>`

`(csv:to-string <list> <delimiter>)`

Reports the given list of lists as a CSV string. For example:

    observer> show csv:to-string [[1 "two" 3] [4 5]]
    observer: "1,two,3\n4,5"

#### to-file

`csv:to-file <string> <list>`

`(csv:to-file <string> <list> <delimiter>)`

Writes the given list of lists to a new CSV file. For example:

    observer> csv:to-file "myfile.csv" [[1 "two" 3] [4 5]]

will result in a file `myfile.csv` containing:

    1,two,3
    4,5
