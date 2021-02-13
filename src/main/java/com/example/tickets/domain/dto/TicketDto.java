package com.example.tickets.domain.dto;

import com.example.tickets.domain.enums.TicketState;
import com.example.tickets.domain.enums.TicketTheme;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketDto {

    private Integer id;
    private TicketTheme ticketTheme;
    private TicketState ticketState;
    private Integer windowNum;

}
