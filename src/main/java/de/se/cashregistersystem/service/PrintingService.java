package de.se.cashregistersystem.service;

import de.se.cashregistersystem.dto.ItemDTO;
import de.se.cashregistersystem.util.POS;
import de.se.cashregistersystem.util.POSPrinter;
import de.se.cashregistersystem.util.POSReceipt;
import de.se.cashregistersystem.util.POSBarcode;
import org.springframework.stereotype.Service;

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

    public void printReceipt(List<ItemDTO> items) {
        print(new ItemListPrintStrategy(items));
    }

    public String printValueReceipt(double value) {
        return print(new ValuePrintStrategy(value));

    }

    private String print(PrintStrategy strategy) {
        String testingMode = System.getenv("TESTING_MODE");
        if ("true".equalsIgnoreCase(testingMode)) {
            System.out.println("Mock Printing Receipt: " + strategy.toString());
            return "xxDebugxx";
        }
        PrintService printerService = findPrintService(PRINTER_NAME);
        if (printerService == null) {
            throw new RuntimeException("Printer '" + PRINTER_NAME + "' not found");
        }

        POSPrinter posPrinter = new POSPrinter();
        POSReceipt receipt = new POSReceipt();
        receipt.setTitle(TITLE);
        receipt.setAddress(ADDRESS);
        receipt.setPhone(PHONE);

        strategy.addItemsToReceipt(receipt);

        String barcodeString = generateRandomEAN13();
        POSBarcode barcode = new POSBarcode(Long.parseLong(barcodeString), POS.BarcodeType.JAN13_EAN13);
        barcode.setHeight(162);
        barcode.setWidth(POS.BarWidth.DEFAULT);
        receipt.addBarcode(barcode);

        receipt.setFooterLine("Thank you for your purchase!");

        posPrinter.print(receipt, printerService);
        return barcodeString;

    }

    private PrintService findPrintService(String printerName) {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService service : services) {
            if (service.getName().equalsIgnoreCase(printerName)) {
                return service;
            }
        }
        return null;
    }

    private String generateRandomEAN13() {
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
    }

    // Strategy Interface
    private interface PrintStrategy {
        void addItemsToReceipt(POSReceipt receipt);
    }

    // Concrete strategy for ItemDTO list
    private static class ItemListPrintStrategy implements PrintStrategy {
        private final List<ItemDTO> items;

        ItemListPrintStrategy(List<ItemDTO> items) {
            this.items = items;
        }

        @Override
        public void addItemsToReceipt(POSReceipt receipt) {
            Map<String, ItemGroup> groupedItems = items.stream()
                    .collect(Collectors.groupingBy(
                            ItemDTO::getName,
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