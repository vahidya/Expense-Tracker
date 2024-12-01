package com.vahidya;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jdk.jfr.Category;

import java.io.File;
import java.lang.ref.SoftReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        Scanner scanner=new Scanner(System.in);
        List<String> words;
        List<String> readCategoryList;
        System.out.println("Welcome to Expense Tracker Application!");
        System.out.println("Enter a command (type 'help' for available commands, or 'exit' to quit):");
        File exp_file= new File("expense.json");
        Path filePath_cat = Paths.get("category.txt");
        while (true){
            System.out.print("Expense-Tracker~");
            //read expense categories from file
            try{
                readCategoryList= Files.readAllLines(filePath_cat);
                for (String category:readCategoryList) Expense_Category.addCategory(category);
            }catch (Exception e){
                e.printStackTrace();
            }
            //read all of expense from file
            List<Expense> expenseList=readExpensesFromFile();
            if (!expenseList.isEmpty()){
                Expense.LAST_ID=expenseList.get(expenseList.size()-1).getId();
            }else {
                Expense.LAST_ID = 0;
            }
            String input =scanner.nextLine().trim();
            words= convertInputToWords(input);
            switch (words.get(0).toLowerCase()){
                case "help" :
                    System.out.println("Available commands:");
                    break;
                case "add_cat":
                    if (words.size()==2){
                        Expense_Category.addCategory(words.get(1));
                        System.out.println("Category \""+words.get(1)+"\" was added");
                        List<String> temp= new ArrayList<>();
                        temp.add(words.get(1));
                        try {
                            Files.write(filePath_cat,temp, StandardOpenOption.APPEND);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        System.out.println("add_cat command has a parameter to set name of your expense category");
                    }
                    break;
                case "cat_list":
                    if (words.size()==1){
                        System.out.println("list of expense categories:");
                        try {
                            readCategoryList=Files.readAllLines(filePath_cat);
                            readCategoryList.forEach(
                                    System.out::println
                            );
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        System.out.println("cat list has no parameter");
                    }
                    break;
                case "exp_list":
                    if (words.size()==1){
                        System.out.println("list of expenses:");
                        expenseList=readExpensesFromFile();
                        if (expenseList.isEmpty()){
                            System.out.println("there is no expense");
                        }else{
                            expenseList.forEach(expense -> System.out.println(expense.toString()));
                        }
                    }else{
                        System.out.println("exp_list has no parameter");
                    }
                    break;
                case "exp_filter":
                    if (words.size()==2){
                        List<String> finalWords = words;
                        expenseList=readExpensesFromFile();
                        if (expenseList.isEmpty()){
                            System.out.println("there is no expense");
                        }else{
                            expenseList.stream().filter(expense -> expense.getCategory().equals(finalWords.get(1)))
                                    .forEach(expense -> System.out.println(expense.toString()));
                        }
                    }else{
                        System.out.println("exp_filter has a parameter to determine category");
                    }
                    break;
                case "del_expense":
                    if (words.size()==2){
                        int id= Integer.parseInt(words.get(1));
                        expenseList=readExpensesFromFile();
                        if (expenseList.isEmpty()){
                            System.out.println("This ID does not exist!!!");
                        }else{
                            if(expenseList.removeIf(expense -> expense.getId()==id)){
                                ObjectMapper objectMapper = new ObjectMapper();
                                objectMapper.registerModule(new JavaTimeModule());
                                try {
                                    objectMapper.writerWithDefaultPrettyPrinter().writeValue(exp_file, expenseList);
                                    System.out.println("new expense with ID="+id+" was deleted.");
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }else{
                                System.out.println("This ID does not exist!!!");
                            }
                        }
                    }else{
                        System.out.println("del_expense has a parameter to determine id of expense");
                    }
                    break;
                case "summary":
                    if (words.size()==1){
                        expenseList=readExpensesFromFile();
                        if (expenseList.isEmpty()){
                            System.out.println("There is no expense!!!");
                        }else{
                            int sumOfAmount= expenseList.stream().mapToInt(expense -> (int) expense.getAmount()).sum();
                            System.out.println("total expense : $"+sumOfAmount);
                        }
                    }else{
                        if (words.size()==2){
                            String cat_filter=words.get(1);
                            expenseList=readExpensesFromFile();
                            if (expenseList.isEmpty()){
                                System.out.println("There is no expense!!!");
                            }else{
                                int sumOfAmount= expenseList.stream()
                                        .filter(expense -> expense.getCategory().equals(cat_filter))
                                        .mapToInt(expense -> (int) expense.getAmount()).sum();
                                System.out.println("total expense for cat" +cat_filter+" is : $"+sumOfAmount);
                            }
                        }else {
                            System.out.println("del_expense has a parameter to determine id of expense");
                        }

                    }
                    break;
                case "add_expense":
                    if (words.size()==7){
                        if (words.indexOf("--description")==-1) {
                            helpAboutAdd_expens();
                        }else{
                            if(words.indexOf("--amount")==-1){
                                helpAboutAdd_expens();
                            }else{
                                if (words.indexOf("--category")==-1){
                                    helpAboutAdd_expens();
                                }else{
                                    try{

                                        String cat=words.get(words.indexOf("--category")+1);
                                        String desc=words.get(words.indexOf("--description")+1);
                                        try {
                                            float amount=Float.parseFloat(words.get(words.indexOf("--amount")+1));
                                            if(Expense_Category.containCategory(cat)){
                                                Expense newExpense=new Expense(desc, cat, amount);
                                                expenseList.add(newExpense);
                                                ObjectMapper objectMapper = new ObjectMapper();
                                                objectMapper.registerModule(new JavaTimeModule());
                                                try {
                                                    objectMapper.writerWithDefaultPrettyPrinter().writeValue(exp_file, expenseList);
                                                    System.out.println("new expense with ID="+newExpense.getId()+" was added.");
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }
                                            }else {
                                                System.out.println("Category is not in list of categories");
                                            }
                                        }catch (Exception e){
                                            helpAboutAdd_expens();
                                        }

                                    }catch ( Exception e){
                                        helpAboutAdd_expens();
                                    }
                                }

                            }
                        }

                    }else{
                        helpAboutAdd_expens();
                    }
                    break;
                case "exit":
                    System.out.println("Goodbye!");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("command is not correct!please use help command.");
            }
        }
    }

    private static void helpAboutAdd_expens() {
        System.out.println("add_exp command has a structure like this :");
        System.out.println(" add_expense --description \"restaurant payment\" --amount 100 --category \"dinner\"  ");
    }

    private static List<Expense> readExpensesFromFile() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            File file= new File("expense.json");
            List<Expense> eList=objectMapper.readValue(file, new TypeReference<List<Expense>>() {});
            return eList;
        }catch (Exception e){
            return new ArrayList<>();
        }

    }

    private static List<String> convertInputToWords(String input) {
        Pattern pattern = Pattern.compile("\"([^\"]*)\"|(\\S+)");
        Matcher matcher = pattern.matcher(input);
        // Find matches
        List<String> words = new ArrayList<>();
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                // Group 1 captures the content inside quotes
                words.add(matcher.group(1));
            } else if (matcher.group(2) != null) {
                // Group 2 captures single words
                words.add(matcher.group(2));
            }
        }
        return words;
    }
}
