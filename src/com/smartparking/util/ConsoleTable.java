package com.smartparking.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility for formatting and printing structured ASCII tables on the console.
 * 
 * @author Madhan sree (Roll: 732925ITR054, Reg No: 54)
 */
public class ConsoleTable {
    private final List<String> headers = new ArrayList<>();
    private final List<List<String>> rows = new ArrayList<>();

    public ConsoleTable(String... headers) {
        this.headers.addAll(Arrays.asList(headers));
    }

    public void addRow(String... rowValues) {
        rows.add(Arrays.asList(rowValues));
    }

    public void print() {
        if (headers.isEmpty() && rows.isEmpty()) {
            System.out.println("(Empty Table)");
            return;
        }

        int columnCount = headers.size();
        int[] columnWidths = new int[columnCount];

        for (int i = 0; i < columnCount; i++) {
            columnWidths[i] = headers.get(i).length();
        }

        for (List<String> row : rows) {
            for (int i = 0; i < Math.min(row.size(), columnCount); i++) {
                String val = row.get(i) != null ? row.get(i) : "";
                if (val.length() > columnWidths[i]) {
                    columnWidths[i] = val.length();
                }
            }
        }

        // Print separator
        printSeparator(columnWidths);

        // Print Header
        System.out.print("| ");
        for (int i = 0; i < columnCount; i++) {
            System.out.print(padRight(headers.get(i), columnWidths[i]));
            System.out.print(" | ");
        }
        System.out.println();
        printSeparator(columnWidths);

        // Print Data Rows
        if (rows.isEmpty()) {
            System.out.println("| " + padRight("(No records found)", getTotalWidth(columnWidths) - 2) + " |");
        } else {
            for (List<String> row : rows) {
                System.out.print("| ");
                for (int i = 0; i < columnCount; i++) {
                    String val = (i < row.size() && row.get(i) != null) ? row.get(i) : "";
                    System.out.print(padRight(val, columnWidths[i]));
                    System.out.print(" | ");
                }
                System.out.println();
            }
        }

        printSeparator(columnWidths);
    }

    private void printSeparator(int[] widths) {
        System.out.print("+");
        for (int w : widths) {
            for (int i = 0; i < w + 2; i++) {
                System.out.print("-");
            }
            System.out.print("+");
        }
        System.out.println();
    }

    private int getTotalWidth(int[] widths) {
        int total = 0;
        for (int w : widths) {
            total += w + 3;
        }
        return total;
    }

    private String padRight(String text, int width) {
        return String.format("%-" + width + "s", text);
    }
}
