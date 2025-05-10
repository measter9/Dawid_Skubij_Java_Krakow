# Payment Optimization System

Projekt Java analizujący zamówienia i optymalnie przypisujący metody płatności w oparciu o dostępne limity i zniżki. System ma na celu maksymalizację opłacalności płatności z wykorzystaniem punktów lojalnościowych oraz zdefiniowanych metod płatności.

##  Zasady działania

System przypisuje metodę płatności do zamówienia według następujących zasad:

1. **Najbardziej opłacalna promocja** – wybór metody płatności z największym rabatem (jeśli możliwa).
2. **Zastosowanie punktów do 10% wartości** – jeśli nie można zapłacić całym zamówieniem punktami, ale jest dostępne pokrycie do 10%.
3. **Pełna płatność punktami** – jeżeli punkty wystarczają na całkowite pokrycie zamówienia.


