package com.vahidya;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense {
    public static int LAST_ID;
    private int id;
    private String description;
    private String category;
    private float amount;

    public Expense(String description, String category, float amount) {
        this.id=++LAST_ID;
        this.description = description;
        this.category = category;
        this.amount = amount;
    }
}
