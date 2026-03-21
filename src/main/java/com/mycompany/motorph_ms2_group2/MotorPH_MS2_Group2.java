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
    // Stores the file paths for employee and attendance CSVs.
    // The program reads these files to find and display employee details.
    // Declared 'static final' to keep paths constant during execution.
    private static final String Employee_CSV =
"resources/MotorPH_Employee Data - Employee Details.csv";

private static final String Attendance_CSV =
"resources/MotorPH_Employee Data - Attendance Record.csv";
    
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        
        // ===========================
        // Login Portal
        // ===========================
        // Prompts the user for username and password, checks credentials,
        // and directs them to the appropriate menu if login is successful.
        
        // Ask the user to enter their username
        
        System.out.println("==================================");
        System.out.print("Enter Username: ");
        String username= input.nextLine();
        
        // Ask the user to enter their password
        System.out.print("Enter Password: ");
        String password= input.nextLine();
        
       // Check if the entered credentials are correct
       // Only "employee" or "payroll_staff" with password "12345" are allowed
        if (!(username.equals("employee") || username.equals("payroll_staff")) || !password.equals("12345")) {
            // If credentials are incorrect, show message and terminate program
            System.out.println("\nIncorrect username and/or password.");
            System.out.println("\nProgram terminated.");
            input.close(); // close the Scanner to free resources
            return; // exit from the method
        }
        
        // If credentials are correct, show login success message
        System.out.println("Login Successful!");
        
        // Direct the user to the appropriate menu based on username
        if (username.equals("employee")) {
            employeeMenu(input, Employee_CSV); // show employee menu
        } else {
           payrollStaffMenu(input, Employee_CSV, Attendance_CSV); // show payroll staff menu
        }

        // Close the Scanner after finishing input
        input.close();
    }
    // ===========================
    // Employee Menu
    // ===========================
    // This section allows employees to view their personal details
    // by entering their employee number. It reads the data from
    // a CSV file and displays the employee's number, name, and birthday.
    
    public static void employeeMenu(Scanner input, String employeeFile) {
        
        // Display the employee menu options
        System.out.println("==================================");
        System.out.println("\nEmployee Menu");
        System.out.println("1. Enter your employee number");
        System.out.println("2. Exit");
        System.out.print("\nChoose option: ");
        
        // Get the user's menu choice
        int choice = input.nextInt();
        input.nextLine(); // consume the leftover newline

        if (choice == 1) {
            
            // Ask the user to enter their employee number
            System.out.println("\n==================================");
            System.out.print("\nEnter your employee number: ");
            String empNumber = input.nextLine();
            
            // Retrieve employee data from the file
            String[] empData = getEmployeeData(employeeFile, empNumber);
        if (empData == null) {
            
            // Employee number not found in the file
            System.out.println("Employee number does not exist.");
        } else {
            
            // Display employee details if found
            System.out.println("\n==================================");
            System.out.println("Employee Details:");
            // empData[0] -> Employee Number
            System.out.println("Employee #: " + empData[0]);
            // empData[1] -> Last Name, empData[2] -> First Name
            System.out.println("Employee Name: " + empData[2] + " " + empData[1]);
            // empData[3] -> Birthday
            System.out.println("Birthday: " + empData[3]);
            System.out.println("==================================");
            }
        } else {
            // If user chooses any other option, exit program
            System.out.println("\nProgram terminated.");
        }
    }
    
        // Method to get employee data from a CSV file
    public static String[] getEmployeeData(String employeeFile, String empNumber) {
        try (BufferedReader br = new BufferedReader(new FileReader(employeeFile))) {
            br.readLine(); // // skip the header line (column names)
            String line;
            
            // Read each line from the file
            while ((line = br.readLine()) != null) {
                // Split the line into fields using comma as separator
                String[] fields = line.split(",");
                
                // fields[0] -> Employee Number
                // fields[1] -> Last Name
                // fields[2] -> First Name
                // fields[3] -> Birthday
                // Check if the first field (employee number) matches input
                if (fields[0].trim().equals(empNumber)) return fields;
            }
        } catch (IOException e) {
            
            // Display error if the file cannot be read
            System.out.println("Error reading employee file.");
        }
        return null; // return null if employee not found
    }
    
    // ===========================
    // Payroll Staff Menu
    // ===========================
    // This method displays a menu for payroll staff and lets them choose
    // whether to process payroll for one employee or all employees.

    public static void payrollStaffMenu(Scanner input, String employeeFile, String attendanceFile) {
        
        // Display main payroll menu options
        System.out.println("\n==================================");
        System.out.println("\nPayroll Menu");
        System.out.println("1. Process Payroll");
        System.out.println("2. Exit");
        System.out.print("\nChoose option: ");
        int payrollChoice = input.nextInt();
        input.nextLine(); // consume newline
        
        // Ask user to choose an option
        if (payrollChoice == 1) {
            
            // Show sub-menu for payroll processing options
            System.out.println("\n==================================");
            System.out.println("\nProcess Payroll");
            System.out.println("\n1. One employee");
            System.out.println("2. All employees");
            System.out.println("3. Exit");
            System.out.println("\n==================================");
            
            // Ask user to choose an option
            System.out.print("\nChoose option: ");
            int option = input.nextInt();
            input.nextLine(); // consume newline
            
        // If user chooses to process payroll
            if (option == 1) {
                
                // Ask for employee number
                System.out.print("\nEnter employee number: ");
                String empNumber = input.nextLine();
                
                // Call method to process payroll for one employee
                processPayrollForOneMonthly(empNumber, employeeFile, attendanceFile);
                
        // If user chooses to process payroll for all employees
            } else if (option == 2) {
                
                 // Call method to process payroll for all employees
                processPayrollForAll(employeeFile, attendanceFile);
            } else {
                
                // If user selects exit
                System.out.println("Program terminated.");
            }
        } else {
            
                // If user selects exit from main menu
            System.out.println("Program terminated.");
        }
    }
    
    // ===========================
    // Attendance Data Retrieval
    // ===========================
    // Reads the attendance CSV file and collects all records for a specific employee.

    public static String[][] getEmployeeAttendanceRecords(String attendanceFile, String empNumber) {
        // Create a list to store the matching attendance records
        List<String[]> records = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(attendanceFile))) {
            br.readLine(); // Skip the header line (column names)
            String line;
            
            // Read each line from the file
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // Skip empty lines
                
                // Split the line into fields using comma as the delimiter
                String[] fields = line.split(","); // <-- comma delimiter
                
                // Remove any extra spaces from each field
                for (int i = 0; i < fields.length; i++) fields[i] = fields[i].trim();
                
                // Skip lines that don't have enough fields
                if (fields.length < 6) continue;
                
                // If the employee number matches, add the record to the list
                if (fields[0].equals(empNumber)) records.add(fields);
            }
        } catch (IOException e) {
            // Show an error message if the file cannot be read
            System.out.println("Error reading attendance file: " + e.getMessage());
        }
        
        // Convert the list to a 2D array and return it
        return records.toArray(new String[0][]);
    }
    
    // ===========================
    // Payroll Processing - One Employee
    // ===========================
    // Calculates and displays the payroll for a single employee
    // for each month from June to December. It shows total hours,
    // gross salary, deductions, and net salary for each cutoff.
    
    public static void processPayrollForOneMonthly(String empNumber, String employeeFile, String attendanceFile) {
        // Get employee data from the CSV file
        String[] empData = getEmployeeData(employeeFile, empNumber);
        if (empData == null) {
            System.out.println("Employee number does not exist."); // Employee not found
            return;
        }

        // Get hourly rate from the employee data (last field)
        double hourlyRate = Double.parseDouble(empData[empData.length - 1].trim());
        // Get all attendance records for this employee
        String[][] attendanceRecords = getEmployeeAttendanceRecords(attendanceFile, empNumber);

        // Display basic employee info
        System.out.println("\nEmployee #: " + empData[0]);
        System.out.println("Employee Name: " + empData[2] + " " + empData[1]);
        System.out.println("Birthday: " + empData[3]);

        // Formatter to parse dates from the attendance records
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        // Loop through each month from June (6) to December (12)
        for (int month = 6; month <= 12; month++) { 
            Month monthEnum = Month.of(month);
            String monthName = monthEnum.name();

            // ---------- First cutoff 1–15 ----------
            long totalMinutes1 = 0;
            for (String[] record : attendanceRecords) {
                if (record.length < 6) continue; // Skip invalid records
                LocalDate date;
                try {
                    date = LocalDate.parse(record[3].trim(), dateFormatter); // Parse attendance date
                } catch (Exception e) {
                    continue; // Skip if date is invalid
                }
                if (date.getMonthValue() != month) continue; // Skip other months
                int day = date.getDayOfMonth();
                if (day < 1 || day > 15) continue; // Only consider days 1–15
                totalMinutes1 += calculateDailyMinutes(record[4].trim(), record[5].trim()); // Add daily worked minutes
            }

            // Convert total minutes to decimal hours for salary computation
            double hoursDecimal1 = totalMinutes1 / 60.0;
            double gross1 = hoursDecimal1 * hourlyRate; // Calculate gross pay for first cutoff

            // Display first cutoff payroll info
            System.out.println("\nMonth: " + monthName);
            System.out.println("Cutoff Date: " + monthName + " 1 to " + monthName + " 15");
            System.out.println("Total Hours Worked: " + formatMinutesToTime(totalMinutes1));
            System.out.println("Gross Salary: ₱" + String.format("%.2f", gross1));
            System.out.println("Net Salary: ₱" + String.format("%.2f", gross1)); // No deductions for first cutoff
            System.out.println("--------------------------------");

            // ---------- Second cutoff 16–end ----------
            long totalMinutes2 = 0;
            for (String[] record : attendanceRecords) {
                if (record.length < 6) continue; // Skip invalid records
                LocalDate date;
                try {
                    date = LocalDate.parse(record[3].trim(), dateFormatter); // Parse date
                } catch (Exception e) {
                    continue; // Skip invalid dates
                }
                if (date.getMonthValue() != month) continue; // Skip other months
                int day = date.getDayOfMonth();
                if (day < 16) continue; // Only consider days 16–end
                totalMinutes2 += calculateDailyMinutes(record[4].trim(), record[5].trim()); // Add daily worked minutes
            }

            // Convert total minutes to decimal hours for salary computation
            double hoursDecimal2 = totalMinutes2 / 60.0;
            double gross2 = hoursDecimal2 * hourlyRate; // Gross pay for second cutoff

            // Calculate mandatory deductions for second cutoff
            double grossTotal = gross1 + gross2;

            // Compute deductions based on TOTAL monthly salary
            double sss = computeSSS(grossTotal);
            double philHealth = computePH(grossTotal);
            double pagibig = computePagibig(grossTotal);
            double tax = computeIncomeTax(grossTotal);

            // Total deductions
            double totalDeductions = sss + philHealth + pagibig + tax;

            // Final net salary for the whole month
            double gross3 = grossTotal - totalDeductions;
            

            // Display second cutoff payroll info
            System.out.println("Cutoff Date: " + monthName + " 16 to " + monthName + " " + monthEnum.length(LocalDate.now().isLeapYear()));
            System.out.println("Total Hours Worked: " + formatMinutesToTime(totalMinutes2));
            System.out.println("Gross Salary: ₱" + String.format("%.2f", gross2));
            System.out.println("Each Deduction:");
            System.out.println("SSS: ₱" + String.format("%.2f", sss));
            System.out.println("PhilHealth: ₱" + String.format("%.2f", philHealth));
            System.out.println("Pag-IBIG: ₱" + String.format("%.2f", pagibig));
            System.out.println("Tax: ₱" + String.format("%.2f", tax));
            System.out.println("Total Deductions: ₱" + String.format("%.2f", totalDeductions));
            System.out.println("Final Net Salary: ₱" + String.format("%.2f", gross3));
            System.out.println("--------------------------------");
        }
        
    }
    
    // ===========================
    // Payroll Processing - All Employees
    // ===========================
    // Reads the employee CSV file and processes payroll for every employee.
    // It calls 'processPayrollForOneMonthly' for each employee in the file.
    
    public static void processPayrollForAll(String employeeFile, String attendanceFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(employeeFile))) {
            br.readLine(); // Skip the header line (column names)
            String line;
            
            // Read each employee record line by line
            while ((line = br.readLine()) != null) {
                // Split the line into fields (Employee Number, Name, etc.)
                String[] empData = line.split(",");
                String empNumber = empData[0].trim(); // Get the employee number
                
                System.out.println("--------------------------------------------------"); // Separator for readability
                
                // Process payroll for this employee
                processPayrollForOneMonthly(empNumber, employeeFile, attendanceFile);
            }
        } catch (IOException e) {
            // Show an error message if the file cannot be read
            System.out.println("Error reading employee file.");
        }
    }
    
    // ===========================
    // Daily Hours Calculation (clamped 8:00–17:00)
    // ===========================
    // Calculates total worked minutes for a day based on log-in and log-out times.
    // Times are adjusted to the official working hours (8:00–17:00) and include
    // a 10-minute grace period for arrival. Deducts 1-hour lunch if applicable.
    
    public static long calculateDailyMinutes(String logInStr, String logOutStr) {
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");

    LocalTime logIn;
    LocalTime logOut;

    // Convert log-in and log-out strings to LocalTime objects
    try {
        logIn = LocalTime.parse(logInStr, timeFormatter);
        logOut = LocalTime.parse(logOutStr, timeFormatter);
    } catch (Exception e) {
        return 0; // Return 0 if times are invalid
    }

    // Define official working hours
    LocalTime start = LocalTime.of(8, 0); // Start of work
    LocalTime graceEnd = LocalTime.of(8, 10); // 10-min grace period
    LocalTime end = LocalTime.of(17, 0); // End of work

    // Apply grace period: if within 8:00–8:10, treat as 8:00
    if (!logIn.isAfter(graceEnd)) {
        logIn = start;
    }

    // Clamp log-in and log-out within working hours
    if (logIn.isBefore(start)) logIn = start;
    if (logOut.isAfter(end)) logOut = end;

    // Compute total worked minutes
    long minutes = Duration.between(logIn, logOut).toMinutes();

    // ===========================
    // Deduct 1-hour lunch (12:00–13:00)
    // ===========================
    LocalTime lunchStart = LocalTime.of(12, 0);
    LocalTime lunchEnd = LocalTime.of(13, 0);

    // Deduct 1 hour if employee worked across the lunch period
    if (logOut.isAfter(lunchStart) && logIn.isBefore(lunchEnd)) {
        minutes -= 60; // Deduct lunch break
    }

    // Prevent negative minutes (in case log-out before log-in)
    return Math.max(minutes, 0);
}
    
    // ===========================
    // Time Formatting
    // ===========================
    // Converts total worked minutes into HH:mm format for display.
    
    public static String formatMinutesToTime(long totalMinutes) {
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        return String.format("%d:%02d", hours, minutes);
    }
    
    // ===========================
    // Government & Tax Calculations
    // ===========================
    // Calculates mandatory government contributions and income tax for an employee,
    // including PhilHealth, SSS, Pag-IBIG, and Philippine income tax based on salary.
    
    public static double computePH(double salary) {
    /*
     * PhilHealth Contribution:
     * - 3% of the salary
     * - Minimum premium = 300
     * - Maximum premium = 1800
     * - Employee only pays 50% of the total premium
     */

    double premium = salary * 0.03;

    // Apply minimum and maximum limits
    if (premium < 300) premium = 300;
    if (premium > 1800) premium = 1800;

    // Return employee share (50%)
    return premium * 0.5;
}

    public static double computeSSS(double salary) {
    /*
     * SSS Contribution:
     * - Based on salary brackets
     * - Starts at 135 for salaries below 3250
     * - Increases by 22.5 for every 500 increase in salary
     * - Maximum contribution is 1125
     */

    if (salary < 3250) return 135;

    // Compute which bracket the salary falls into
    int bracket = (int) ((salary - 3250) / 500);

    // Compute contribution based on bracket
    double contribution = 135 + (bracket * 22.5);

    // Apply maximum cap
    if (contribution > 1125) contribution = 1125;

    return contribution;
}

    public static double computePagibig(double salary) {
    /*
     * Pag-IBIG Contribution:
     * - Salary is capped at 5000
     * - If salary ≤ 1500 → 1%
     * - If salary > 1500 → 2%
     */

    // Apply salary cap
    double baseSalary = (salary > 5000) ? 5000 : salary;

    // Determine contribution rate
    double rate = (baseSalary <= 1500) ? 0.01 : 0.02;

    return baseSalary * rate;
}

    public static double computeIncomeTax(double income) {
    /*
     * Income Tax (Philippine Tax Table):
     * - Different tax rates depending on income range
     * - Each bracket has a base tax + percentage of excess
     */

    if (income <= 20832) {
        return 0; // no tax
    }
    else if (income <= 33332) {
        return (income - 20832) * 0.20;
    }
    else if (income <= 66666) {
        return 2500 + (income - 33332) * 0.25;
    }
    else if (income <= 166666) {
        return 10833 + (income - 66666) * 0.30;
    }
    else if (income <= 666666) {
        return 40833.33 + (income - 166666) * 0.32;
    }
    else {
        return 200833.33 + (income - 666666) * 0.35;
    }
    
}
}