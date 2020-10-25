package com.jwjo1g20;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

  public static void main(String[] args) {
    //Checking arguement was included
    if (args.length == 0) {
      System.out.println("Usage: java Main.java [PROG_NAME].bb");
      System.exit(1);
    }

    //Checking file exists
    if (!Files.exists(Paths.get(args[0]))) {
      System.out.println("File: " + args[0] + " does not exist");
      System.exit(1);
    }

    //Reading file bytes
    String[] program = new String[0];
    try {
      program = new String(Files.readAllBytes(Paths.get(args[0]))).split(";");
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
    //Trimming program lines
    for (int i = 0; i < program.length; i++) {
      program[i] = program[i].trim();
    }

    // Outputting program lines
    System.out.println("--- Program " + "-".repeat(18));
    for (int i = 1; i <= program.length; i++) {
      System.out.println(i + " " + program[i - 1] + ";");
    }
    System.out.println("-".repeat(30));
    System.out.println();

    // Running (or attempting to run) program
    BBInterpreter bbi = new BBInterpreter(program);
    bbi.stepThrough();
  }
}
