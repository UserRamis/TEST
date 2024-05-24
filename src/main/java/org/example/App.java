package org.example;

import org.apache.commons.io.input.BOMInputStream;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class App {
    public static void main(String[] args) {
        // Определение пути к файлу tickets.json
        String filePath = "src/main/tickets.json";

        try (FileInputStream fileInputStream = new FileInputStream(filePath);
             BOMInputStream bomInputStream = new BOMInputStream(fileInputStream);
             InputStreamReader reader = new InputStreamReader(bomInputStream, StandardCharsets.UTF_8)) {

            // Парсинг JSON
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(reader);

            // Извлечение массива билетов из JSON-объекта
            JSONArray ticketsArray = (JSONArray) jsonObject.get("tickets");

            // Анализ данных билетов
            analyzeTicketData(ticketsArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void analyzeTicketData(JSONArray ticketsArray) {
        // Инициализация переменных для хранения результатов анализа
        double minFlightDuration = Double.MAX_VALUE;
        double totalTicketPrice = 0;
        int ticketCount = 0;

        // Инициализация карты для хранения минимального времени полета для каждого перевозчика
        Map<String, Double> minFlightDurationPerCarrier = new HashMap<>();

        for (Object obj : ticketsArray) {
            JSONObject ticket = (JSONObject) obj;

            String originCity = (String) ticket.get("origin_name");
            String destinationCity = (String) ticket.get("destination_name");
            double price = ((Number) ticket.get("price")).doubleValue();
            double flightDuration = calculateFlightDuration((String) ticket.get("departure_time"), (String) ticket.get("arrival_time"));
            String carrier = (String) ticket.get("carrier");

            // Проверка на соответствие маршрута Владивосток - Тель-Авив
            if ("Владивосток".equals(originCity) && "Тель-Авив".equals(destinationCity)) {
                if (flightDuration < minFlightDuration) {
                    minFlightDuration = flightDuration;
                }
                totalTicketPrice += price;
                ticketCount++;

                // Обновление минимального времени полета для текущего перевозчика
                minFlightDurationPerCarrier.merge(carrier, flightDuration, Math::min);
            }
        }

        if (ticketCount == 0) {
            System.out.println("Не найдено билетов по маршруту Владивосток - Тель-Авив.");
            return;
        }

        double averagePrice = totalTicketPrice / ticketCount;
        System.out.printf("Минимальное время полета: %.2f часов%n", minFlightDuration);
        System.out.printf("Средняя цена билета: %.2f рублей%n", averagePrice);

        // Расчет медианной цены
        double medianPrice = calculateMedianPrice(ticketsArray);
        System.out.printf("Медианная цена билета: %.2f рублей%n", medianPrice);

        double priceDifference = averagePrice - medianPrice;
        System.out.printf("Разница между средней и медианной ценами: %.2f рублей%n", priceDifference);

        // Вывод минимального времени полета для каждого перевозчика
        System.out.println("Минимальное время полета между городами Владивосток и Тель-Авив для каждого авиаперевозчика:");
        for (Map.Entry<String, Double> entry : minFlightDurationPerCarrier.entrySet()) {
            System.out.printf("Перевозчик: %s, Минимальное время полета: %.2f часов%n", entry.getKey(), entry.getValue());
        }
    }

    static double calculateMedianPrice(JSONArray ticketsArray) {
        // Сортировка билетов по цене
        ticketsArray.sort((o1, o2) -> {
            double price1 = ((Number) ((JSONObject) o1).get("price")).doubleValue();
            double price2 = ((Number) ((JSONObject) o2).get("price")).doubleValue();
            return Double.compare(price1, price2);
        });

        int size = ticketsArray.size();
        if (size % 2 == 0) {
            double price1 = ((Number) ((JSONObject) ticketsArray.get(size / 2 - 1)).get("price")).doubleValue();
            double price2 = ((Number) ((JSONObject) ticketsArray.get(size / 2)).get("price")).doubleValue();
            return (price1 + price2) / 2;
        } else {
            return ((Number) ((JSONObject) ticketsArray.get(size / 2)).get("price")).doubleValue();
        }
    }

    static double calculateFlightDuration(String departureTime, String arrivalTime) {
        // Преобразование времени отправления и прибытия в часы для расчета продолжительности полета
        String[] depParts = departureTime.split(":");
        String[] arrParts = arrivalTime.split(":");

        double depHour = Double.parseDouble(depParts[0]);
        double depMinute = Double.parseDouble(depParts[1]);
        double arrHour = Double.parseDouble(arrParts[0]);
        double arrMinute = Double.parseDouble(arrParts[1]);

        return (arrHour + arrMinute / 60) - (depHour + depMinute / 60);
    }
}
