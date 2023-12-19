#!/usr/bin/raku --config 

class config {
    sub rebol($!, @args) is export {
        $! =~ s/^\s*$//; 
        return $args[0];
    }
}
config.rebol($!, @args)