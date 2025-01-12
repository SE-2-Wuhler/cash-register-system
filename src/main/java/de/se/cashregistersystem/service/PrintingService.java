package de.se.cashregistersystem.service;

import de.se.cashregistersystem.entity.Item;
import de.se.cashregistersystem.util.POS;
import de.se.cashregistersystem.util.POSPrinter;
import de.se.cashregistersystem.util.POSReceipt;
import de.se.cashregistersystem.util.POSBarcode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class PrintingService {

    private static final String PRINTER_NAME = "Printer";
    private static final String TITLE = "Wühlmarkt";
    private static final String ADDRESS = "Wühlallee 1";
    private static final String PHONE = "0176 12345678";

    public String printReceipt(List<Item> items) {
        if (items == null || items.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot print receipt: Item list is empty or null"
            );
        }
        return print(new ItemListPrintStrategy(items));
    }

    public String printValueReceipt(double value) {
        if (value <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot print receipt: Value must be greater than 0"
            );
        }
        return print(new ValuePrintStrategy(value));
    }

    private String print(PrintStrategy strategy) {
        String testingMode = System.getenv("TESTING_MODE");
        if ("true".equalsIgnoreCase(testingMode)) {
            System.out.println("Mock Printing Receipt: " + strategy.toString());
            return "xxDebugxx";
        }

        try {
            PrintService printerService = findPrintService(PRINTER_NAME);
            if (printerService == null) {
                throw new ResponseStatusException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "Printer '" + PRINTER_NAME + "' not found or not available"
                );
            }

            POSPrinter posPrinter = new POSPrinter();
            POSReceipt receipt = new POSReceipt();
            receipt.setTitle(TITLE);
            receipt.setAddress(ADDRESS);
            receipt.setPhone(PHONE);

            strategy.addItemsToReceipt(receipt);

            String barcodeString = generateRandomEAN13();
            try {
                POSBarcode barcode = new POSBarcode(Long.parseLong(barcodeString), POS.BarcodeType.JAN13_EAN13);
                barcode.setHeight(162);
                barcode.setWidth(POS.BarWidth.DEFAULT);
                receipt.addBarcode(barcode);
            } catch (NumberFormatException e) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Failed to generate valid barcode"
                );
            }

            receipt.setFooterLine("Thank you for your purchase!");

            try {
                posPrinter.print(receipt, printerService);
            } catch (Exception e) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Failed to print receipt: " + e.getMessage()
                );
            }

            return barcodeString;

        } catch (ResponseStatusException e) {
            throw e; // Rethrow ResponseStatusExceptions as they are
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unexpected error during printing: " + e.getMessage()
            );
        }
    }

    private PrintService findPrintService(String printerName) {
        try {
            PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
            for (PrintService service : services) {
                if (service.getName().equalsIgnoreCase(printerName)) {
                    return service;
                }
            }
            return null;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Failed to lookup print services: " + e.getMessage()
            );
        }
    }

    private String generateRandomEAN13() {
        try {
            Random random = new Random();
            StringBuilder sb = new StringBuilder(13);

            // Generate first 12 digits randomly
            for (int i = 0; i < 12; i++) {
                sb.append(random.nextInt(10));
            }

            // Calculate check digit
            int sum = 0;
            for (int i = 0; i < 12; i++) {
                int digit = Character.getNumericValue(sb.charAt(i));
                sum += (i % 2 == 0) ? digit : digit * 3;
            }
            int checkDigit = (10 - (sum % 10)) % 10;

            // Append check digit
            sb.append(checkDigit);

            return sb.toString();
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to generate EAN13 barcode: " + e.getMessage()
            );
        }
    }

    // Strategy Interface
    private interface PrintStrategy {
        void addItemsToReceipt(POSReceipt receipt);
    }

    // Concrete strategy for Item list
    private static class ItemListPrintStrategy implements PrintStrategy {
        private final List<Item> items;

        ItemListPrintStrategy(List<Item> items) {
            this.items = items;
        }

        @Override
        public void addItemsToReceipt(POSReceipt receipt) {
            Map<String, ItemGroup> groupedItems = items.stream()
                    .collect(Collectors.groupingBy(
                            Item::getName,
                            Collectors.collectingAndThen(
                                    Collectors.toList(),
                                    list -> new ItemGroup(
                                            list.size(),
                                            list.get(0).getPrice() * list.size()
                                    )
                            )
                    ));

            groupedItems.forEach((name, group) -> {
                String itemName = group.quantity > 1 ?
                        String.format("%dx %s", group.quantity, name) :
                        name;
                receipt.addItem(itemName, group.totalPrice);
            });
        }
    }

    // Concrete strategy for double value
    private static class ValuePrintStrategy implements PrintStrategy {
        private final double value;

        ValuePrintStrategy(double value) {
            this.value = value;
        }

        @Override
        public void addItemsToReceipt(POSReceipt receipt) {
            receipt.addItem("Value", value);
        }
    }

    private static class ItemGroup {
        final int quantity;
        final double totalPrice;

        ItemGroup(int quantity, double totalPrice) {
            this.quantity = quantity;
            this.totalPrice = totalPrice;
        }
    }
}