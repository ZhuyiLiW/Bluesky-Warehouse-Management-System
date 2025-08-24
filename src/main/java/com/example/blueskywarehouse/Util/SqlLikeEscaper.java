package com.example.blueskywarehouse.Util;

public class SqlLikeEscaper {
    public static String escape(String input) {
        if (input == null) return null;
        return input
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
