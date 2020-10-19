package com.jwjo1g20;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class BBInterpreter {

    //Properties
    final private String[] PROGRAM;
    final private String[] RESERVED = new String[] {"clear", "copy", "decr", "do", "end", "incr", "init", "not", "to", "while"};
    private int programCounter;
    private boolean skipTillEnd;
    private int noToSkip;
    private HashMap<String, Integer> variables = new HashMap<String, Integer>();
    //private LinkedList<Integer> whileLoopPointers = new LinkedList<Integer>();
    private HashMap<Integer, Integer> whileLoopPointers = new HashMap<Integer, Integer>();

    //Constructor
    public BBInterpreter(String[] inProgram) {
        PROGRAM = inProgram;
        programCounter = 0;
        skipTillEnd = false;
        noToSkip = 0;
    }

    //Methods
    public void outputRecordOfExecution() {
        if (programCounter + 1 == PROGRAM.length && whileLoopPointers.size() == 0) {
            System.out.println("-".repeat(15));
            System.out.println("~~~ Program has finished successfully! ~~~");
            System.out.println("-".repeat(15));
        } else {
            System.out.println("-".repeat(15));
            System.out.println("Line " + (programCounter + 1) + " has finished executing. Variable list:");
            variables.forEach((key, value) -> System.out.println(key + ":\t" + value));
        }

    }

    public void stepThrough() {
        String[] command = PROGRAM[programCounter].split(" ");

        if (skipTillEnd) {
            if (!(command[0].toLowerCase().equals("while") || command[0].toLowerCase().equals("end"))) {
                programCounter++;
                this.stepThrough();

            } else if (command[0].toLowerCase().equals("while")) {
                noToSkip++;
                programCounter++;
                this.stepThrough();

            }
        }

        switch (command[0].toLowerCase()) {
            case "init":
                /*These words are case-insensitive and reserved (cannot be used as identifiers):
                  clear, copy, decr, do, end, incr, init, not, to, while
                */
                if (Arrays.asList(RESERVED).contains(command[1].toLowerCase())) {
                    System.out.println("Error on line " + (programCounter + 1) + ": Attempt to initialise var with reserved keyword '" + command[1].toLowerCase() + "'");
                    System.exit(1);

                } else {
                    variables.put(command[1], 0);
                }
                break;

            case "clear":
                if (!variables.containsKey(command[1])) {
                    System.out.println("Error on line " + (programCounter + 1) + ": Attempt to clear a var that has not been initialised '" + command[1] + "'");
                    System.exit(1);

                } else {
                    variables.replace(command[1], 0);
                }
                break;

            case "incr":
                if (!variables.containsKey(command[1])) {
                    System.out.println("Error on line " + (programCounter + 1) + ": Attempt to incr a var that has not been initialised '" + command[1] + "'");
                    System.exit(1);

                } else {
                    variables.replace(command[1], variables.get(command[1]) + 1);
                }
                break;

            case "decr":
                if (!variables.containsKey(command[1])) {
                    System.out.println("Error on line " + (programCounter + 1) + ": Attempt to decr a var that has not been initialised '" + command[1] + "'");
                    System.exit(1);

                } else {
                    variables.replace(command[1], variables.get(command[1]) - 1);
                }
                break;

            case "copy":
                if (!command[2].toLowerCase().equals("to")) {
                    System.out.println("Error on line " + (programCounter + 1) + ": Attempt to copy without correct syntaxing - 'to' was absent from args");
                    System.exit(1);

                } else if (!(variables.containsKey(command[1]) && variables.containsKey(command[2]))) {
                    System.out.println("Error on line " + (programCounter + 1) + ": Attempt to copy with variables that have not been initialised");
                    System.exit(1);

                } else {
                    variables.replace(command[3], variables.get(command[1]));
                }
                break;

            case "while":
                if (!(command[2].equals("not") && command[4].equals("do"))) {
                    System.out.println("Error on line " + (programCounter + 1) + ": Invalid Syntaxing - use 'while [VAR] not [VAL] do;'");
                    System.exit(1);

                } else if (!variables.containsKey(command[1])) {
                    System.out.println("Error on line " + (programCounter + 1) + ": Attempt to start while loop with un-initialised var");
                    System.exit(1);

                } else {
                    try {
                        Integer.parseInt(command[3]);

                    } catch (NumberFormatException e) {
                        System.out.println("Error on line " + (programCounter + 1) + ": Target VAL for VAR in while is not an Integer");
                        System.exit(1);

                    }

                    if (!(variables.get(command[1]) == Integer.parseInt(command[3]))) {
                        //Continue with While
                        whileLoopPointers.putIfAbsent(programCounter, -1);

                    } else {
                        //Terminate While
                        if (whileLoopPointers.containsKey(programCounter)) {
                            int copyBuffer;
                            copyBuffer = whileLoopPointers.get(programCounter);
                            whileLoopPointers.remove(programCounter);
                            this.outputRecordOfExecution();
                            programCounter = copyBuffer;
                            skipTillEnd = true;
                            noToSkip = 1;
                            this.stepThrough();
                            return;
                        } else {
                            skipTillEnd = true;
                            noToSkip++;
                        }
                    }
                }
                break;

            case "end":
                if (skipTillEnd) {
                    //A while loop that has never been executed / one that is terminating is handled here.
                    noToSkip--;

                    if (noToSkip == 0 && programCounter == (PROGRAM.length - 1)) {
                        //Program ending point
                        this.outputRecordOfExecution();
                        return;
                    } else if (noToSkip == 0) {
                        //Skipped enough times, continue normally
                        skipTillEnd = false;
                    }
                    break;
                }

                if (!whileLoopPointers.isEmpty()) {
                    //While loop point exists, see if any have the current PC as their value

                    if (whileLoopPointers.containsValue(programCounter)) {
                        AtomicBoolean found = new AtomicBoolean(false);
                        whileLoopPointers.forEach((key, value) -> {
                            if (value == programCounter) {
                                found.set(true);
                                this.outputRecordOfExecution();
                                programCounter = key;
                            }
                        });
                        if (found.get()) {
                            this.stepThrough();
                            return;
                        }

                    } else {
                        //Links the 'last' unlinked while loop with this end statement
                        AtomicInteger latestUnlinkedWhilePointer = new AtomicInteger();
                        whileLoopPointers.forEach((key, value) -> {
                            if (key > latestUnlinkedWhilePointer.get()) {
                                latestUnlinkedWhilePointer.set(key);
                            }
                        });

                        AtomicBoolean found = new AtomicBoolean(false);
                        whileLoopPointers.forEach((key, value) -> {
                            if (key == latestUnlinkedWhilePointer.get() && value == -1) {
                                found.set(true);
                                whileLoopPointers.replace(key, -1, programCounter);
                                this.outputRecordOfExecution();
                                programCounter = key;

                            }
                        });
                        if (found.get()) {
                            this.stepThrough();
                            return;
                        }
                    }

                } else if (programCounter == (PROGRAM.length - 1)) {
                    //Program ending point
                    this.outputRecordOfExecution();
                    return;
                } else {
                    System.out.println("Error on line " + (programCounter + 1) + ": Invalid End - 'end' was not ending a while loop, nor the end of a program");
                    System.exit(1);
                }
                break;
            default:
                System.out.println("Error on line " + (programCounter + 1) + ": Command '" + command[0] + "' was not a valid command");
                System.exit(1);
                break;
        }
        this.outputRecordOfExecution();
        programCounter += 1;
        this.stepThrough();
    }
}