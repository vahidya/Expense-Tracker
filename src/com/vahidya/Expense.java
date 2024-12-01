package com.vahidya;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Expense {
    public static int LAST_ID;
    private int id;
    private String description;
    private String category;
    private float amount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;

    public Expense(String description, String category, float amount) {
        this.id=++LAST_ID;
        this.description = description;
        this.category = category;
        this.amount = amount;
        this.dateTime=LocalDateTime.now();
    }
}
