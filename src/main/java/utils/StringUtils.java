package utils;

public class StringUtils {
    public String removeNonDigits(String value) {
        return value.replaceAll("[^\\d]", "");
    }
}