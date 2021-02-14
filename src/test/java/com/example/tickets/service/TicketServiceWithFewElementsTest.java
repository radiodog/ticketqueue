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
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TicketServiceWithFewElementsTest {

    @Autowired
    TicketService ticketService;

    @Before
    public void fillStorage(){
        TicketDto ticketDto1 = new TicketDto();
        ticketDto1.setTicketTheme(TicketTheme.ISSUE_DOCS);

        TicketDto ticketDto2 = new TicketDto();
        ticketDto2.setTicketTheme(TicketTheme.SUBMIT_DOCS);

        ticketService.addTicket(ticketDto1);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ticketService.addTicket(ticketDto2);
    }

    @After
    public void clearStorage(){
        List<Integer> ids = ticketService.getTickets().stream().map(Ticket::getId).collect(Collectors.toList());
        for (Integer id: ids){
            ticketService.removeTicketById(id);
        }
    }

    @Test
    public void addTicketShouldSaveTicketWithIdThree(){
        TicketDto ticketDto = new TicketDto();
        ticketDto.setTicketTheme(TicketTheme.ISSUE_DOCS);
        ticketService.addTicket(ticketDto);
        Ticket ticket = ticketService.getTicketById(3);
        Assert.assertNotNull(ticket);
    }

    @Test(expected = ResponseStatusException.class)
    public void updateMethodShouldThrowExceptionAfterTryingSetAnotherTicketToSameWindow(){
        TicketDto ticketDto1 = new TicketDto();
        ticketDto1.setId(1);
        ticketDto1.setTicketTheme(TicketTheme.ISSUE_DOCS);
        ticketDto1.setTicketState(TicketState.CALL);
        ticketDto1.setWindowNumber(1);

        TicketDto ticketDto2 = new TicketDto();
        ticketDto2.setId(2);
        ticketDto2.setTicketTheme(TicketTheme.SUBMIT_DOCS);
        ticketDto2.setTicketState(TicketState.CALL);
        ticketDto2.setWindowNumber(1);

        ticketService.updateTicket(ticketDto1);
        ticketService.updateTicket(ticketDto2);
    }

    @Test
    public void getTicketsShouldReturnListWithTwoElements(){
        Assert.assertEquals(2, ticketService.getTickets().size());
    }

    @Test
    public void getTicketsShouldReturnEmptyListAfterRemovingTickets(){
        ticketService.removeTicketById(1);
        ticketService.removeTicketById(2);
        Assert.assertTrue( ticketService.getTickets().isEmpty() );
    }

    @Test
    public void getTicketsShouldReturnListOrderByCreatedDate(){
        TicketDto ticketDto = new TicketDto();
        ticketDto.setTicketTheme(TicketTheme.SUBMIT_DOCS);
        ticketService.addTicket(ticketDto);

        List<Ticket> tickets = ticketService.getTickets();
        LocalDateTime firstTicketCreationDateTime = tickets.get(0).getCreatedDate();
        LocalDateTime thirdTicketCreationDateTime = tickets.get(2).getCreatedDate();
        Assert.assertTrue( firstTicketCreationDateTime.isBefore(thirdTicketCreationDateTime) );
    }


}
