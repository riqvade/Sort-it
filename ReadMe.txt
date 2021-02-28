Pass required arguments to jar:
java -jar target/sort-it-1.0-SNAPSHOT.jar -i -a outfile.txt test1.txt test2.txt test3.txt

Input files must be sorted ascending.
Algorithm stops reading file as soon as found line with incorrect sorting and continues with other files.