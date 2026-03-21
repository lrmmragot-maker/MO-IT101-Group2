/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.motorph_ms2_group2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author allerizajamsuli
 */
public class MotorPH_MS2_Group2 {

    // ===========================
    // CSV file paths
    // ===========================
    private static final String Employee_CSV =
            "/Users/Lois/NetBeansProjects/MO-IT101-Group2/resources/MotorPH_Employee Data - Employee Details.csv";

    private static final String Attendance_CSV =
            "/Users/Lois/NetBeansProjects/MO-IT101-Group2/resources/MotorPH_Employee Data - Attendance Record.csv";

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("==================================");
        System.out.print("Enter Username: ");
        String username = input.nextLine();

        System.out.print("Enter Password: ");
        String password = input.nextLine();

        if (!(username.equals("employee") || username.equals("payroll_staff")) || !password.equals("12345")) {
            System.out.println("\nIncorrect username and/or password.");
            System.out.println("\nProgram terminated.");
            input.close();
            return;
        }

        System.out.println("Login Successful!");

        if (username.equals("employee")) {
            employeeMenu(input, Employee_CSV);
        } else {
            payrollStaffMenu(input, Employee_CSV, Attendance_CSV);
        }

        input.close();
    }

    // ===========================
    // Employee Menu
    // ===========================
    public static void employeeMenu(Scanner input, String employeeFile) {
        System.out.println("==================================");
        System.out.println("\nEmployee Menu");
        System.out.println("1. Enter your employee number");
        System.out.println("2. Exit");
        System.out.print("\nChoose option: ");

        int choice = input.nextInt();
        input.nextLine();

        if (choice == 1) {
            System.out.println("\n==================================");
            System.out.print("\nEnter your employee number: ");
            String empNumber = input.nextLine();

            String[] empData = getEmployeeData(employeeFile, empNumber);

            if (empData == null) {
                System.out.println("Employee number does not exist.");
            } else {
                System.out.println("\n==================================");
                System.out.println("Employee Details:");
                System.out.println("Employee #: " + empData[0]);
                System.out.println("Employee Name: " + empData[2] + " " + empData[1]);
                System.out.println("Birthday: " + empData[3]);
                System.out.println("==================================");
            }
        } else {
            System.out.println("\nProgram terminated.");
        }
    }

    // ===========================
    // Get employee data
    // ===========================
    public static String[] getEmployeeData(String employeeFile, String empNumber) {
        try (BufferedReader br = new BufferedReader(new FileReader(employeeFile))) {
            br.readLine();
            String line;

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields[0].trim().equals(empNumber)) {
                    return fields;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading employee file.");
        }

        return null;
    }

    // ===========================
    // Payroll Staff Menu
    // ===========================
    public static void payrollStaffMenu(Scanner input, String employeeFile, String attendanceFile) {
        System.out.println("\n==================================");
        System.out.println("\nPayroll Menu");
        System.out.println("1. Process Payroll");
        System.out.println("2. Exit");
        System.out.print("\nChoose option: ");
        int payrollChoice = input.nextInt();
        input.nextLine();

        if (payrollChoice == 1) {
            System.out.println("\n==================================");
            System.out.println("\nProcess Payroll");
            System.out.println("\n1. One employee");
            System.out.println("2. All employees");
            System.out.println("3. Exit");
            System.out.println("\n==================================");

            System.out.print("\nChoose option: ");
            int option = input.nextInt();
            input.nextLine();

            if (option == 1) {
                System.out.print("\nEnter employee number: ");
                String empNumber = input.nextLine();
                processPayrollForOneMonthly(empNumber, employeeFile, attendanceFile);
            } else if (option == 2) {
                processPayrollForAll(employeeFile, attendanceFile);
            } else {
                System.out.println("Program terminated.");
            }
        } else {
            System.out.println("Program terminated.");
        }
    }

    // ===========================
    // Attendance Data Retrieval
    // ===========================
    public static String[][] getEmployeeAttendanceRecords(String attendanceFile, String empNumber) {
        List<String[]> records = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(attendanceFile))) {
            br.readLine();
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] fields = line.split(",");

                for (int i = 0; i < fields.length; i++) {
                    fields[i] = fields[i].trim();
                }

                if (fields.length < 6) {
                    continue;
                }

                if (fields[0].equals(empNumber)) {
                    records.add(fields);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading attendance file: " + e.getMessage());
        }

        return records.toArray(new String[0][]);
    }

    // ===========================
    // Payroll Processing - One Employee
    // ===========================
    public static void processPayrollForOneMonthly(String empNumber, String employeeFile, String attendanceFile) {
        String[] empData = getEmployeeData(employeeFile, empNumber);
        if (empData == null) {
            System.out.println("Employee number does not exist.");
            return;
        }

        double hourlyRate = Double.parseDouble(empData[empData.length - 1].trim());
        String[][] attendanceRecords = getEmployeeAttendanceRecords(attendanceFile, empNumber);

        System.out.println("\nEmployee #: " + empData[0]);
        System.out.println("Employee Name: " + empData[2] + " " + empData[1]);
        System.out.println("Birthday: " + empData[3]);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        for (int month = 6; month <= 12; month++) {
            Month monthEnum = Month.of(month);
            String monthName = monthEnum.name();

            // ---------- First cutoff 1–15 ----------
            long totalMinutes1 = 0;

            for (String[] record : attendanceRecords) {
                if (record.length < 6) {
                    continue;
                }

                LocalDate date;
                try {
                    date = LocalDate.parse(record[3].trim(), dateFormatter);
                } catch (Exception e) {
                    continue;
                }

                if (date.getMonthValue() != month) {
                    continue;
                }

                int day = date.getDayOfMonth();
                if (day < 1 || day > 15) {
                    continue;
                }

                totalMinutes1 += calculateDailyMinutes(record[4].trim(), record[5].trim());
            }

            double hoursDecimal1 = totalMinutes1 / 60.0;
            double gross1 = hoursDecimal1 * hourlyRate;

            System.out.println("\nMonth: " + monthName);
            System.out.println("Cutoff Date: " + monthName + " 1 to " + monthName + " 15");
            System.out.println("Total Hours Worked: " + formatMinutesToTime(totalMinutes1));
            System.out.println("Gross Salary: ₱" + String.format("%.2f", gross1));
            System.out.println("Net Salary: ₱" + String.format("%.2f", gross1));
            System.out.println("--------------------------------");

            // ---------- Second cutoff 16–end ----------
            long totalMinutes2 = 0;

            for (String[] record : attendanceRecords) {
                if (record.length < 6) {
                    continue;
                }

                LocalDate date;
                try {
                    date = LocalDate.parse(record[3].trim(), dateFormatter);
                } catch (Exception e) {
                    continue;
                }

                if (date.getMonthValue() != month) {
                    continue;
                }

                int day = date.getDayOfMonth();
                if (day < 16) {
                    continue;
                }

                totalMinutes2 += calculateDailyMinutes(record[4].trim(), record[5].trim());
            }

            double hoursDecimal2 = totalMinutes2 / 60.0;
            double gross2 = hoursDecimal2 * hourlyRate;

            double sss = computeSSS(gross2);
            double philHealth = computePH(gross2);
            double pagibig = computePagibig(gross2);
            double tax = computeIncomeTax(gross2);
            double totalDeductions = sss + philHealth + pagibig + tax;
            double net2 = gross2 - totalDeductions;

            System.out.println("Cutoff Date: " + monthName + " 16 to " + monthName + " "
                    + monthEnum.length(LocalDate.now().isLeapYear()));
            System.out.println("Total Hours Worked: " + formatMinutesToTime(totalMinutes2));
            System.out.println("Gross Salary: ₱" + String.format("%.2f", gross2));
            System.out.println("Each Deduction:");
            System.out.println("SSS: ₱" + String.format("%.2f", sss));
            System.out.println("PhilHealth: ₱" + String.format("%.2f", philHealth));
            System.out.println("Pag-IBIG: ₱" + String.format("%.2f", pagibig));
            System.out.println("Tax: ₱" + String.format("%.2f", tax));
            System.out.println("Total Deductions: ₱" + String.format("%.2f", totalDeductions));
            System.out.println("Net Salary: ₱" + String.format("%.2f", net2));
            System.out.println("--------------------------------");
        }
    }

    // ===========================
    // Payroll Processing - All Employees
    // ===========================
    public static void processPayrollForAll(String employeeFile, String attendanceFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(employeeFile))) {
            br.readLine();
            String line;

            while ((line = br.readLine()) != null) {
                String[] empData = line.split(",");
                String empNumber = empData[0].trim();

                System.out.println("--------------------------------------------------");
                processPayrollForOneMonthly(empNumber, employeeFile, attendanceFile);
            }
        } catch (IOException e) {
            System.out.println("Error reading employee file.");
        }
    }

    // ===========================
    // Daily Hours Calculation in Minutes
    // ===========================
    public static long calculateDailyMinutes(String logInStr, String logOutStr) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");

        LocalTime logIn;
        LocalTime logOut;

        try {
            logIn = LocalTime.parse(logInStr, timeFormatter);
            logOut = LocalTime.parse(logOutStr, timeFormatter);
        } catch (Exception e) {
            return 0;
        }

        LocalTime start = LocalTime.of(8, 0);
        LocalTime graceEnd = LocalTime.of(8, 10);
        LocalTime end = LocalTime.of(17, 0);

        if (!logIn.isAfter(graceEnd)) {
            logIn = start;
        }

        if (logIn.isBefore(start)) {
            logIn = start;
        }

        if (logOut.isAfter(end)) {
            logOut = end;
        }

        long minutes = Duration.between(logIn, logOut).toMinutes();

        LocalTime lunchStart = LocalTime.of(12, 0);
        LocalTime lunchEnd = LocalTime.of(13, 0);

        if (logOut.isAfter(lunchStart) && logIn.isBefore(lunchEnd)) {
            minutes -= 60;
        }

        return Math.max(minutes, 0);
    }

    // ===========================
    // Convert minutes to HH:mm
    // ===========================
    public static String formatMinutesToTime(long totalMinutes) {
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        return String.format("%02d:%02d", hours, minutes);
    }

    // ===========================
    // Government & Tax Calculations
    // ===========================
    public static double computePH(double salary) {
        double premium = salary * 0.03;

        if (premium < 300) {
            premium = 300;
        }
        if (premium > 1800) {
            premium = 1800;
        }

        return premium * 0.5;
    }

    public static double computeSSS(double salary) {
        if (salary < 3250) {
            return 135;
        }

        int bracket = (int) ((salary - 3250) / 500);
        double contribution = 135 + (bracket * 22.5);

        if (contribution > 1125) {
            contribution = 1125;
        }

        return contribution;
    }

    public static double computePagibig(double salary) {
        double baseSalary = (salary > 5000) ? 5000 : salary;
        double rate = (baseSalary <= 1500) ? 0.01 : 0.02;
        return baseSalary * rate;
    }

    public static double computeIncomeTax(double income) {
        if (income <= 20832) {
            return 0;
        } else if (income <= 33332) {
            return (income - 20832) * 0.20;
        } else if (income <= 66666) {
            return 2500 + (income - 33332) * 0.25;
        } else if (income <= 166666) {
            return 10833 + (income - 66666) * 0.30;
        } else if (income <= 666666) {
            return 40833.33 + (income - 166666) * 0.32;
        } else {
            return 200833.33 + (income - 666666) * 0.35;
        }
    }
}