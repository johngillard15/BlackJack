package com.utilities;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>This enum holds fields containing ANSI escape codes for colored text and backgrounds.</p>
 * <p>The method {@link ANSI#getCode(String)} accepts the name of an escape code and will return the desired code</p>
 *
 * @since 15/8/2021
 * @author John Gillard
 */
public enum ANSI {
    RESET("\u001B[0m"),

    BOLD("\u001B[1m")/* HIGH_INTENSITY */,
    HIGH_INTENSITY("\u001B[1m"),
    LOW_INTENSITY("\u001B[2m"),
    ITALIC("\u001B[3m"),
    UNDERLINE("\u001B[4m"),
    INVERT("\u001B[7m"),

    BLACK("\u001B[30m"),
    BLUE("\u001B[34m"),
    CYAN("\u001B[36m"),
    GREEN("\u001B[32m"),
    MAGENTA("\u001B[35m"),
    RED("\u001B[31m"),
    WHITE("\u001B[37m"),
    YELLOW("\u001B[33m"),
    GRAY("\u001B[90m")/* BLACK_BRIGHT */,
    BLACK_BRIGHT("\u001B[90m"),
    BLUE_BRIGHT("\u001B[94m"),
    CYAN_BRIGHT("\u001B[96m"),
    RED_BRIGHT("\u001B[91m"),
    GREEN_BRIGHT("\u001B[92m"),
    MAGENTA_BRIGHT("\u001B[95m"),
    WHITE_BRIGHT("\u001B[97m"),
    YELLOW_BRIGHT("\u001B[93m"),

    BLACK_BG("\u001B[40m"),
    BLUE_BG("\u001B[44m"),
    CYAN_BG("\u001B[46m"),
    GREEN_BG("\u001B[42m"),
    MAGENTA_BG("\u001B[45m"),
    RED_BG("\u001B[41m"),
    WHITE_BG("\u001B[47m"),
    YELLOW_BG("\u001B[43m"),
    BLACK_BG_BRIGHT("\u001B[100m"),
    BLUE_BG_BRIGHT("\u001B[104m"),
    CYAN_BG_BRIGHT("\u001B[106m"),
    GREEN_BG_BRIGHT("\u001B[102m"),
    MAGENTA_BG_BRIGHT("\u001B[105m"),
    RED_BG_BRIGHT("\u001B[101m"),
    WHITE_BG_BRIGHT("\u001B[107m"),
    YELLOW_BG_BRIGHT("\u001B[103m");

    private final String code;

    ANSI(String code){
        this.code = code;
    }

    /**
     * <p>Returns a String containing an ANSI escape code.</p>
     *
     * @param color the name of the requested escape code
     * @return a String containing the escape code
     *
     * @throws NoSuchElementException if the requested code does not exist as a field in ANSI
     */
    public static String getCode(String color){
        Optional<ANSI> colorCode = Arrays.stream(ANSI.values())
                .filter(x -> x.name().equals(color))
                .findFirst();

        if(colorCode.isPresent())
            return colorCode.get().toString();
        else
            throw new NoSuchElementException("Code \"" + color + "\" does not exist.");
    }

    public static String format(String str, ANSI... codes) {
        String formats = Arrays.stream(codes).map(ANSI::toString).collect(Collectors.joining());
        return formats + str + RESET;
    }
    public static String format(String str, String... formatting){
        String formats = Arrays.stream(formatting).map(ANSI::getCode).collect(Collectors.joining());
        return formats + str + RESET;

//        Function<String, String> reformatter = ANSI::getCode;
//        return format(str, reformatter, formatting);
    }
//    public static String format(String str, Function<String, String> reformatter, String... stuff) {
//        String formats = Arrays.stream(stuff).map(reformatter).collect(Collectors.joining());
//        return formats + str + RESET;
//    }

    @Override
    public String toString(){
        return code;
    }
}
