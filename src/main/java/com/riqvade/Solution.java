package com.riqvade;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Function;

public class Solution {
    /**
     * @param args command line arguments.
     * 1. args0 sort mode (-a  ascending или -d descending), -d default
     * 2. args1 data type (-s string -i integer), required
     * 3. args2 output filename, required
     * 4. args3 s1[,s2[,s3..]] input filenames, each file is sorted ascending
     */
    public static void main(String[] args) throws Exception {
        boolean sortMode = false;
        Function<String, ? extends Comparable> mapper = null;
        String outFilename = null;
        ArrayList<String> listOfFileNames = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            if (args[i].contains("-")) {
                if ((args[i].equals("-d") || args[i].equals("-a"))) {
                    sortMode = args[i].equals("-d");
                } else {
                    if (args[i].equals("")) {
                        sortMode = false; // default ascending
                    }
                }
                if (args[i].equals("-s") || args[i].equals("-i")) {
                    mapper = args[i].equals("-s") ? Integer::parseInt : (s)->s;
                }
            } else {
                if (outFilename == null) {
                    outFilename = args[i];
                } else {
                    listOfFileNames.add(args[i]);
                }
            }
        }

        Objects.requireNonNull(outFilename, "No out filename");
        Objects.requireNonNull(mapper, "Data type not specified");
        System.out.println("out file: " + outFilename);
        System.out.printf("input files: %s%n", listOfFileNames.toString());
        if (listOfFileNames.isEmpty()) {
            throw new IllegalArgumentException("Empty input filename list");
        }
        SortManager<? extends Comparable> sortManager = new SortManager<>(sortMode, mapper);
        sortManager.ofFiles(listOfFileNames, outFilename);
        sortManager.sortData();
    }
}

