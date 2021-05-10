package com.zolve;

public class ServerURL {
    private static String BASE_URL = "http://192.168.1.2:5678";
    static String LOGIN_URL = BASE_URL + "/login";
    static String TRANSACTIONS_URL = BASE_URL + "/list";
    static String CREDIT_URL = BASE_URL + "/credit";
    static String DEBIT_URL = BASE_URL + "/debit";
    static String BALANCE_URL = BASE_URL + "/currentBalance";

    public static void setBaseUrl(String baseUrl) {
        BASE_URL = "http://" + baseUrl + ":5678";
    }
}
