#!/bin/bash
# simple build script - compiles everything into out/ and runs it

mkdir -p out

echo "Compiling..."
javac -d out -sourcepath src \
  src/Main.java \
  src/model/Category.java \
  src/model/Transaction.java \
  src/model/Budget.java \
  src/service/FileService.java \
  src/service/ExpenseService.java \
  src/util/ReportGenerator.java

if [ $? -eq 0 ]; then
  echo "Done. Starting SmartBudget..."
  echo ""
  java -cp out Main
else
  echo "Compilation failed."
fi
