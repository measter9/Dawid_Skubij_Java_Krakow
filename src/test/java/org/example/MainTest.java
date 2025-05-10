package org.example;

import org.example.Model.Orders;
import org.example.Model.PaymentMethods;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {


    @Test
    void calculateDiscount() {
        Main main = new Main();
        ArrayList<Orders> orders = new ArrayList<>();
        orders.add(new Orders("ORDER1", 100.00, List.of("mZysk").toArray(new String[0])));
        orders.add(new Orders("ORDER2", 200.00, List.of("BosBankrut").toArray(new String[0])));
        orders.add(new Orders("ORDER3", 150.00, List.of("mZysk", "BosBankrut").toArray(new String[0])));
        orders.add(new Orders("ORDER4", 50.00, null));
        main.setOrdersList(orders);

        Map<String, PaymentMethods> paymentMethods = new HashMap<>();
        paymentMethods.put("PUNKTY", new PaymentMethods("PUNKTY", 15, 100.00));
        paymentMethods.put("mZysk", new PaymentMethods("mZysk", 10, 180.00));
        paymentMethods.put("BosBankrut", new PaymentMethods("BosBankrut", 5, 200.00));
        main.setPaymentMethodsMap(paymentMethods);

        Map<String,Double>result = main.calculateDiscount();

        Map<String, Double> expected = new HashMap<>();
        expected.put("mZysk", 165.0);
        expected.put("BosBankrut", 190.0);
        expected.put("PUNKTY", 100.0);
        assertEquals(expected, result);

    }
}