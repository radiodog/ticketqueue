package com.example.tickets.domain.enums;

public enum TicketTheme {
    SUBMIT_DOCS("Подача документов"),
    ISSUE_DOCS("Выдача документов");

    private String name;
    public String getName(){
        return name;
    }
    TicketTheme(String name){
        this.name = name;
    }
}
