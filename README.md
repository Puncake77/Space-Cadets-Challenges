# Barebones Interpreter
This weeks challenge is to implement a Bare Bones interpreter. The program should take a text file containing a bare bones program as input and execute each statement in turn. After each statement has been executed it should output the state of all the variables in the system to form a record of execution.

You will almost certainly want to look at the String.split() as a starting point or possibly the java.util.regex package. Think carefully about the internal structure of your interpreter, try and come up with a decent object oriented design. Also beware of the nested while loop!
## Assumptions
The BareBones language is quite underspecified. From http://www.brouhaha.com/~eric/software/barebones/bare_bones_language_summary.html, I can see that in the same book the language was created in, an extension of the language to include variable initialisation and declaration as 'init [VAR_NAME] = [INTEGER];'. I have decided NOT to implement this.

I have, however, decided that the 'end;' command can be used independantly of a while loop for the purpose of denoting the end of the program. I have not made this a requirement, but rather used this for better styled programs.

I have ensured that nested while loops are implemented. Finally, I have enabled the 'copy [VAR1] to [VAR2]' to enable easier swapping of held variable integers, as defined in the book.

## Usage
javac Main.java BBInterpreter.java
java Main.java [FILE_NAME].bb

# Test Files
You will find 2 test files in this branch, the first is a simple counting up and down program in barebones ('Counting.bb').
The second ('MultiplyXY.bb') is a program that multiplies together two integers X and Y. These are increased before the loop starts, with other necessary variables instantiated.
