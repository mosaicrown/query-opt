#!/bin/bash

#jump into bytecode folder
cd "GeneratedBytecode"
#run project

java -classpath .::../../../../../target/Processing-1-jar-with-dependencies.jar Operation_allocator.Processing.Processor $1 $2 $3 $4 $5 $6

#fylesystem comback
cd ..
