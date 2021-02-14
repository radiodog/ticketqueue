package com.example.tickets.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InitTicketServiceTest {

    @Autowired
    TicketService ticketService;

    @Test(expected = ResponseStatusException.class)
    public void addTicketWithPassingNullArgumentShouldThrowException(){
        TicketService ticketService = new TicketService();
        ticketService.addTicket(null);
    }

    @Test( expected = ResponseStatusException.class)
    public void getTicketByIdWithPassingNullArgumentShouldThrowException() {
        TicketService ticketService = new TicketService();
        ticketService.getTicketById(null);
    }

    @Test
    public void getTicketsShouldReturnEmptyList(){
        TicketService ticketService = new TicketService();
        Assert.assertTrue(ticketService.getTickets().isEmpty());
    }

    @Test(expected = ResponseStatusException.class)
    public void removeTicketByIdWithPassingNullArgumentShouldThrowException(){
        TicketService ticketService = new TicketService();
        ticketService.removeTicketById(null);
    }

    @Test(expected = ResponseStatusException.class)
    public void updateTicketWithPassingNullArgumentShouldThrowException(){
        TicketService ticketService = new TicketService();
        ticketService.updateTicket(null);
    }
}
