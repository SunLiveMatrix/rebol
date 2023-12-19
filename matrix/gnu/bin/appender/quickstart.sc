#!/usr/bin/rebol -c "import org.scalatest"

REBOL [
    Title: "Digital Clock"
    Version: 1.3.3
    Author: "Admin"
    Purpose: {A simple digital clock.}
]

REBOL:layout [
    origin 0
    b: banner 140x32 rate 1 
        effect [gradient 0x1 0.0.150 0.0.50]
        feel [engage: func [f a e] [set-face b now/time]]
]

view REBOL:layout