# Barebones Interpreter Extended
This weeks challenge is to extend the Bare Bones interpreter.

## Assumptions & Changes
I have now implemented explicitly initialized varaibles `init [VAR_NAME] = [VALUE];`.

I have also added if & else statements, whilst omitting 'else if's. The syntax is:
```
if [VAR] [COMPARISON_OPERATOR] [VAR|VALUE] then;
[CODE]
...
(else;)
([CODE])
endif;
```

I cleaned up my code quite a bit including the structure of the BBInterpreter class, adding more comments and ensuring the code's formatting matches Google's Java Code Style more accurately.

All other implementations from the older version has been kept such as nested and unnested while loops, copy to etc.

## Usage
javac Main.java BBInterpreter.java

java Main.java `[FILE_NAME].bb`

# Test Files
You will find 3 test files in this branch, two of which are the test files supplied in last weeks challenge. I have coded my own barebones program to test my if statements functionality. The 'bool' variables indicate a successful test if the value is 'true' (1). `IfTesting.bb`


