package com.example.tickets.service;


import com.example.tickets.domain.dto.TicketDto;
import com.example.tickets.domain.enums.TicketState;
import com.example.tickets.domain.model.Ticket;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private Map<Integer,Ticket> tickets;
    private List<Integer> busyWindows;
    private List<Ticket> allTickets;

    {
        allTickets = Collections.synchronizedList(new LinkedList<Ticket>());
        tickets = Collections.synchronizedMap(new HashMap<Integer, Ticket>());
        busyWindows = Collections.synchronizedList(new ArrayList<>());
    }

    public void addTicket(TicketDto ticketDto) {

        synchronized (TicketService.class) {

            Optional<Ticket> optionalTicket = allTickets.stream().max(Comparator.comparing(Ticket::getId));

            if (optionalTicket.isPresent()) {

                Integer id = optionalTicket.get().getId() + 1;
                ticketDto.setId(id);
                Ticket ticket = createNewTicketFromTicketDto(ticketDto);
                tickets.put(id, ticket);
                allTickets.add(ticket);

            } else {

                ticketDto.setId(1);
                Ticket ticket = createNewTicketFromTicketDto(ticketDto);
                allTickets.add(ticket);
                tickets.put(ticket.getId(), ticket);

            }
        }
    }

    public void updateTicket(TicketDto ticketDto) {

        synchronized (TicketService.class){

            if (tickets.containsKey(ticketDto.getId())){

                List<TicketState> allowedStates = getAllowedStates(tickets.get(ticketDto.getId()).getTicketState());
                if (allowedStates.contains(ticketDto.getTicketState())){

                    if(isTicketDtoValidForStateChanging(tickets.get(ticketDto.getId()), ticketDto)){

                        Ticket ticket = createUpdatedTicketFromTicketAndTicketDto(tickets.get(ticketDto.getId()), ticketDto);
                        if (Arrays.asList(TicketState.CALL, TicketState.SERVED).contains(ticketDto.getTicketState()) ){
                            busyWindows.add(ticket.getWindowNumber());
                        } else if (Arrays.asList(TicketState.HOLD, TicketState.COMPLETE).contains(ticketDto.getTicketState())){
                            busyWindows.remove(ticket.getWindowNumber());
                        }
                        allTickets.remove(tickets.get(ticketDto.getId()));
                        tickets.remove(ticketDto.getId());
                        allTickets.add(ticket);
                        tickets.put(ticket.getId(),ticket);

                    } else {
                        throw new HttpClientErrorException(HttpStatus.CONFLICT);
                    }

                } else {
                    throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
                }

            } else {
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
            }
        }
    }

    public List<Ticket> getTickets() {

        synchronized (TicketService.class){
            return allTickets.stream().sorted(Comparator.comparing(Ticket::getCratedDate)).collect(Collectors.toList());
        }

    }

    public Ticket getTicketById(Integer id) {

        synchronized (TicketService.class){

            if (tickets.containsKey(id)){
                return tickets.get(id);
            } else {
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
            }

        }
    }

    public void removeTicketById(Integer id){

        synchronized (TicketService.class){
            if (tickets.containsKey(id)){

                Ticket ticket = tickets.get(id);
                busyWindows.remove(ticket.getWindowNumber());
                tickets.remove(id);
                allTickets.remove(ticket);

            } else {
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
            }
        }

    }


    private Ticket createNewTicketFromTicketDto(TicketDto ticketDto){
        Ticket ticket = new Ticket(ticketDto.getId(), LocalDateTime.now());
        ticket.setModifiedDate(LocalDateTime.now());
        ticket.setTicketState(TicketState.CREATED);
        ticket.setTicketTheme(ticketDto.getTicketTheme());
        return ticket;
    }

    private Ticket createUpdatedTicketFromTicketAndTicketDto(Ticket ticket, TicketDto ticketDto){

        synchronized (TicketService.class){

            Ticket updatedTicket = new Ticket(ticketDto.getId(), ticket.getCratedDate());
            if (Arrays.asList(TicketState.CALL, TicketState.SERVED).contains(ticketDto.getTicketState()) ){
                updatedTicket.setWindowNumber(ticketDto.getWindowNum());
            }
            updatedTicket.setTicketTheme(ticketDto.getTicketTheme());
            updatedTicket.setTicketState(ticketDto.getTicketState());
            updatedTicket.setModifiedDate(LocalDateTime.now());
            return updatedTicket;
        }
    }

    private List<TicketState> getAllowedStates(TicketState ticketState){
        List<TicketState> allowedStates = new ArrayList<>();
        switch (ticketState){
            case CREATED :
            case HOLD:
                allowedStates.addAll(Collections.singletonList(TicketState.CALL));
            break;
            case CALL: allowedStates.addAll(Collections.singletonList(TicketState.SERVED));
            break;
            case SERVED: allowedStates.addAll(Collections.singletonList(TicketState.HOLD));
            break;
            default:
        }
        return allowedStates;
    }

    private Boolean isTicketDtoValidForStateChanging(Ticket ticket, TicketDto ticketDto){
        if ( Arrays.asList(TicketState.CALL,TicketState.SERVED).contains(ticketDto.getTicketState()) && ticket.getWindowNumber() != null && !busyWindows.contains(ticketDto.getWindowNum())){
            return true;
        } else {
            return false;
        }
    }


}