package org.example;

import org.apache.commons.io.input.BOMInputStream;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {
    private JSONArray ticketsArray;

    @BeforeEach
    void setUp() {
        ticketsArray = new JSONArray();

        JSONObject ticket1 = new JSONObject();
        ticket1.put("origin_name", "Владивосток");
        ticket1.put("destination_name", "Тель-Авив");
        ticket1.put("price", 1000);
        ticket1.put("departure_time", "10:00");
        ticket1.put("arrival_time", "14:00");

        JSONObject ticket2 = new JSONObject();
        ticket2.put("origin_name", "Владивосток");
        ticket2.put("destination_name", "Тель-Авив");
        ticket2.put("price", 2000);
        ticket2.put("departure_time", "11:00");
        ticket2.put("arrival_time", "15:00");

        ticketsArray.add(ticket1);
        ticketsArray.add(ticket2);
    }

    @Test
    void testAnalyzeTicketData() {
        // Перехватываем вывод в консоль для проверки
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Вызываем метод analyzeTicketData с тестовыми данными
        App.analyzeTicketData(ticketsArray);

        // Проверяем, что вывод соответствует ожиданиям
        String expectedOutput = "Минимальное время полета: 4.00 часов\n" +
                "Средняя цена билета: 1500.00 рублей\n" +
                "Медианная цена билета: 1500.00 рублей\n" +
                "Разница между средней и медианной ценами: 0.00 рублей\n";
        assertEquals(expectedOutput, outContent.toString());
    }

    @Test
    void testCalculateMedianPrice() {
        double medianPrice = App.calculateMedianPrice(ticketsArray);
        assertEquals(1500, medianPrice, 0.01);
    }

    @Test
    void testCalculateFlightDuration() {
        double flightDuration = App.calculateFlightDuration("10:00", "14:00");
        assertEquals(4.0, flightDuration, 0.01);
    }
}
