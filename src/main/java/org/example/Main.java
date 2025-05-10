package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.example.Model.Orders;
import org.example.Model.PaymentMethods;


import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    @Setter
    static ArrayList<Orders> ordersList = new ArrayList<>();
    @Setter
    static Map<String,PaymentMethods> paymentMethodsMap = new HashMap<>();

    public static void main(String[] args) {

       loadFiles(args);
//
//        for (Orders order : ordersList) {
//            System.out.println(order.toString());
//        }
//
//        paymentMethodsMap.forEach((key, value) -> {
//            System.out.println(key+":"+value);
//        });

        calculateDiscount();
    }

    public static void loadFiles(String[] args){
        String orderPath = args.length == 0 ? "F:\\Dokumenty (F)\\job\\Ocado Technology\\Dawid_Skubij_Java_Krakow\\src\\main\\resources\\orders.json" : args[0];
        String paymentmethodPath = args.length == 0 ? "F:\\Dokumenty (F)\\job\\Ocado Technology\\Dawid_Skubij_Java_Krakow\\src\\main\\resources\\paymentmethods.json" : args[1];

        try {
            ObjectMapper mapper = new ObjectMapper();

            ordersList = mapper.readValue(
                    Paths.get(orderPath).toFile(),
                    new TypeReference<ArrayList<Orders>>() {
                    }
            );

            ArrayList<PaymentMethods> paymentMethodsList = mapper.readValue(
                    Paths.get(paymentmethodPath).toFile(),
                    new TypeReference<ArrayList<PaymentMethods>>() {
                    }
            );

            paymentMethodsMap = paymentMethodsList.stream()
                    .collect(Collectors.toMap(PaymentMethods::getId, method -> method));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String,Double> calculateDiscount() {

        Map<String, Double> paymentInfo = new HashMap<>();
        for (String key : paymentMethodsMap.keySet()) {
            paymentInfo.put(key,0.0);
        }

        for (Orders order : ordersList) {


            // Sprawdzenie czy da się zapłacić za całe zamówienie punktami - najbardziej opłacalna metoda płatności zasada 4
            Double priceFullPunkty = (order.getValue() - (order.getValue() * paymentMethodsMap.get("PUNKTY").getDiscount() / 100));
            if (priceFullPunkty < paymentMethodsMap.get("PUNKTY").getLimit()) {
                paymentMethodsMap.get("PUNKTY").setLimit(paymentMethodsMap.get("PUNKTY").getLimit() - priceFullPunkty);
                paymentInfo.put("PUNKTY", priceFullPunkty);
            } else {
                // Sprawdzenie czy wystaczy punktów na 10% wartości zamówienia - druga najbardziej opłacalna metoda zasada 3
                Double price10Percent = (order.getValue() * 10 / 100);
                if (price10Percent < paymentMethodsMap.get("PUNKTY").getLimit()) {
                    Double priceLeftToPay = order.getValue() - paymentMethodsMap.get("PUNKTY").getLimit();
                    for (PaymentMethods paymentMethod : paymentMethodsMap.values()) {
                        if (paymentMethod.getLimit() > priceLeftToPay) {
                            paymentInfo.put(paymentMethod.getId(),paymentInfo.get(paymentMethod.getId())+ priceLeftToPay - (0.10 * order.getValue()));
                            paymentInfo.put("PUNKTY", paymentInfo.get("PUNKTY")+ paymentMethodsMap.get("PUNKTY").getLimit());
                            paymentMethod.setLimit(paymentMethod.getLimit() - priceLeftToPay);
                            paymentMethodsMap.get("PUNKTY").setLimit(0.0);
                        }
                    }
                } else {
                    // Sprawdzenie która z metod płatności jest bardziej opłacalna zasada 2
                    try {
                        Double minPrice = order.getValue();
                        String minPriceDiscountName = "NONE";
                        for (String promotions : order.getPromotions()) {
                            Double priceWhenDiscount = (order.getValue() - (order.getValue() * paymentMethodsMap.get(promotions).getDiscount() / 100));
                            if (priceWhenDiscount < minPrice && priceWhenDiscount < paymentMethodsMap.get(promotions).getLimit()) {
                                minPriceDiscountName = promotions;
                                minPrice = priceWhenDiscount;
                            }
                        }
                        paymentMethodsMap.get(minPriceDiscountName).setLimit(paymentMethodsMap.get(minPriceDiscountName).getLimit() - minPrice);
                        paymentInfo.put(minPriceDiscountName, minPrice);
                    } catch (Exception e) {
                        System.err.println("Error at " + order.toString() + ": " + e.getMessage());
                    }
                }
            }
        }

        paymentInfo.forEach((key, value) -> {
            System.out.println(key+":"+value);
        });

        return paymentInfo;
    }
}