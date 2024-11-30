package com.vahidya;


import java.util.HashSet;
import java.util.Set;

public class Expense_Category {
    private static final Set<String>  expense_categories= new HashSet<>();
    public static void addCategory(String newCategory){
        expense_categories.add(newCategory);
    }
    public static boolean containCategory(String category){
        return expense_categories.contains(category);
    }

}
