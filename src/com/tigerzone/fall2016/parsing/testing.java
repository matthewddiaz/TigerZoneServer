package com.tigerzone.fall2016.parsing;

import java.util.Scanner;

/**
 * Created by clayhausen on 11/19/16.
 */
public class testing {
    public static void main(String[] args) {
        ProtocolStateMachine psm = new ProtocolStateMachine();

        String testString1 = "GAME 1 MOVE 1 PLACE TLTTP AT 4 -1 90 NONE";
        String testString2 = "GAME 2 MOVE 1 PLACE TLLL- AT 4 1 90 NONE";

        Scanner scanner1 = new Scanner(testString1);
        Scanner scanner2 = new Scanner(testString2);

        Context context = new GameContext(scanner1, 1);
        psm.parse(context);
        System.out.println(testString1 + " is a valid move: " + context.wasMoveValid());

        context = new GameContext(scanner2, 2);

        psm.parse(context);
        System.out.println(testString2 + " is a valid move: " + context.wasMoveValid());
    }
}
