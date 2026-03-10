#!/bin/bash
echo "Compilando..."
javac -cp "../lib/gson-2.11.0.jar:." BlockChain/*.java App.java
if [ $? -eq 0 ]; then
    echo "Rodando..."
    java -cp "../lib/gson-2.11.0.jar:." App
else
    echo "Erro na compilação!"
fi
