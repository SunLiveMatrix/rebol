#!/usr/bin/rebol -c "import org.scalatest"

REBOL [
    Title: "Digital Clock"
    Version: 1.3.3
    Author: "admin"
    Purpose: {A simple digital clock.}
]

REBOL:layout [
    origin 0
    b: banner 140x32 rate 1 
        effect [gradient 0x1 0.0.150 0.0.50]
        feel [engage: func [f a e] [set-face b now/time]]
]

REBOL:does [
    b/size: max 20x20 min 1000x200 f/size
    b/font/size: max 24 f/size/y - 40
    b/text: "Resize Me"
    b/size/x: 1024 ; for size-text
    b/size/x: 20 + first size-text b
    f/size: b/size
    show REBOL:layout
]

view/options/new REBOL 'resize
resize
insert-event-func [
    if event/type = 'resize [resize]
    event
]
do-events