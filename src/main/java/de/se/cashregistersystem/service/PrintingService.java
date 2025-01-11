package de.se.cashregistersystem.service;

import de.se.cashregistersystem.dto.ItemDTO;
import de.se.cashregistersystem.dto.PledgeDTO;
import de.se.cashregistersystem.util.POS;
import de.se.cashregistersystem.util.POSPrinter;
import de.se.cashregistersystem.util.POSReceipt;
import de.se.cashregistersystem.util.POSBarcode;
import org.springframework.stereotype.Service;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.util.List;
import java.util.Map;
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

    public void printPledgeReceipt(PledgeDTO PledgeDTO) {
        print(new PledgePrintStrategy(PledgeDTO));
    }

    private void print(PrintStrategy strategy) {
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

        POSBarcode barcode = new POSBarcode(4012345678901L, POS.BarcodeType.JAN13_EAN13);
        barcode.setHeight(162);
        barcode.setWidth(POS.BarWidth.DEFAULT);
        receipt.addBarcode(barcode);

        receipt.setFooterLine("Thank you for your purchase!");

        posPrinter.print(receipt, printerService);
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

    // Concrete strategy for PledgeDTO
    private static class PledgePrintStrategy implements PrintStrategy {
        private final PledgeDTO PledgeDTO;

        PledgePrintStrategy(PledgeDTO PledgeDTO) {
            this.PledgeDTO = PledgeDTO;
        }

        @Override
        public void addItemsToReceipt(POSReceipt receipt) {
            receipt.addItem("PledgeDTO: " + PledgeDTO.getBarcodeId(), PledgeDTO.getValue());
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