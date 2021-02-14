package com.example.tickets.domain.model;

import com.example.tickets.domain.enums.TicketState;
import com.example.tickets.domain.enums.TicketTheme;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Ticket {

    private final Integer id;
    @JsonFormat(pattern = "YYYY-MM-dd HH:mm:ss")
    private final LocalDateTime createdDate;
    private TicketTheme ticketTheme;
    private TicketState ticketState;
    private Integer windowNumber;
    @JsonFormat(pattern = "YYYY-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDate;

    public Ticket(Integer id, LocalDateTime createdDate){
        this.id = id;
        this.createdDate = createdDate;
    }
}

