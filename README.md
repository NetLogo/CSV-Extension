CSV-Extension
===

This NetLogo extension adds CSV parsing capabilities to models.

## Common use cases and examples

### Read a file all at once

Just use `csv:file-to-strings-and-numbers "/path/to/myfile.csv"! See [file-to-strings](#file-to-strings) and
[file-to-strings-and-numbers](#file-to-strings-and-numbers) for more information.

### Read a file one line at a time

For really big files, you may not want to store the entire file in memory, but rather just process it a line at a
time. For instance, if you want to sum each of the columns of a numeric CSV file, you can do:

    to-report sum-columns [ file ]
      file-open file
      set result csv:csv-row-to-numbers-and-strings file-read-line
      while [ not file-at-end? ] [
        let row csv:csv-row-to-numbers-and-strings
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
      set data csv:csv-row-to-numbers-and-strings file-read-line
      % model update goes here
      tick
    end

## Primitives

### Reading

#### csv-row-to-strings

`csv:csv-row-to-strings <string>`

`(csv:csv-row-to-strings <string> <delimiter>)`

Parses the given string as though it were a row from a CSV file and returns it as a list of strings. For example:

    observer> show csv:csv-row-to-strings "one,two,three"
    observer: ["one" "two" "three"]

Quotes can be used when items contain commas:

    observer> show csv:csv-row-to-strings "there's,a,comma,\"in,here\""
    observer: ["there's" "a" "comma" "in,here"]

You can put two quotes in a row to put an actual quote in an entry. If the entry is not quoted, you can just use one quote:

    observer> foreach (csv:csv-row-to-strings "he said \"hi there\",\"afterwards, she said \"\"hello\"\"\"") print
    he said "hi there"
    afterwards, she said "hello"

The list will keep all items as strings:

    observer> show csv:csv-row-to-strings "1,2,3"
    observer: ["1" "2" "3"]

You can use `map` and `read-from-string` to convert NetLogo literals into actual literals. This technique is only useful if you're dealing with actual NetLogo literals. [`csv:to-string-and-numbers`](#row-to-strings-and-numbers) should cover most use cases:

    observer> show map read-from-string csv:from-string "1,2,3"
    observer: [1 2 3]
    observer> show map read-from-string csv:from-string "\"1\",\"\"\"one\"\"\""
    observer: [1 "one"]

To use a different delimiter, you can specify a second, optional argument. Only single character delimiters are supported:

    observer> show (csv:csv-row-to-strings "1;2;3" ";")
    observer: ["1" "2" "3"]

#### csv-row-to-strings-and-numbers

`csv:csv-row-to-strings-and-numbers <string>`

`(csv:csv-row-to-strings-and-numbers <string> <delimiter>)`

Parses the given string as though it were a row from a CSV file and returns it as a list of strings and numbers. This is exactly like `csv:csv-row-to-strings`, but any number-like entries are converted to numbers. Anything that can't be turned into a number is left as a string. For example:

    observer> show csv:csv-row-to-strings-and-numbers "1,one,\"2\",1.5e10"
    observer: [1 "one" 2 15000000000]

To use a different delimiter, you can specify a second, optional argument. Only single character delimiters are supported:

    observer> show (csv:csv-row-to-strings-and-numbers "1;2;3" ";")
    observer: [1 2 3]

#### csv-to-strings

`csv:csv-to-strings <string>`

`(csv:csv-to-strings <string> <delimiter>)`

Parses a string representation of one or more CSV rows and returns it as a list of lists of strings. For example:

    observer> show csv:csv-to-strings "1,two,3\nfour,5,six"
    observer: [["1" "two" "3"] ["four" "5" "six"]]

#### csv-to-strings-and-numbers

`csv:csv-to-strings-and-numbers <string>`

`(csv:csv-to-strings-and-numbers <string> <delimiter>)`

Like `csv:csv-to-strings`, but will parse number-like items as numbers. For example:

    observer> show csv:csv-to-strings-and-numbers "1,two,3\nfour,5,six"
    observer: [[1 "two" 3] ["four" 5 "six"]]

#### file-to-strings

`csv:file-to-strings <string>`

`(csv:file-to-strings <string> <delimiter)`

Parses an entire CSV file to a list of lists of strings. For example, if we have a file `example.csv` that contains:

    1,2,3
    4,5,6
    7,8,9
    10,11,12

Then, we get:

    observer> show csv:file-to-strings "example.csv"
    observer: [["1" "2" "3"] ["4" "5" "6"] ["7" "8" "9"] ["10" "11" "12"]]

The parser doesn't care if the rows have different numbers of items on them. The number of items in the rows list
will always be `<number of delimiters> + 1`, though blank lines are skipped. This makes handling files with headers
quite easy. For instance, if we have `header.csv` that contains:

    My Data
    2/1/2015

    Parameters:
    start,stop,resolution,population
    0,4,1,100

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
    observer: ["start" "stop" "resolution" "population"]
    observer: ["0" "4" "1" "100"]
    observer: ["Data:"]
    observer: ["time" "x" "y"]
    observer: ["0" "0" "0"]
    observer: ["1" "1" "1"]
    observer: ["2" "4" "8"]
    observer: ["3" "9" "27"]


#### file-to-strings-and-numbers

`csv:file-to-strings-and-numbers <string>`

`(csv:file-to-strings-and-numbers <string> <delimiter)`

Exactly like `csv:file-to-strings`, except that number-like items are parsed as numbers.

### Writing

#### list-to-csv-row

`csv:list-to-csv-row <list>`

`(csv:list-to-csv-row <list> <delimiter>)`

Creates a CSV representation of the given list.

#### list-to-readable-csv-row

`csv:list-to-readable-csv-row <list>`

`(csv:list-to-csv-row <list> <delimiter>)`

Creates a CSV representation of the given list. The elements of the CSV are represented such that they may be read with `read-from-csv-row`. This is similar to how BehaviorSpace formats CSV.

