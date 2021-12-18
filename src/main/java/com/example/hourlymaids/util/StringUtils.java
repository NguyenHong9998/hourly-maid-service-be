package com.example.hourlymaids.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static final String TIME_MILLISECONDS_PATTERN = "^(0[0-9]|1[0-9]|2[0-3]):([0-9]|[0-5][0-9]):([0-9]|[0-5][0-9]):([0-9]{1,3})$";
    public static final String PATTERN_LOG = "[line: %s] %s: %s";

    public static boolean compareString(String str1, String str2) {
        String str1Temp = str1;
        String str2Temp = str2;
        if (str1Temp == null) {
            str1Temp = "";
        }
        if (str2Temp == null) {
            str2Temp = "";
        }

        if (str1Temp.equals(str2Temp)) {
            return true;
        }
        return false;
    }

    public static boolean isValidString(Object temp) {
        if (temp == null || temp.toString().trim().equals("")) {
            return false;
        }
        return true;
    }


    static boolean isInteger(String str) {
        if (str == null || !str.matches("[0-9]+$")) {
            return false;
        }
        return true;
    }


    public static boolean isLong(String str) {
        try {
            Long.valueOf(str);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }


    public static boolean isDouble(String str) {
        try {
            Double.valueOf(str);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }


    public static boolean isBoolean(String str) {
        try {
            Boolean.valueOf(str);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }


    public static Boolean convertStringToBooleanOrNull(String input) {
        try {
            return Boolean.valueOf(input);
        } catch (Exception e) {
            return null;
        }
    }


    public static Long convertStringToLongOrNull(String input) {
        try {
            return Long.valueOf(input);
        } catch (Exception e) {
            return null;
        }
    }


    public static Long convertObjectToLongOrNull(Object input) {
        try {
            return convertStringToLongOrNull(input.toString());
        } catch (Exception e) {
            return null;
        }
    }


    public static Integer convertStringToIntegerOrNull(String input) {
        try {
            return Integer.valueOf(input);
        } catch (Exception e) {
            return null;
        }
    }

    public static Float convertStringToFloatOrNull(String input) {
        try {
            return Float.valueOf(input);
        } catch (Exception e) {
            return null;
        }
    }

    public static String convertObjectToString(Object input) {
        return input == null ? null : input.toString();

    }

    public static boolean convertStringToBoolean(String input) throws Exception {
        if (input != null) {
            if (input.equals(Boolean.TRUE.toString()))
                return true;
            if (input.equals(Boolean.FALSE.toString()))
                return false;
        }
        throw new Exception();
    }

    public static boolean isStringAscii(String str) {
        for (char ch : str.toCharArray()) {
            if (!isAscii(ch)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAscii(char ch) {
        return ch < 128;
    }


    public static Double convertStringToDoubleOrNull(String amountNumber) {
        try {
            return Double.valueOf(amountNumber);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
                + "A-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        if (email == null) {
            return false;
        }
        return pat.matcher(email).matches();
    }

    public static boolean containsOnlyNumbers(String str) {
        String regex = "[0-9]+";
        boolean b = str.matches(regex);
        return b;
    }


    public static String convertDoubleToStringOrNull(Double value, String format) {
        return value == null ? null : new DecimalFormat(format).format(new Double(value.toString()));
    }

    public static String convertDateToStringFormatyyyyMMdd(Date input) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return input == null ? null : dateFormat.format(input).toString();

    }

    public static boolean isEmpty(Object str) {
        return (str == null || "".equals(str));
    }

    public static String formatId(String strOriginal, char leadingCharacter, int outputLen) {
        String str = String.format("%" + outputLen + "s", "").replace(' ', leadingCharacter);

        return (strOriginal.length() == outputLen) ? strOriginal : (str + strOriginal).substring(strOriginal.length());
    }

    public static String convertObjectToStringOrEmpty(Object input) {
        return input == null ? "" : input.toString();
    }

    public static boolean isMatcherPattern(String str, String regex) {
        return str.matches(regex);
    }

    public static boolean isCharacterWithSize(String str, int regex) {
        return str.matches("^[\\w]{0," + regex + "}+$");
    }

    public static boolean isDigitWithSize(String str, int regex) {
        return str.matches("^[\\d]{0," + regex + "}+$");
    }

    public static String convertDateToStringFormatPattern(Date input, String Pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Pattern);
        return input == null ? null : dateFormat.format(input).toString();
    }


    public static String calculateTimeEnd(String timeStart, String duration) {

        int timeEnd[] = new int[4];
        String timePattern = "^(\\d{2}):(\\d{2}):(\\d{2}):(\\d{2})$";
        Pattern pattern = Pattern.compile(timePattern);
        Matcher timeStartMatcher = pattern.matcher(timeStart);
        Matcher durationMatcher = pattern.matcher(duration);

        if (timeStartMatcher.find() && durationMatcher.find()) {
            timeEnd[3] = StringUtils.convertStringToIntegerOrNull(timeStartMatcher.group(4))
                    + StringUtils.convertStringToIntegerOrNull(durationMatcher.group(4));
            timeEnd[2] = StringUtils.convertStringToIntegerOrNull(timeStartMatcher.group(3))
                    + StringUtils.convertStringToIntegerOrNull(durationMatcher.group(3));
            timeEnd[1] = StringUtils.convertStringToIntegerOrNull(timeStartMatcher.group(2))
                    + StringUtils.convertStringToIntegerOrNull(durationMatcher.group(2));
            timeEnd[0] = StringUtils.convertStringToIntegerOrNull(timeStartMatcher.group(1))
                    + StringUtils.convertStringToIntegerOrNull(durationMatcher.group(1));

            if (timeEnd[3] >= 100) {
                timeEnd[3] -= 100;
                timeEnd[2] += 1;
            }
            if (timeEnd[2] >= 60) {
                timeEnd[2] -= 60;
                timeEnd[1] += 1;
            }
            if (timeEnd[1] >= 60) {
                timeEnd[1] -= 60;
                timeEnd[0] += 1;
            }
        }

        return String.format("%02d:%02d:%02d:%02d", timeEnd[0], timeEnd[1], timeEnd[2], timeEnd[3]);
    }

    public static String replaceSpecialCharacter(String value) {
        if (value != null) {
            value = value.replaceAll("\\\\", "\\\\\\\\");
            value = value.replaceAll("%", "\\\\%");
            value = value.replaceAll("_", "\\\\_");
        } else {
            value = "";
        }
        return value;
    }


    public static Long calculateTotalMilliSeconds(String time) {
        long[] timeConvert = new long[4];
        String timePattern = "^(\\d{2}):(\\d{2}):(\\d{2}):(\\d{3})$";
        Pattern pattern = Pattern.compile(timePattern);
        Matcher timeMatcher = pattern.matcher(time);
        if (timeMatcher.find()) {
            timeConvert[3] = StringUtils.convertStringToLongOrNull(timeMatcher.group(4));
            timeConvert[2] = StringUtils.convertStringToLongOrNull(timeMatcher.group(3));
            timeConvert[1] = StringUtils.convertStringToLongOrNull(timeMatcher.group(2));
            timeConvert[0] = StringUtils.convertStringToLongOrNull(timeMatcher.group(1));
        }

        return (((timeConvert[0] * 60 * 60) + (timeConvert[1] * 60) + (timeConvert[2])) * 1000) + timeConvert[3];
    }


    public static boolean validateStringFormat(String valueCheck, String pattern) {
        try {
            Pattern patternCheck = Pattern.compile(pattern);
            return patternCheck.matcher(valueCheck).matches();
        } catch (Exception e) {
            return false;
        }
    }

    public static String buildDeleteConditionQuery(String deleteParam) {
        return " (" + deleteParam + " != :isDeleted)";
    }


    public static String buildSelectQuery(List<String> attributes, List<String> tables, List<String> conditions,
                                          String orderByValue) {
        StringBuilder selectQuery = new StringBuilder();
        selectQuery.append(" SELECT ");
        selectQuery.append(String.join(", ", attributes));
        selectQuery.append(" FROM ");
        selectQuery.append(String.join(" ", tables));
        selectQuery.append(" WHERE ");
        selectQuery.append(String.join(" AND ", conditions));
        selectQuery.append(" ORDER BY " + orderByValue);
        return selectQuery.toString();
    }


    public static String buildSelectQuery(List<String> attributes, List<String> tables, List<String> conditions, List<String> groupBy,
                                          String orderByValue) {
        StringBuilder selectQuery = new StringBuilder();
        selectQuery.append(" SELECT ");
        selectQuery.append(String.join(", ", attributes));
        selectQuery.append(" FROM ");
        selectQuery.append(String.join(" ", tables));
        selectQuery.append(" WHERE ");
        selectQuery.append(String.join(" AND ", conditions));
        selectQuery.append(" GROUP BY ");
        selectQuery.append(String.join(", ", groupBy));
        selectQuery.append(" ORDER BY " + orderByValue);
        return selectQuery.toString();
    }

    public static String getAlphaNumericString(int n) {
        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }


    public static String buildSelectQuery(List<String> attribute, List<String> table, List<String> condition) {
        StringBuilder selectQuery = new StringBuilder();
        selectQuery.append(" SELECT ");
        selectQuery.append(String.join(", ", attribute));
        selectQuery.append(" FROM ");
        selectQuery.append(String.join(" ", table));
        selectQuery.append(" WHERE ");
        selectQuery.append(String.join(" AND ", condition));
        return selectQuery.toString();
    }


    public static String changeDatePattern(String dateInput, String sourcePattern, String targetPattern) {
        return convertDateToStringFormatPattern(
                DateTimeUtils.convertStringToDateOrNull(dateInput, sourcePattern), targetPattern);
    }


    public static String convertDuration(Long milliseconds) {
        Long timespan[] = new Long[]{0l, 0l, (milliseconds / 1000), (milliseconds % 1000)};

        if (timespan[2] >= 60) {

            timespan[1] = timespan[2] / 60;
            timespan[2] %= 60;

            // Hours
            if (timespan[1] >= 60) {
                timespan[0] = timespan[1] / 60;
                timespan[1] %= 60;
            }
        }
        return formatDuration(timespan);
    }

    static String formatDuration(Long[] timespan) {
        String result[] = new String[]{"", "", "", ""};
        int i = 0;
        for (Long time : timespan) {

            if (time < 10) {
                result[i] = "0" + timespan[i];
            } else {
                result[i] = "" + timespan[i];
            }

            if (i == 3 && timespan[i] < 100) {
                result[i] = "0" + result[i];
            }
            i++;
        }
        return String.join(":", result);

    }

    public static String buildLog(String err, int line) {
        return String.format(PATTERN_LOG, line, null, err);
    }

}
