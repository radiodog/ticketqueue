package com.example.tickets.domain.model;

import com.example.tickets.domain.enums.TicketState;
import com.example.tickets.domain.enums.TicketTheme;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Ticket {

    private final Integer id;
    private final LocalDateTime cratedDate;
    private TicketTheme ticketTheme;
    private TicketState ticketState;
    private Integer windowNumber;
    private LocalDateTime modifiedDate;

    public Ticket(Integer id, LocalDateTime cratedDate){
        this.id = id;
        this.cratedDate = cratedDate;
    }
}

