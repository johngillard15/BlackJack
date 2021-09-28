package com.utilities;

import java.util.Arrays;
import java.util.Scanner;

/**
 * <p>Contains methods that manage grabbing user input and handling invalid data. </p>
 *
 * <p>This class contains methods that handle runtime exceptions involving user input, mainly involving type conversions
 * from String to a number format (Integer, Double, etc.).</p>
 *
 * @author John Gillard
 * @since 13/8/2021
 */

public class Input {
    private static final Scanner scan = new Scanner(System.in);

    public static String getInput(){
        System.out.print("-> ");
        return scan.nextLine().trim();
    }

    public static String getString(String... validChoices){
        return getString(false, validChoices);
    }
    public static String getString(boolean isCaseSensitive, String... validChoices){
        String input;

        boolean validInput;
        do{
            input = getInput();

            validInput = validChoices.length == 0 || checkStringOptions(input, isCaseSensitive, validChoices);

            if(!validInput)
                System.out.printf("\"%s\" is not a valid option. Please try again.\n", input);
        }while(!validInput);

        return input;
    }

    public static boolean checkStringOptions(String input, boolean isCaseSensitive, String... validChoices){
        if(isCaseSensitive)
            return Arrays.asList(validChoices).contains(input);
        return Arrays.stream(validChoices).anyMatch(input::equalsIgnoreCase);
    }

    public static int getInt(){
        return getInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
    public static int getInt(final int MIN){
        return getInt(MIN, Integer.MAX_VALUE);
    }
    public static int getInt(final int MIN, final int MAX){
        String input;

        boolean validInput;
        do{
            input = getInput();

            validInput =
                    checkInt(input) && (Integer.parseInt(input) >= MIN && Integer.parseInt(input) <= MAX);

            if(!checkInt(input))
                System.out.printf("\"%s\" is not a valid number value. Please try again.\n", input);
            else if(MIN != Integer.MIN_VALUE && !(Integer.parseInt(input) >= MIN))
                System.out.println("Value must be greater than or equal to " + MIN);
            else if(MAX != Integer.MAX_VALUE && !(Integer.parseInt(input) <= MAX))
                System.out.println("Value must be less than or equal to " + MAX);
        }while(!validInput);

        return Integer.parseInt(input);
    }

    public static double getDouble(){
        return getDouble(Double.MIN_VALUE, Double.MAX_VALUE);
    }
    public static double getDouble(final double MIN){
        return getDouble(MIN, Double.MAX_VALUE);
    }
    public static double getDouble(final double MIN, final double MAX){
        String input;

        boolean validInput;
        do{
            input = getInput();

            validInput =
                    checkDouble(input) && (Double.parseDouble(input) >= MIN && Double.parseDouble(input) <= MAX);

            if(!checkDouble(input))
                System.out.println("That is not a valid number value. Please try again.");
            else if(MIN != Double.MIN_VALUE && !(Double.parseDouble(input) >= MIN))
                System.out.println("Value must be greater than or equal to " + MIN);
            else if(MAX != Double.MAX_VALUE && !(Double.parseDouble(input) <= MAX))
                System.out.println("Value must be less than or equal to " + MAX);
        }while(!validInput);

        return Double.parseDouble(input);
    }

    public static boolean getBoolean(String truthy, String falsy){
        String input = getString(truthy, falsy);

        return truthy.equalsIgnoreCase(input);
    }

    /**
     * <p>Attempts to convert the value of the passed parameter to an Integer value.</p>
     *
     * @param checkMe the String to convert
     * @return a boolean, which determines whether the value can be converted to an Integer
     *
     * @throws NumberFormatException if the specified String value cannot be converted to an Integer (not really
     * anymore, but it did)
     */
    public static boolean checkInt(String checkMe){
        try{
            Integer.parseInt(checkMe);
            return true;
        }
        catch(NumberFormatException e){
            return false;
        }
    }

    public static boolean checkDouble(String checkMe){
        try{
            Double.parseDouble(checkMe);
            return true;
        }
        catch(NumberFormatException e){
            return false;
        }
    }
}
