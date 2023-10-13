# Delete all previously compiled files
if ("--del-all" -in $args) {
    Write-Host "Suppressions des fichiers class..." -ForegroundColor "Red"
    del bin/* -Recurse
}

# Check if test or main project is going to be compiled
if ("--test" -in $args) {
    Write-Host "Compilation projet test..." -ForegroundColor "Yellow"
    cd src/test/java
} else {
    Write-Host "Compilation projet principal..." -ForegroundColor "Yellow"
    cd src/main/java
}

# Proceed to compile and launch main
javac -d ../../../bin *.java
cd ../../../bin
java Main
cd ..