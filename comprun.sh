# Delete all previously compiled files
if [[ "$@" =~ --del-all ]]; then
    echo -e '\033[0;31mSuppressions des fichiers class...\033[0m'
    sudo rm -rf bin/
fi


# Check if test or main project is going to be compiled
if [[ "$@" =~ --test ]]; then
    echo -e '\033[0;33mCompilation projet test...\033[0m'
    cd src/test/java/
else
    echo -e '\033[0;33mCompilation projet principal...\033[0m'
    cd src/main/java/
fi

# Proceed to compile and launch main
javac -d ../../../bin *.java
cd ../../../bin/
java Main
cd ..