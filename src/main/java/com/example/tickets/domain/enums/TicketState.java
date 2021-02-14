package com.example.tickets.domain.enums;

public enum TicketState {
    CREATED("создан"),
    CALL("вызывается"),
    SERVED("обслуживается"),
    HOLD("отложен"),
    COMPLETE("обслуживание завершено");

    private String name;
    public String getName(){
        return name;
    }
    TicketState(String name){
        this.name = name;
    }
}
