package de.se.cashregistersystem.service;

import com.google.common.annotations.VisibleForTesting;
import de.se.cashregistersystem.entity.Pledge;
import de.se.cashregistersystem.entity.Product;
import de.se.cashregistersystem.util.printer.POS;
import de.se.cashregistersystem.util.printer.POSPrinter;
import de.se.cashregistersystem.util.printer.POSReceipt;
import de.se.cashregistersystem.util.printer.POSBarcode;
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
    private static final String TITLE = "Wuehlmarkt";
    private static final String ADDRESS = "Wuehlallee 1";
    private static final String PHONE = "0176 12345678";

    public String printReceipt(List<Product> products, List<Pledge> pledges) {
        if (products == null || products.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot print receipt: Item list is empty or null"
            );
        }
        return print(new ItemListPrintStrategy(products, pledges));
    }

    public String printPledgeReceipt(Pledge pledge) {
        return print(new PledgePrintStrategy(pledge));
    }

    private String print(PrintStrategy strategy) {
        String testingMode = System.getenv("TESTING_MODE");
        if ("true".equalsIgnoreCase(testingMode)) {
            System.out.println("Mock Printing Receipt: " + strategy.toString());
            return "xxDebugxx";
        }

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

            double totalPrice = strategy.calculateTotalPrice();

            receipt.setTotal(totalPrice);

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

            receipt.setFooterLine("Thank you for your purchase @ " + TITLE + "!");

            try {
                posPrinter.print(receipt, printerService);
            } catch (Exception e) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Failed to print receipt: " + e.getMessage()
                );
            }

            return barcodeString;
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

            sb.append(checkDigit);

            return sb.toString();
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to generate EAN13 barcode: " + e.getMessage()
            );
        }
    }

    @VisibleForTesting
    private interface PrintStrategy {
        void addItemsToReceipt(POSReceipt receipt);
        double calculateTotalPrice();
    }

    @VisibleForTesting
    private static class ItemListPrintStrategy implements PrintStrategy {
        private final List<Product> products;
        private final List<Pledge> pledges;

        ItemListPrintStrategy(List<Product> products, List<Pledge> pledges) {
            this.products = products;
            this.pledges = pledges;
        }

        // calculate the total price of all items
        public double calculateTotalPrice() {
            return products.stream()
                    .mapToDouble(Product::getPrice)
                    .sum();
        }

        @Override
        public void addItemsToReceipt(POSReceipt receipt) {
            // Group products by name and calculate quantities and total prices
            Map<String, ProductGroup> groupedProducts = products.stream()
                    .collect(Collectors.groupingBy(
                            Product::getName,
                            Collectors.collectingAndThen(
                                    Collectors.toList(),
                                    list -> new ProductGroup(
                                            list.size(),
                                            list.get(0).getPrice() * list.size(),
                                            list.get(0).getPledgeValue() * list.size()
                                    )
                            )
                    ));

            // Add regular items with their prices
            groupedProducts.forEach((name, group) -> {
                // Add main item
                String productName = group.quantity > 1 ?
                        String.format("%dx %s", group.quantity, name) :
                        name;
                receipt.addItem(productName, group.totalPrice);

                // Add deposit line if there is a deposit value
                if (group.totalPledge > 0) {
                    String pledgeName = group.quantity > 1 ?
                            String.format("%dx Pfand", group.quantity) :
                            "Pfand";
                    receipt.addItem(pledgeName, group.totalPledge);
                }
            });

            double totalPledgeValue = pledges.stream()
                    .mapToDouble(Pledge::getValue)
                    .sum();

            // Add total pledge value if it is greater than 0
            if (totalPledgeValue > 0) {
                receipt.addItem("Pfand", -totalPledgeValue);
            }
        }
    }


    private static class PledgePrintStrategy implements PrintStrategy {
        private final Pledge pledge;

        PledgePrintStrategy(Pledge pledge) {
            this.pledge = pledge;
        }

        @Override
        public void addItemsToReceipt(POSReceipt receipt) {
            receipt.addItem("Pledge Value", pledge.getValue());
        }

        @Override
        public double calculateTotalPrice() {
            return 0;
        }
    }
    @VisibleForTesting
    private static class ProductGroup {
        final int quantity;
        final double totalPrice;
        final double totalPledge;

        ProductGroup(int quantity, double totalPrice, double totalPledge) {
            this.quantity = quantity;
            this.totalPrice = totalPrice;
            this.totalPledge = totalPledge;
        }
    }
}