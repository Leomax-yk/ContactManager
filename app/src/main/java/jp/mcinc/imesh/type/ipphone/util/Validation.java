package jp.mcinc.imesh.type.ipphone.util;

import android.text.TextUtils;

import java.util.Calendar;

public class Validation {
    public static boolean isMobileNumberValid(CharSequence mobileno) {
        if (TextUtils.isEmpty(mobileno)) {
            return false;
        } else if (mobileno.length() < 7) {
            return false;
        }
        return true;
    }

    public static String removeWhiteSpace(String input) {
        if (!validateString(input))
            return input;
        return input.trim();
    }

    public static String removeAllWhiteSpace(String input) {
        if (!validateString(input))
            return input;
        return input.replaceAll(" ", "");
    }

    public static boolean validateString(String str) {
        return str != null && !str.isEmpty() && !str.equals("null");
    }

    public static boolean validateNameLength(String str) {
        return str.length()>2;
    }

    public static boolean validateGender(String str) {
        if (validateString(str)) {
            if (str.equals("Male") || str.equals("Female") || str.equals("Other"))
                return true;
            else
                return false;
        } else
            return false;
    }

    public static boolean validateBloodGroup(String str) {
        if (str != null && !str.isEmpty() && !str.equals("null")) {
            if (str.equals("A+") || str.equals("A-") || str.equals("B+") || str.equals("B-") || str.equals("AB+") || str.equals("AB-") || str.equals("O+") || str.equals("O-"))
                return true;
            else
                return false;
        } else
            return false;
    }

    public static boolean validateCondtion(String str) {
        if (str != null && !str.isEmpty() && !str.equals("null")) {
            if (str.equals("Yes") || str.equals("No"))
                return true;
            else
                return false;
        } else
            return false;
    }

    public static String getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return "" + age;
    }
}

