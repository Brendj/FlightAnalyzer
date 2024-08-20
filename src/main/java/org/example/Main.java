package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            Tickets tickets = mapper.readValue(new File("tickets.json"), Tickets.class);

            List<Flight> ticketList = tickets.getTickets();
            List<Double> prices = new ArrayList<>();

            Map<String, LocalDateTime> minTimeFlightCorp = new HashMap<>();

            for (Flight ticket : ticketList) {
                if (ticket.getOriginName().equals("Владивосток") && ticket.getDestinationName().equals("Тель-Авив")) {
                    prices.add((double) ticket.getPrice());

                    if ((minTimeFlightCorp.get(ticket.getCarrier()) == null) ||
                            differenceLocalDateTimes(combineDateAndTime(ticket.getDepartureDate(), ticket.getDepartureTime()),
                                                        combineDateAndTime(ticket.getArrivalDate(), ticket.getArrivalTime()))
                                    .isBefore(minTimeFlightCorp.get(ticket.getCarrier()))) {
                            minTimeFlightCorp.put(ticket.getCarrier(), differenceLocalDateTimes(combineDateAndTime(ticket.getDepartureDate(), ticket.getDepartureTime()),
                                                                                                combineDateAndTime(ticket.getArrivalDate(), ticket.getArrivalTime())));
                        }
                }
            }

            for (Map.Entry<String, LocalDateTime> entry : minTimeFlightCorp.entrySet()) {
                if (entry.getValue().getYear() == 0000) {
                    System.out.println("Транспортная компания: " + entry.getKey() +
                            ", Минимальное время перелета: " + LocalTime.of(entry.getValue().getHour(), entry.getValue().getMinute()));
                } else {
                    System.out.println("Транспортная компания: " + entry.getKey() +
                            ", Минимальное время перелета: " + entry.getValue().getDayOfMonth() + " день " + entry.getValue().getHour() + " часов " + entry.getValue().getMinute() + " минут");
                }
            }

            double average = calculateAverage(prices);
            System.out.println("Средняя цена: " + average);

            double median = calculateMedian(prices);
            System.out.println("Медиана: " + median);

            double difference = average - median;
            System.out.println("Разница между средней ценой и медианой: " + difference);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static LocalDateTime differenceLocalDateTimes(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        Duration duration = Duration.between(dateTime1, dateTime2);

        LocalDate zeroDate = LocalDate.of(0, 1, 1);
        LocalDateTime zeroDateTime = LocalDateTime.of(zeroDate, LocalTime.MIDNIGHT);

        zeroDateTime = zeroDateTime.plus(duration);

        return zeroDateTime;
    }

    public static LocalDate parseDate(String dateString) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yy");
        return LocalDate.parse(dateString, dateFormatter);
    }

    public static LocalDateTime combineDateAndTime(String dateString, LocalTime time) {
        LocalDate date = parseDate(dateString);
        return LocalDateTime.of(date, time);
    }

    public static double calculateAverage(List<Double> prices) {
        double sum = 0.0;
        for (double price : prices) {
            sum += price;
        }
        return sum / prices.size();
    }

    public static double calculateMedian(List<Double> prices) {
        Collections.sort(prices);

        int length = prices.size();

        if (length % 2 == 0) {
            return (prices.get(length / 2 - 1) + prices.get(length / 2)) / 2.0;
        } else {
            return prices.get(length / 2);
        }
    }
}