package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            Tickets tickets = mapper.readValue(new File("tickets.json"), Tickets.class);

            List<Flight> ticketList = tickets.getTickets();
            LocalTime minTime = ticketList.get(0).getDepartureTime();
            List<Double> prices = new ArrayList<>();

            for (Flight ticket : ticketList) {
                prices.add((double) ticket.getPrice());
                if (ticket.getDepartureDate().equals(ticket.getArrivalDate())) {
                    if (getFlightDuration(ticket.getDepartureTime(), ticket.getArrivalTime()).isBefore(minTime)) {
                        minTime = getFlightDuration(ticket.getDepartureTime(), ticket.getArrivalTime());
                    }
                }
            }

            double averagePrice = prices.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

            Collections.sort(prices);
            double medianPrice;
            int size = prices.size();
            if (size % 2 == 0) {
                medianPrice = (prices.get(size / 2 - 1) + prices.get(size / 2)) / 2;
            } else {
                medianPrice = prices.get(size / 2);
            }

            double difference = averagePrice - medianPrice;

            System.out.println("Минимальное время полета: " + minTime);
            System.out.println("Разница между средней ценой и медианой: " + difference);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static LocalTime  getFlightDuration(LocalTime departureTime, LocalTime arrivalTime) {
        Duration duration = Duration.between(departureTime, arrivalTime);
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        return LocalTime.of((int) hours, (int) minutes);
    }
}