package com.example.hackathon2023;

import java.sql.*;
import java.util.regex.Pattern;

public class DBUtility {
    private static String dbUser = "root";
    private static String password = "password";
    /**
     * jdbc:mysql -> the database driver
     * 127.0.0.1 -> IP address of the server
     * 3306 -> port of the database server
     * COMP1011 -> name of the database
     */
    private static String connectURLDecoy = "jdbc:mysql://127.0.0.1:3306/decoy_database";
    private static String connectURLReal = "jdbc:mysql://127.0.0.1:3306/real_database";

    /**
     * This method will save a user to the database
     */
    public static String saveUserToDB(User user) throws SQLException {
        String responseMsg = "Broken";
        String sql = "INSERT INTO users VALUES (?,?,?)";

        // try() ... is called try with resources
        // anything inside the () will be auto closed
        if(isSqlInjectionAttempt(user.getEmail())) {
            System.out.println("EMAIL HACKER");
        }
        if(isSqlInjectionAttempt(user.getUserName())) {
            System.out.println("USER HACKER");
        }
        if(isSqlInjectionAttempt(user.getPassword())) {
            System.out.println("PASSWORD HACKER");
        }
        if(!isSqlInjectionAttempt(user.getEmail()) && !isSqlInjectionAttempt(user.getUserName()) && !isSqlInjectionAttempt(user.getPassword())) {
            try(
                    Connection conn = DriverManager.getConnection(connectURLReal, dbUser, password);
                    PreparedStatement ps = conn.prepareStatement(sql);
            )
            {
                ps.setString(1, user.getEmail());
                ps.setString(2, user.getUserName());
                ps.setString(3, user.getPassword());

                ps.executeUpdate();

                responseMsg = "Added New User!";
            } catch (SQLIntegrityConstraintViolationException e) {
                responseMsg = "User in DB";
            }
        } else {
            try(
                    Connection conn = DriverManager.getConnection(connectURLDecoy, dbUser, password);
                    PreparedStatement ps = conn.prepareStatement(sql);
            )
            {
                ps.setString(1, user.getEmail());
                ps.setString(2, user.getUserName());
                ps.setString(3, user.getPassword());

                ps.executeUpdate();

                responseMsg = "Added New User! (to fake DB)";
            } catch (SQLIntegrityConstraintViolationException e) {
                responseMsg = "User in (fake) DB";
            }
        }


        return responseMsg;
    }

    static boolean isSqlInjectionAttempt(String input) {
        // List of SQL Injection patterns as regular expressions
        String[] sqlInjectionPatterns =  {
                "--", // SQL line comment
                ";", // String termination
                "' OR", // OR-based injection
                "'=", // Comparison-based injection
                "\\bUNION\\b", // UNION-based injection
                "'; DROP", // DROP statement
                "'; EXEC", // EXEC statement
                "\\bSLEEP\\b", // SLEEP function (MySQL)
                "\\bWAITFOR\\b", // WAITFOR function (MSSQL)
                "\\bBENCHMARK\\b", // BENCHMARK function (MySQL)
                "'[^\\w\\s]+", // Unescaped special character after quote
                "xp_cmdshell", // Command execution (MSSQL)
                "1\\s*=\\s*1", // Tautology-based injection
                "\\bSELECT\\b", // SELECT statement
                "\\bUPDATE\\b", // UPDATE statement
                "\\bINSERT\\b", // INSERT statement
                "\\bDELETE\\b", // DELETE statement
                "\\bNULL\\b", // NULL keyword
                "\\bCAST\\b", // CAST function
                "\\bCONVERT\\b", // CONVERT function
                "\\bHAVING\\b", // HAVING keyword
                //TODO Add more patterns as needed
        };

        for (String pattern: sqlInjectionPatterns) {
            if (Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(input).find()) return true;
        }

        return false;
    }
}
