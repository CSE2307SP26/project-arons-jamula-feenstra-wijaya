#!/bin/bash
echo "Compiling..."
mkdir -p bin
javac -d bin src/main/*.java

echo "Running..."
java -cp bin main.BankApplication