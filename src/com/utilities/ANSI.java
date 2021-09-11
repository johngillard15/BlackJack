package com.utilities;

/**
 * <p>This interface holds fields containing ANSI escape codes for colored text and backgrounds.</p>
 * <p>The method {@link ANSI#getCode(String)} accepts the name of an escape code and will return the desired code</p>
 *
 * @since 15/8/2021
 * @author John Gillard
 */
public interface ANSI {
    String RESET = "\u001B[0m";
    String BLACK = "\u001B[30m";
    String RED = "\u001B[31m";
    String GREEN = "\u001B[32m";
    String YELLOW = "\u001B[33m";
    String BLUE = "\u001B[34m";
    String PURPLE = "\u001B[35m";
    String CYAN = "\u001B[36m";
    String WHITE = "\u001B[37m";
    String BLACK_BG = "\u001B[40m";
    String RED_BG = "\u001B[41m";
    String GREEN_BG = "\u001B[42m";
    String YELLOW_BG = "\u001B[43m";
    String BLUE_BG = "\u001B[44m";
    String PURPLE_BG = "\u001B[45m";
    String CYAN_BG = "\u001B[46m";
    String WHITE_BG = "\u001B[47m";

    /**
     * <p>Returns a String containing an ANSI escape code.</p>
     *
     * @param code the name of the requested escape code
     * @return a String containing the escape code
     *
     * @throws IllegalStateException if the requested code does not exist as a field in ANSI
     */
    static String getCode(String code){
        return switch(code.toUpperCase().trim()){
            case "RESET" -> RESET;
            case "BLACK" -> BLACK;
            case "RED" -> RED;
            case "GREEN" -> GREEN;
            case "YELLOW" -> YELLOW;
            case "BLUE" -> BLUE;
            case "PURPLE" -> PURPLE;
            case "CYAN" -> CYAN;
            case "WHITE" -> WHITE;
            case "BLACK_BG" -> BLACK_BG;
            case "RED_BG" -> RED_BG;
            case "GREEN_BG" -> GREEN_BG;
            case "YELLOW_BG" -> YELLOW_BG;
            case "BLUE_BG" -> BLUE_BG;
            case "PURPLE_BG" -> PURPLE_BG;
            case "CYAN_BG" -> CYAN_BG;
            case "WHITE_BG" -> WHITE_BG;
            default -> throw new IllegalStateException("Code \"" + code + "\" does not exist");
        };
    }
}
