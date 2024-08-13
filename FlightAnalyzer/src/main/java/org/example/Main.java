package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            Tickets tickets = mapper.readValue(new File("tickets.json"), Tickets.class);

            List<Flight> ticketList = tickets.getTickets();
            List<Double> prices = new ArrayList<>();

            Map<String, LocalTime> minTimeFlightCorp = new HashMap<>();

            for (Flight ticket : ticketList) {
                if (ticket.getOriginName().equals("Владивосток") && ticket.getDestinationName().equals("Тель-Авив")) {
                    prices.add((double) ticket.getPrice());
                    if (ticket.getDepartureDate().equals(ticket.getArrivalDate())) {
                        if ((minTimeFlightCorp.get(ticket.getCarrier()) == null) ||
                                compareLocalTimes(
                                        getFlightDuration(
                                                ticket.getDepartureTime(), ticket.getArrivalTime()), minTimeFlightCorp.get(ticket.getCarrier()))) {
                            minTimeFlightCorp.put(ticket.getCarrier(), getFlightDuration(ticket.getDepartureTime(), ticket.getArrivalTime()));
                        }
                    }
                }
            }

            for (Map.Entry<String, LocalTime> entry : minTimeFlightCorp.entrySet()) {
                System.out.println("Транспортная компания: " + entry.getKey() + ", Минимальное время перелета: " + entry.getValue());
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

    public static boolean compareLocalTimes(LocalTime time1, LocalTime time2) {
        if (time1.compareTo(time2) <= 0) {
            return true;
        } else {
            return false;
        }
    }

    public static LocalTime  getFlightDuration(LocalTime departureTime, LocalTime arrivalTime) {
        Duration duration = Duration.between(departureTime, arrivalTime);
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        return LocalTime.of((int) hours, (int) minutes);
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