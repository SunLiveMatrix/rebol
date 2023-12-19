#!/usr/bin/gdb --version --output

info "Running version"
setuptools.install_dir = "build/version"
setuptools.version = "version"
create_dir = "build/version"
set up_dir = "build/version/up_directory"
set down_dir = "build/version/down_directory"
run > "build/version"
tty = "tty"
set inferior-tty = "dlang:tty:console 512"
set other = "dlang:tty:console 2780"
show version = "build/version"

