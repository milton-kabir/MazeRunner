//package maze;
package com.kabir.milton;

public class Main {

    public static void main(String[] args) {
        // write your code here
        var ar = new int[11][11];
        for (int i = 1; i <= 10; i++) {
            for (int j = 1; j <= 10; j++) {
                ar[i][j] = 0;
            }
        }
        for (int i = 1; i <= 10; i++) {
            ar[i][1] = 1;
            ar[i][10] = 1;
            ar[1][i] = 1;
            ar[10][i] = 1;
        }
        ar[2][1] = 0;
        ar[2][10] = 0;
        for (int i = 3; i <= 8; i += 2) {
            for (int j = 1; j <= 8; j++) {
                ar[j][i] = 1;
            }
        }
        for (int i = 1; i <= 10; i++) {
            for (int j = 1; j <= 10; j++) {
                if (ar[i][j] == 0) {
                    System.out.print("  ");
                } else {
                    System.out.print("\u2588\u2588");
                }
            }
            System.out.println();
        }
    }
}
