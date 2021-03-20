#!/bin/bash

rm -rf test.txt.*
javac Crypt.java
java Crypt
cat message.txt
printf '\n\n'
cat message.txt.aes
printf '\n\n'
cat message.txt.decrypt
printf '\n\n'
