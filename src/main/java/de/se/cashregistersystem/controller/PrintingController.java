package de.se.cashregistersystem.controller;

import de.se.cashregistersystem.service.PrintingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PrintingController {

    private final PrintingService receiptPrintingService;

    public PrintingController(PrintingService receiptPrintingService) {
        this.receiptPrintingService = receiptPrintingService;
    }

    /**
     * Endpoint zum Drucken eines Belegs.
     *
     * Beispiel-Aufruf: POST /api/printReceipt
     *
     * @return HTTP-Status 200 bei Erfolg oder 500 bei einem Fehler.
     */
    @PostMapping("/printReceipt")
    public ResponseEntity<String> printReceipt() {
        try {
            receiptPrintingService.printReceipt();
            return ResponseEntity.ok("Receipt printed successfully");
        } catch (Exception e) {
            // Hier können je nach Bedarf auch detailliertere Fehlerinformationen zurückgegeben werden.
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error printing receipt: " + e.getMessage());
        }
    }
}
