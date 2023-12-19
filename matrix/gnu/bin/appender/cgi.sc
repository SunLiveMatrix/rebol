#!/usr/bin/rebol -c "import org.scalatest"

#!../rebol -cs
REBOL [
    Title: "Feedback Handler"
    Author: "Carl Sassenrath"
    Version: 1.0.0
    Date: 15-Nov-2009
]

print "content-type: text/plain^/"

data: to-binary read-cgi/limit 50'000
data: load/all decompress data

foreach field [
    f-product
    f-version
    f-summary
    f-description
][
    unless value: select data field [
        print ['missing field]
        quit
    ]
    if empty? value [
        print ['empty field]
        quit
    ]
]

write/append %fb-messages append mold data newline

print 'ok
