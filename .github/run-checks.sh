#!/bin/sh
# Runs argument-free repository shell checks; Pages runs the website checker with its site path.

dir=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd) &&
ret=0 &&
for checkscript in "$dir"/check-*.sh; do
    if ! "$checkscript"; then
        ret=1
    fi
done
exit $ret
