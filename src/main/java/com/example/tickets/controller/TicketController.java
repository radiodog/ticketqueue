package com.example.tickets.controller;

import com.example.tickets.domain.dto.TicketDto;
import com.example.tickets.domain.model.Ticket;
import com.example.tickets.service.TicketService;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/service")
public class TicketController {

    @Autowired
    TicketService ticketService;

    @PostMapping("/ticket")
    public void addTicket(@RequestBody TicketDto ticketDto){
        ticketService.addTicket(ticketDto);
    }

    @PutMapping("/ticket")
    public void updateTicket(@RequestBody TicketDto ticketDto){
        ticketService.updateTicket(ticketDto);
    }

    @GetMapping("/ticket")
    public List<Ticket> getTickets(){
        return ticketService.getTickets();
    }

    @GetMapping("/ticket/{id}")
    public Ticket getById(@PathVariable("id") Integer id){
        return ticketService.getTicketById(id);
    }

    @DeleteMapping("/ticket/{id}")
    public void removeTicket(@PathVariable("id") Integer id){
        ticketService.removeTicketById(id);
    }
}
