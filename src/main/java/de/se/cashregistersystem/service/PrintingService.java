package de.se.cashregistersystem.service;

import de.se.cashregistersystem.util.POS;
import de.se.cashregistersystem.util.POSPrinter;
import de.se.cashregistersystem.util.POSReceipt;
import de.se.cashregistersystem.util.POSBarcode;
import org.springframework.stereotype.Service;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

@Service
public class PrintingService {

    private static final String PRINTER_NAME = "Printer"; // Name des Druckers

    /**
     * Druckt den Beleg (Receipt) mithilfe des konfigurierten POS-Systems.
     */
    public void printReceipt() {
        // Sucht den Drucker anhand des Namens
        PrintService printerService = findPrintService(PRINTER_NAME);

        if (printerService == null) {
            // Falls der Drucker nicht gefunden wird, kann hier z. B. eine Exception geworfen oder geloggt werden
            throw new RuntimeException("Printer '" + PRINTER_NAME + "' not found");
        }

        // Erzeugt eine neue Instanz des POS-Druckers
        POSPrinter posPrinter = new POSPrinter();

        // Erzeugt einen neuen Beleg (Receipt)
        POSReceipt receipt = new POSReceipt();
        receipt.setTitle("Cat Shop 24");
        receipt.setAddress("Europaplatz 17\n69115 Heidelberg");
        receipt.setPhone("01749885992");

        // Fügt einige Artikel zum Beleg hinzu
        receipt.addItem("Snackies", 1.99);
        receipt.addItem("CatMilk", 2.99);

        // Erstellt einen Barcode und fügt ihn dem Beleg hinzu
        POSBarcode barcode = new POSBarcode(4012345678901L, POS.BarcodeType.JAN13_EAN13);
        barcode.setHeight(162);
        barcode.setWidth(POS.BarWidth.DEFAULT);
        receipt.addBarcode(barcode);

        // Setzt eine Fußzeile
        receipt.setFooterLine("Thank you for shopping!");

        // Startet den Druckvorgang
        posPrinter.print(receipt, printerService);
    }

    /**
     * Hilfsmethode, um einen Drucker anhand seines Namens zu finden.
     *
     * @param printerName Der Name des zu suchenden Druckers.
     * @return Gefundener PrintService oder null, falls nicht gefunden.
     */
    private PrintService findPrintService(String printerName) {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService service : services) {
            if (service.getName().equalsIgnoreCase(printerName)) {
                return service;
            }
        }
        return null;
    }
}
