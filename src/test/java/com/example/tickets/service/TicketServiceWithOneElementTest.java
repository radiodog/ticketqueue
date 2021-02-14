package com.example.tickets.service;

import com.example.tickets.domain.dto.TicketDto;
import com.example.tickets.domain.enums.TicketState;
import com.example.tickets.domain.enums.TicketTheme;
import com.example.tickets.domain.model.Ticket;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TicketServiceWithOneElementTest {

    @Autowired
    TicketService ticketService;

    @Before
    public void addOneTicket(){
        TicketDto ticketDto = new TicketDto();
        ticketDto.setTicketTheme(TicketTheme.ISSUE_DOCS);
        ticketService.addTicket(ticketDto);
    }

    @After
    public void removeTicket(){
        ticketService.removeTicketById(1);
    }

    @Test
    public void addTicketShouldSaveTicketWithIdOne(){
        Assert.assertEquals(ticketService.getTicketById(1).getId(), Integer.valueOf(1));
    }

    @Test
    public void  updateTicketShouldChangeModifiedDateChanging(){
        Ticket ticket = ticketService.getTicketById(1);
        LocalDateTime modifiedDate = ticket.getModifiedDate();
        TicketDto ticketDto = new TicketDto();
        ticketDto.setId(1);
        ticketDto.setTicketTheme(TicketTheme.ISSUE_DOCS);
        ticketDto.setTicketState(TicketState.CALL);
        ticketDto.setWindowNumber(1);
        ticketService.updateTicket(ticketDto);
        Ticket updatedTicked = ticketService.getTicketById(1);
        Assert.assertNotEquals(modifiedDate, updatedTicked.getModifiedDate());
    }

    @Test(expected = ResponseStatusException.class)
    public void updateTicketShouldThrowExceptionWithArgumentTwo(){
        TicketDto ticketDto = new TicketDto();
        ticketDto.setId(2);
        ticketDto.setTicketTheme(TicketTheme.ISSUE_DOCS);
        ticketDto.setTicketState(TicketState.CALL);
        ticketDto.setWindowNumber(1);
        ticketService.updateTicket(ticketDto);
    }

    @Test(expected = ResponseStatusException.class)
    public void updateTicketShouldThrowExceptionIfWindowNumberNotSet(){
        TicketDto ticketDto = new TicketDto();
        ticketDto.setId(1);
        ticketDto.setTicketTheme(TicketTheme.ISSUE_DOCS);
        ticketDto.setTicketState(TicketState.CALL);
        ticketDto.setWindowNumber(null);
        ticketService.updateTicket(ticketDto);
    }

    @Test(expected = ResponseStatusException.class)
    public void updateTicketShouldThrowExceptionIfTicketStateComplete(){
        TicketDto ticketDto = new TicketDto();
        ticketDto.setId(1);
        ticketDto.setTicketTheme(TicketTheme.ISSUE_DOCS);
        ticketDto.setTicketState(TicketState.COMPLETE);
        ticketService.updateTicket(ticketDto);
    }

    @Test(expected = ResponseStatusException.class)
    public void updateTicketShouldThrowExceptionIfTicketStateServed(){
        TicketDto ticketDto = new TicketDto();
        ticketDto.setId(1);
        ticketDto.setTicketTheme(TicketTheme.ISSUE_DOCS);
        ticketDto.setTicketState(TicketState.SERVED);
        ticketService.updateTicket(ticketDto);
    }

    @Test(expected = ResponseStatusException.class)
    public void updateTicketShouldThrowExceptionIfTicketStateHold(){
        TicketDto ticketDto = new TicketDto();
        ticketDto.setId(1);
        ticketDto.setTicketTheme(TicketTheme.ISSUE_DOCS);
        ticketDto.setTicketState(TicketState.HOLD);
        ticketService.updateTicket(ticketDto);
    }

    @Test
    public void updateTicketShouldChangeStateToCall(){
        TicketDto ticketDto = new TicketDto();
        ticketDto.setId(1);
        ticketDto.setTicketTheme(TicketTheme.ISSUE_DOCS);
        ticketDto.setTicketState(TicketState.CALL);
        ticketDto.setWindowNumber(1);
        ticketService.updateTicket(ticketDto);
        Ticket updatedTicket = ticketService.getTicketById(1);
        Assert.assertEquals(TicketState.CALL, updatedTicket.getTicketState());
    }

    @Test
    public void getTicketsShouldReturnListWithOneElement(){
        List<Ticket> tickets = ticketService.getTickets();
        Assert.assertEquals(1, tickets.size());
    }

    @Test
    public void getTicketByIdShouldReturnOneTicketWithIdOne(){
        Ticket ticket = ticketService.getTicketById(1);
        Assert.assertEquals(Integer.valueOf(1),ticket.getId());
    }

    @Test(expected = ResponseStatusException.class)
    public void getTicketByIdShouldThrowExceptionWithArgumentTwo(){
        ticketService.getTicketById(2);
    }

    @Test(expected = ResponseStatusException.class)
    public void removeTicketByIdShouldThrowExceptionWithArgumentTwo(){
        ticketService.removeTicketById(2);
    }
}
