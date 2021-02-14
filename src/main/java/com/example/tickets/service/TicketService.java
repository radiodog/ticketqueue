package com.example.tickets.service;


import com.example.tickets.domain.dto.TicketDto;
import com.example.tickets.domain.enums.TicketState;
import com.example.tickets.domain.model.Ticket;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private Map<Integer, Ticket> tickets;
    private List<Integer> busyWindows;
    private List<Ticket> allTickets;

    {
        allTickets = Collections.synchronizedList(new LinkedList<Ticket>());
        tickets = Collections.synchronizedMap(new HashMap<Integer, Ticket>());
        busyWindows = Collections.synchronizedList(new ArrayList<>());
    }

    public void addTicket(TicketDto ticketDto) {

        if (ticketDto == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Тело запроса не может быть пустым!");
        }

        synchronized (TicketService.class) {

            Optional<Ticket> optionalTicket = allTickets.stream().max(Comparator.comparing(Ticket::getId));
            Integer id = optionalTicket.map(ticket -> ticket.getId() + 1).orElse(1);
            ticketDto.setId(id);
            Ticket ticket = createNewTicketFromTicketDto(ticketDto);
            addToStorage(ticket);
        }
    }

    public void updateTicket(TicketDto ticketDto) {

        if (ticketDto == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Тело запроса не может быть пустым!");
        }

        synchronized (TicketService.class) {

            if (tickets.containsKey(ticketDto.getId())) {

                List<TicketState> allowedStates = getAllowedStates(tickets.get(ticketDto.getId()).getTicketState());
                if (allowedStates.contains(ticketDto.getTicketState())) {

                    if (isTicketDtoValidForStateChanging(tickets.get(ticketDto.getId()), ticketDto)) {
                        if (isWindowFree(ticketDto)){

                            Ticket ticket = createUpdatedTicketFromTicketAndTicketDto(tickets.get(ticketDto.getId()), ticketDto);
                            if (Arrays.asList(TicketState.CALL, TicketState.SERVED).contains(ticketDto.getTicketState())) {
                                busyWindows.add(ticket.getWindowNumber());
                            } else if (Arrays.asList(TicketState.HOLD, TicketState.COMPLETE).contains(ticketDto.getTicketState())) {
                                busyWindows.remove(ticket.getWindowNumber());
                            }
                            allTickets.remove(tickets.get(ticketDto.getId()));
                            tickets.remove(ticketDto.getId());
                            addToStorage(ticket);

                        } else {
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Окно номер " + ticketDto.getWindowNumber() + " занято!");
                        }


                    } else {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Номер окна не заполнен!");
                    }

                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Вы не можете перевести талон из статуса " +
                            tickets.get(ticketDto.getId()).getTicketState().getName() +
                            " в статус " + ticketDto.getTicketState().getName() + "!");
                }

            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Талон с идентификатором " + ticketDto.getId() + " не найден!");
            }
        }
    }

    public List<Ticket> getTickets() {

        synchronized (TicketService.class) {
            return allTickets.stream().sorted(Comparator.comparing(Ticket::getCreatedDate)).collect(Collectors.toList());
        }

    }

    public Ticket getTicketById(Integer id) {

        if (id == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Идентификатор не может быть пустым!");
        }

        synchronized (TicketService.class) {

            if (tickets.containsKey(id)) {
                return tickets.get(id);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Талон с идентификатором " + id + " не найден!");
            }

        }
    }

    public void removeTicketById(Integer id) {

        if (id == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Идентификатор не может быть пустым!");
        }

        synchronized (TicketService.class) {
            if (tickets.containsKey(id)) {

                Ticket ticket = tickets.get(id);
                busyWindows.remove(ticket.getWindowNumber());
                tickets.remove(id);
                allTickets.remove(ticket);

            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Талон с идентификатором " + id + " не найден!");
            }
        }

    }

    private Ticket createNewTicketFromTicketDto(TicketDto ticketDto) {
        Ticket ticket = new Ticket(ticketDto.getId(), LocalDateTime.now());
        ticket.setTicketState(TicketState.CREATED);
        ticket.setTicketTheme(ticketDto.getTicketTheme());
        return ticket;
    }

    private Ticket createUpdatedTicketFromTicketAndTicketDto(Ticket ticket, TicketDto ticketDto) {

        Ticket updatedTicket = new Ticket(ticketDto.getId(), ticket.getCreatedDate());
        if (Arrays.asList(TicketState.CALL, TicketState.SERVED).contains(ticketDto.getTicketState())) {
            updatedTicket.setWindowNumber(ticketDto.getWindowNumber());
        }
        updatedTicket.setTicketTheme(ticketDto.getTicketTheme());
        updatedTicket.setTicketState(ticketDto.getTicketState());
        updatedTicket.setModifiedDate(LocalDateTime.now());
        return updatedTicket;
    }

    private List<TicketState> getAllowedStates(TicketState ticketState) {
        List<TicketState> allowedStates = new ArrayList<>();
        switch (ticketState) {
            case CREATED:
            case HOLD:
                allowedStates.addAll(Collections.singletonList(TicketState.CALL));
                break;
            case CALL:
                allowedStates.addAll(Collections.singletonList(TicketState.SERVED));
                break;
            case SERVED:
                allowedStates.addAll(Collections.singletonList(TicketState.HOLD));
                break;
            default:
        }
        return allowedStates;
    }

    private Boolean isTicketDtoValidForStateChanging(Ticket ticket, TicketDto ticketDto) {

        if (Arrays.asList(TicketState.CALL, TicketState.SERVED).contains(ticketDto.getTicketState()) && ticketDto.getWindowNumber() != null ) {
            return true;
        } else {
            return false;
        }
    }

    private Boolean isWindowFree(TicketDto ticketDto){
        return !busyWindows.contains(ticketDto.getWindowNumber());
    }

    private void addToStorage(Ticket ticket) {
        tickets.put(ticket.getId(), ticket);
        allTickets.add(ticket);
    }

}