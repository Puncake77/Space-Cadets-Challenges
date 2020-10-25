package com.jwjo1g20;

import javax.lang.model.element.VariableElement;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class BBInterpreter {

  // Constant Properties
  private final String[] PROGRAM;
  private final String[] COMPARISONOPS = new String[] {"==", ">=", "<=", ">", "<", "<>"};
  private final String[] RESERVED =
      new String[] {
        "clear", "copy", "decr", "do", "end", "endif", "else", "if", "incr", "init", "not", "then",
        "to", "while"
      };
  private final Map<String, Integer> VARIABLES = new HashMap<>();
  private final Map<Integer, Integer> WHILELOOPPOINTERS = new HashMap<>();
  //Properties
  private int programCounter;
  private boolean needsReturn;
  private boolean skippingFailedIf;
  private boolean skipTillEnd;
  private int noToSkip;

  // Constructor
  public BBInterpreter(String[] inProgram) {
    this.PROGRAM = inProgram;
    this.programCounter = 0;
    this.skippingFailedIf = false;
    this.skipTillEnd = false;
    this.noToSkip = 0;
  }

  // Methods
  private void outputRecordOfExecution() {
    if (this.programCounter + 1 == this.PROGRAM.length && this.WHILELOOPPOINTERS.size() == 0) {
      System.out.println("-".repeat(15));
      System.out.println("~~~ Program has finished successfully! ~~~");
      System.out.println("-".repeat(15));
    } else {
      System.out.println("-".repeat(15));
      System.out.println(
          "Line " + (this.programCounter + 1) + " has finished executing. Variable list:");
      this.VARIABLES.forEach((key, value) -> System.out.println(key + ":\t" + value));
    }
  }

  private void init(String[] command) {
    /*These words are case-insensitive and reserved (cannot be used as identifiers):
      clear, copy, decr, do, end, endif, else, if, incr, init, not, to, then, while
    */
    if (Arrays.asList(this.RESERVED).contains(command[1].toLowerCase())) {
      System.out.println(
          "Error on line "
              + (this.programCounter + 1)
              + ": Attempt to initialise var with reserved keyword '"
              + command[1].toLowerCase()
              + "'");
      System.exit(1);

    } else if (command.length > 2) {
      try {
        Integer.parseInt(command[3]);

      } catch (NumberFormatException e) {
        System.out.println(
            "Error on line " + (this.programCounter + 1) + ": VAL for VAR init is not an Integer");
        System.exit(1);
      }
      this.VARIABLES.put(command[1], Integer.parseInt(command[3]));
    } else {
      this.VARIABLES.put(command[1], 0);
    }
  }

  private void clear(String[] command) {
    if (!this.VARIABLES.containsKey(command[1])) {
      System.out.println(
          "Error on line "
              + (this.programCounter + 1)
              + ": Attempt to clear a var that has not been initialised '"
              + command[1]
              + "'");
      System.exit(1);

    } else {
      this.VARIABLES.replace(command[1], 0);
    }
  }

  private void incr(String[] command) {
    if (!this.VARIABLES.containsKey(command[1])) {
      System.out.println(
          "Error on line "
              + (this.programCounter + 1)
              + ": Attempt to incr a var that has not been initialised '"
              + command[1]
              + "'");
      System.exit(1);

    } else {
      this.VARIABLES.replace(command[1], this.VARIABLES.get(command[1]) + 1);
    }
  }

  private void decr(String[] command) {
    if (!this.VARIABLES.containsKey(command[1])) {
      System.out.println(
          "Error on line "
              + (this.programCounter + 1)
              + ": Attempt to decr a var that has not been initialised '"
              + command[1]
              + "'");
      System.exit(1);

    } else {
      this.VARIABLES.replace(command[1], this.VARIABLES.get(command[1]) - 1);
    }
  }

  private void copy(String[] command) {
    if (!command[2].toLowerCase().equals("to")) {
      System.out.println(
          "Error on line "
              + (this.programCounter + 1)
              + ": Attempt to copy without correct syntax - 'to' was absent from args");
      System.exit(1);

    } else if (!(this.VARIABLES.containsKey(command[1])
        && this.VARIABLES.containsKey(command[2]))) {
      System.out.println(
          "Error on line "
              + (this.programCounter + 1)
              + ": Attempt to copy with vars that have not been initialised");
      System.exit(1);

    } else {
      this.VARIABLES.replace(command[3], this.VARIABLES.get(command[1]));
    }
  }

  private void iffHybridCaseCheck(String[] command, int value) {
    switch (command[2]) {
      case "==":
        if (this.VARIABLES.get(command[1]) == value) {
          break;
        } else {
          this.skippingFailedIf = true;
        }
        break;
      case ">=":
        if (this.VARIABLES.get(command[1]) >= value) {
          break;
        } else {
          this.skippingFailedIf = true;
        }
        break;
      case "<=":
        if (this.VARIABLES.get(command[1]) <= value) {
          break;
        } else {
          this.skippingFailedIf = true;
        }
        break;
      case ">":
        if (this.VARIABLES.get(command[1]) > value) {
          break;
        } else {
          this.skippingFailedIf = true;
        }
        break;
      case "<":
        if (this.VARIABLES.get(command[1]) < value) {
          break;
        } else {
          this.skippingFailedIf = true;
        }
        break;
      case "<>":
        if (!(this.VARIABLES.get(command[1]) == value)) {
          break;
        } else {
          this.skippingFailedIf = true;
        }
        break;
      default:
        break;
    }
  }
  private void iff(String[] command) {
    if (!(command.length == 5)) {
      System.out.println("Error on line " + (this.programCounter + 1) + ": Invalid syntax");
    }

    if (this.VARIABLES.containsKey(command[1])) {
      if (Arrays.asList(this.COMPARISONOPS).contains(command[2])) {
        if (this.VARIABLES.containsKey(command[3])) {
          iffHybridCaseCheck(command, this.VARIABLES.get(command[3]));
        } else {
          try {
            Integer.parseInt(command[3]);
          } catch (NumberFormatException e) {
            System.out.println(
                "Error on line "
                    + (this.programCounter + 1)
                    + ": VAL specified is not an Integer or VAR");
            System.exit(1);
          }
          iffHybridCaseCheck(command, Integer.parseInt(command[3]));
        }
      } else {
        System.out.println(
            "Error on line "
                + (this.programCounter + 1)
                + ": Comparison Operator '"
                + command[2]
                + "' is not supported");
        System.exit(1);
      }
    } else {
      System.out.println(
          "Error on line "
              + (this.programCounter + 1)
              + ": VAR '"
              + command[1]
              + "' did not exist");
      System.exit(1);
    }
  }

  private void whilee(String[] command) {
    if (!(command[2].equals("not") && command[4].equals("do"))) {
      System.out.println(
          "Error on line "
              + (this.programCounter + 1)
              + ": Invalid syntax - use 'while [VAR] not [VAL] do;'");
      System.exit(1);

    } else if (!this.VARIABLES.containsKey(command[1])) {
      System.out.println(
          "Error on line "
              + (this.programCounter + 1)
              + ": Attempt to start while loop with un-initialised var");
      System.exit(1);

    } else {
      try {
        Integer.parseInt(command[3]);

      } catch (NumberFormatException e) {
        System.out.println(
            "Error on line "
                + (this.programCounter + 1)
                + ": Target VAL for VAR in while is not an Integer");
        System.exit(1);
      }

      if (!(this.VARIABLES.get(command[1]) == Integer.parseInt(command[3]))) {
        // Continue with While
        this.WHILELOOPPOINTERS.putIfAbsent(this.programCounter, -1);

      } else {
        // Terminate While
        if (this.WHILELOOPPOINTERS.containsKey(this.programCounter)) {
          int copyBuffer;
          copyBuffer = this.WHILELOOPPOINTERS.get(this.programCounter);
          this.WHILELOOPPOINTERS.remove(this.programCounter);
          this.outputRecordOfExecution();
          this.programCounter = copyBuffer;
          this.skipTillEnd = true;
          this.noToSkip = 1;
          this.stepThrough();
          needsReturn = true;
        } else {
          this.skipTillEnd = true;
          this.noToSkip++;
        }
      }
    }
  }

  private void end(String[] command) {
    //Code to deal with while loops and/or end of program
    if (this.skipTillEnd) {
      // A while loop that has never been executed / one that is terminating is handled here.
      this.noToSkip--;
      if (this.noToSkip == 0 && this.programCounter == (this.PROGRAM.length - 1)) {
        // Program ending point
        this.outputRecordOfExecution();
        this.needsReturn = true;
        return;
      } else if (this.noToSkip == 0) {
        // Skipped enough times, continue normally
        this.skipTillEnd = false;
        return;
      }
    }

    if (!this.WHILELOOPPOINTERS.isEmpty()) {
      // While loop point exists, see if any have the current PC as their value

      if (this.WHILELOOPPOINTERS.containsValue(this.programCounter)) {
        AtomicBoolean found = new AtomicBoolean(false);
        this.WHILELOOPPOINTERS.forEach(
            (key, value) -> {
              if (value == this.programCounter) {
                found.set(true);
                this.outputRecordOfExecution();
                this.programCounter = key;
              }
            });
        if (found.get()) {
          this.stepThrough();
          this.needsReturn = true;
        }

      } else {
        // Links the 'last' unlinked while loop with this end statement
        AtomicInteger latestUnlinkedWhilePointer = new AtomicInteger();
        this.WHILELOOPPOINTERS.forEach(
            (key, value) -> {
              if (key > latestUnlinkedWhilePointer.get()) {
                latestUnlinkedWhilePointer.set(key);
              }
            });

        AtomicBoolean found = new AtomicBoolean(false);
        this.WHILELOOPPOINTERS.forEach(
            (key, value) -> {
              if (key == latestUnlinkedWhilePointer.get() && value == -1) {
                found.set(true);
                this.WHILELOOPPOINTERS.replace(key, -1, this.programCounter);
                this.outputRecordOfExecution();
                this.programCounter = key;
              }
            });
        if (found.get()) {
          this.stepThrough();
          this.needsReturn = true;
        }
      }

    } else if (this.programCounter == (this.PROGRAM.length - 1)) {
      // Program ending point
      this.outputRecordOfExecution();
      this.needsReturn = true;
    } else {
      System.out.println(
          "Error on line "
              + (this.programCounter + 1)
              + ": Invalid End - 'end' was not ending a while loop, nor the end of a program");
      System.exit(1);
    }
  }

  //Main interpreting method
  public void stepThrough() {
    String[] command = this.PROGRAM[this.programCounter].split(" "); //Splits up commands

    //Deals with skipping parts of code by consequence of an if statement
    if (this.skippingFailedIf) {
      if (!(command[0].toLowerCase().equals("else") || command[0].toLowerCase().equals("endif"))) {
        this.programCounter++;
        this.stepThrough();
        return;
      }
    }

    //Deals with skipping parts of code by consequence of a while loop
    if (this.skipTillEnd) {
      if (!(command[0].toLowerCase().equals("while") || command[0].toLowerCase().equals("end"))) {
        this.programCounter++;
        this.stepThrough();
        return;

      } else if (command[0].toLowerCase().equals("while")) {
        this.noToSkip++;
        this.programCounter++;
        this.stepThrough();
        return;
      }
    }

    //Checks the command keyword to see if it exists, then executes
    switch (command[0].toLowerCase()) {
      case "//":
        //A comment, so skip this
        this.programCounter++;
        this.stepThrough();
        return;

      case "init":
        init(command);
        break;
      case "clear":
        clear(command);
        break;
      case "incr":
        incr(command);
        break;
      case "decr":
        decr(command);
        break;
      case "copy":
        copy(command);
        break;
      case "if":
        iff(command);
        break;
      case "while":
        whilee(command);
        if (needsReturn) {
          this.needsReturn = false;
          return;
        }
        break;
      case "else":
        if (this.skippingFailedIf) {
          // Perform Else code block
          this.skippingFailedIf = false;
          break;
        } else {
          // Skip till EndIf
          this.skippingFailedIf = true;
        }
        break;

      case "endif":
        // Will continue, but no longer needs to skip code
        this.skippingFailedIf = false;
        break;
      case "end":
        end(command);
        if (needsReturn) {
          this.needsReturn = false;
          return;
        }
        break;

      default:
        //If command keyword does not exist
        System.out.println(
            "Error on line "
                + (this.programCounter + 1)
                + ": Command '"
                + command[0]
                + "' was not a valid command or comment");
        System.exit(1);
        break;
    }
    //Standard post-execution of a line
    this.outputRecordOfExecution();
    this.programCounter++;
    this.stepThrough();
  }
}
