#!/bin/bash

echo $1

hash=`echo $1 | sed -e 's/.war$//' -e 's/^.*-//'`

git checkout $hash

source=`echo $1 | sed -e 's/^.*\///'`
target=`echo $1 | sed -e 's/archive-.*.war$/archive.war/'`

rm -f $target
ln -s $source $target
