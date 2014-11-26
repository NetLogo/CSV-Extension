CSV-Extension
===

This NetLogo extension adds CSV parsing capabilities to model.

## Primitives

### Reading

#### to-strings

`csv:to-strings` _string_

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

#### to-strings-and-numbers

`csv:to-strings-and-numbers` _string_

Parses the given string as though it were a row froma CSV file and returns it as a list of strings and numbers. This is exactly like `csv:to-strings`, but any number-like entries are converted to numbers. Anything that can't be turned into a number is left as a string. For example:

    observer> show csv:to-strings-and-numbers "1,one,\"2\",1.5e10"
    observer: [1 "one" 2 15000000000]
