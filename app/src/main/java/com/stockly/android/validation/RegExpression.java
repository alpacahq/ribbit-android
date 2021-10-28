package com.stockly.android.validation;

/**
 * RegExpression
 * Regex defined that will be used for different validation
 * on app level. e.g regex NAME for validation of user's name.
 */
public interface RegExpression {
    String PHONE = "[0-9]{10,11}$";
    //        val NAME = "^[\\p{L}]{2,}+$".toRegex()
    String USER_NAME = "^[a-zA-Z0-9]{2,15}+$";
    String NAME = "^[A-Z][a-zA-Z]{1,15}+$";
    String FULL_NAME = "^[\\p{L} .'-]{3,25}+$";
    //        val PASSWORD = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{8,25}\$".toRegex()
    String PASSWORD = ".{8,16}$";
    String POLL = "^[\\p{L} ?]{1,}+$";
    String LONG_MESSAGE = "^(?s)[\\p{L} .'-?\\r\\n]{5,1000}+$";
    String NUMBER = "^[0-9]*$";
    String ADDRESS = "([A-Za-z0-9 .'-?#,]+ )+[A-Za-z0-9 .'-?#,]+$|^[A-Za-z0-9 .'-?#,]{5,50}+$";
//    String ADDRESS = "^[a-zA-Z0-9 .'-?#]{5,25}+$";

}
