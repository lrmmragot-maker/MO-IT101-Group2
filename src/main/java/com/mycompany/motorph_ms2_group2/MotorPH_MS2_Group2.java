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
"/Users/allerizajamsuli/NetBeansProjects/MotorPH_MS2_Group2/resources/MotorPH_Employee Data - Employee Details.csv";

private static final String Attendance_CSV =
"/Users/allerizajamsuli/NetBeansProjects/MotorPH_MS2_Group2/resources/MotorPH_Employee Data - Attendance Record.csv";
    
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        
        //Login Portal
        System.out.print("Enter Username: ");
        String username= input.nextLine();
        System.out.print("Enter Password: ");
        String password= input.nextLine();
        
        //Condition if credential is correct
        
        if (!(username.equals("employee") || username.equals("payroll_staff")) || !password.equals("12345")) {
            System.out.println("Incorrect username and/or password.");
            System.out.println("Program terminated.");
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
        System.out.println("\nEmployee Menu");
        System.out.println("1. Enter your employee number");
        System.out.println("2. Exit");
        System.out.print("Choose option: ");
        int choice = input.nextInt();
        input.nextLine(); // consume newline

        if (choice == 1) {
            System.out.print("Enter your employee number: ");
            String empNumber = input.nextLine();
            String[] empData = getEmployeeData(employeeFile, empNumber);
        if (empData == null) {
                System.out.println("Employee number does not exist.");
            } else {
                System.out.println("\nEmployee Details:");
                System.out.println("Employee #: " + empData[0]);
                System.out.println("Employee Name: " + empData[2] + " " + empData[1]);
                System.out.println("Birthday: " + empData[3]);
            }
        } else {
            System.out.println("Program terminated.");
        }
    }
    
    public static String[] getEmployeeData(String employeeFile, String empNumber) {
        try (BufferedReader br = new BufferedReader(new FileReader(employeeFile))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields[0].trim().equals(empNumber)) return fields;
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
        System.out.println("\nPayroll Menu");
        System.out.println("1. Process Payroll");
        System.out.println("2. Exit");
        System.out.print("Choose option: ");
        int payrollChoice = input.nextInt();
        input.nextLine(); // consume newline

        if (payrollChoice == 1) {
            System.out.println("\nProcess Payroll");
            System.out.println("1. One employee");
            System.out.println("2. All employees");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");
            int option = input.nextInt();
            input.nextLine(); // consume newline

            if (option == 1) {
                System.out.print("Enter employee number: ");
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
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] fields = line.split(","); // <-- comma delimiter
                for (int i = 0; i < fields.length; i++) fields[i] = fields[i].trim();
                if (fields.length < 6) continue;
                if (fields[0].equals(empNumber)) records.add(fields);
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

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy"); // Corrected format
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");

        for (int month = 6; month <= 12; month++) { // June to December
            Month monthEnum = Month.of(month);
            String monthName = monthEnum.name();

            // ---------- First cutoff 1–15 ----------
            double hours1 = 0;
            for (String[] record : attendanceRecords) {
                if (record.length < 6) continue;
                LocalDate date;
                try {
                    date = LocalDate.parse(record[3].trim(), dateFormatter);
                } catch (Exception e) {
                    continue; // skip invalid dates
                }
                if (date.getMonthValue() != month) continue;
                int day = date.getDayOfMonth();
                if (day < 1 || day > 15) continue;
                hours1 += calculateDailyHours(record[4].trim(), record[5].trim());
            }
            double gross1 = hours1 * hourlyRate;
            System.out.println("\nMonth: " + monthName);
            System.out.println("Cutoff Date: " + monthName + " 1 to " + monthName + " 15");
            System.out.println("Total Hours Worked: " + Math.round(hours1 * 100.0) / 100.0);
            System.out.println("Gross Salary: ₱" + Math.round(gross1 * 100.0) / 100.0);
            System.out.println("Net Salary: ₱" + Math.round(gross1 * 100.0) / 100.0);
            System.out.println("--------------------------------");

            // ---------- Second cutoff 16–end ----------
            double hours2 = 0;
            for (String[] record : attendanceRecords) {
                if (record.length < 6) continue;
                LocalDate date;
                try {
                    date = LocalDate.parse(record[3].trim(), dateFormatter);
                } catch (Exception e) {
                    continue; // skip invalid dates
                }
                if (date.getMonthValue() != month) continue;
                int day = date.getDayOfMonth();
                if (day < 16) continue;
                hours2 += calculateDailyHours(record[4].trim(), record[5].trim());
            }
            double gross2 = hours2 * hourlyRate;

            // Only apply deductions on 16–end cutoff
            double sss = computeSSS(gross2);
            double philHealth = computePH(gross2);
            double pagibig = computePagibig(gross2);
            double tax = computeIncomeTax(gross2);
            double totalDeductions = sss + philHealth + pagibig + tax;
            double net2 = gross2 - totalDeductions;

            System.out.println("Cutoff Date: " + monthName + " 16 to " + monthName + " " + monthEnum.length(LocalDate.now().isLeapYear()));
            System.out.println("Total Hours Worked: " + Math.round(hours2 * 100.0) / 100.0);
            System.out.println("Gross Salary: ₱" + Math.round(gross2 * 100.0) / 100.0);
            System.out.println("Each Deduction:");
            System.out.println("SSS: ₱" + Math.round(sss * 100.0) / 100.0);
            System.out.println("PhilHealth: ₱" + Math.round(philHealth * 100.0) / 100.0);
            System.out.println("Pag-IBIG: ₱" + Math.round(pagibig * 100.0) / 100.0);
            System.out.println("Tax: ₱" + Math.round(tax * 100.0) / 100.0);
            System.out.println("Total Deductions: ₱" + Math.round(totalDeductions * 100.0) / 100.0);
            System.out.println("Net Salary: ₱" + Math.round(net2 * 100.0) / 100.0);
            System.out.println("--------------------------------");
        }
        
    }
    
    // ===========================
    // Payroll Processing - All Employees
    // ===========================
    public static void processPayrollForAll(String employeeFile, String attendanceFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(employeeFile))) {
            br.readLine(); // skip header
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
    // Daily Hours Calculation (clamped 8:00–17:00)
    // ===========================
    public static double calculateDailyHours(String logInStr, String logOutStr) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");
        LocalTime logIn;
        LocalTime logOut;
        try {
            logIn = LocalTime.parse(logInStr, timeFormatter);
            logOut = LocalTime.parse(logOutStr, timeFormatter);
        } catch (Exception e) {
            return 0; // skip invalid time
        }

        LocalTime start = LocalTime.of(8, 0);  // 8:00 AM
        LocalTime end = LocalTime.of(17, 0);   // 5:00 PM

        if (logIn.isBefore(start)) logIn = start;
        if (logOut.isAfter(end)) logOut = end;

        double hours = Duration.between(logIn, logOut).toMinutes() / 60.0;
        return Math.max(hours, 0); // prevent negative hours
    }
    
    
    
     // ===========================
    // Government & Tax Calculations
    // ===========================
    public static double computePH(double salary) {
        double PHpremium;
        if (salary <= 10000) PHpremium = 300;
        else if (salary >= 60000) PHpremium = 1800;
        else PHpremium = salary * 0.03;
        return PHpremium * 0.5;
    }

    public static double computeSSS(double salary) {
        if (salary < 3250) return 135;
        else if (salary <= 3750) return 157.5;
        else if (salary <= 4250) return 180;
        else if (salary <= 4750) return 202.5;
        else if (salary <= 5250) return 225;
        else if (salary <= 5750) return 247.5;
        else if (salary <= 6250) return 270;
        else if (salary <= 6750) return 292.5;
        else if (salary <= 7250) return 315;
        else if (salary <= 7750) return 337.5;
        else if (salary <= 8250) return 360;
        else if (salary <= 8750) return 382.5;
        else if (salary <= 9250) return 405;
        else if (salary <= 9750) return 427.5;
        else if (salary <= 10250) return 450;
        else if (salary <= 10750) return 472.5;
        else if (salary <= 11250) return 495;
        else if (salary <= 11750) return 517.5;
        else if (salary <= 12250) return 540;
        else if (salary <= 12750) return 562.5;
        else if (salary <= 13250) return 585;
        else if (salary <= 13750) return 607.5;
        else if (salary <= 14250) return 630;
        else if (salary <= 14750) return 652.5;
        else if (salary <= 15250) return 675;
        else if (salary <= 15750) return 697.5;
        else if (salary <= 16250) return 720;
        else if (salary <= 16750) return 742.5;
        else if (salary <= 17250) return 765;
        else if (salary <= 17750) return 787.5;
        else if (salary <= 18250) return 810;
        else if (salary <= 18750) return 832.5;
        else if (salary <= 19250) return 855;
        else if (salary <= 19750) return 877.5;
        else if (salary <= 20250) return 900;
        else if (salary <= 20750) return 922.5;
        else if (salary <= 21250) return 945;
        else if (salary <= 21750) return 967.5;
        else if (salary <= 22250) return 990;
        else if (salary <= 22750) return 1012.5;
        else if (salary <= 23250) return 1035;
        else if (salary <= 23750) return 1057.5;
        else if (salary <= 24250) return 1080;
        else if (salary <= 24750) return 1102.5;
        else return 1125;
    }

    public static double computePagibig(double salary) {
        double cappedSalary = salary > 5000 ? 5000 : salary;
        double rate = cappedSalary <= 1500 ? 0.01 : 0.02;
        return cappedSalary * rate;
    }

    public static double computeIncomeTax(double taxableIncome) {
        if (taxableIncome <= 20832) return 0;
        else if (taxableIncome <= 33332) return (taxableIncome - 20833) * 0.20;
        else if (taxableIncome <= 66666) return 2500 + (taxableIncome - 33333) * 0.25;
        else if (taxableIncome <= 166666) return 10833 + (taxableIncome - 66667) * 0.30;
        else if (taxableIncome <= 666666) return 40833.33 + (taxableIncome - 166667) * 0.32;
        else return 200833.33 + (taxableIncome - 666667) * 0.35;
    }

}
