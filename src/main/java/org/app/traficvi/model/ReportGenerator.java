package org.app.traficvi.model;

import com.opencsv.CSVWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ReportGenerator implements Reportable {


    private List<IntrusionAlert> alerts;

    public ReportGenerator(List<IntrusionAlert> alerts) {
        this.alerts = alerts;
    }

    @Override
    public boolean generateReport(String format) {
        // Ouvrir une boîte de dialogue pour choisir le chemin
        String outputPath = selectSaveDirectory();
        if (outputPath == null) {
            System.out.println("Opération annulée par l'utilisateur.");
            return false;
        }

        // Ajouter des recommandations aux alertes
        for (IntrusionAlert alert : alerts) {
            String recommendation = generateRecommendation(alert);
            alert.setRecommendations(recommendation); // Ajouter la recommandation à l'alerte
        }

        if (format.equalsIgnoreCase("PDF")) {
            return generatePDFReport(outputPath);
        } else if (format.equalsIgnoreCase("CSV")) {
            return generateCSVReport(outputPath);
        } else {
            System.err.println("Format de rapport non pris en charge : " + format);
            return false;
        }
    }

    private String generateRecommendation(IntrusionAlert alert) {
        switch (alert.getType()) {
            case "DoS Attack":
                return "Recommandation: Vérifiez les paramètres du pare-feu et limitez les connexions simultanées.";
            case "Port Scan":
                return "Recommandation: Bloquez les adresses IP suspectes et examinez les ports accessibles.";
            default:
                return "Aucune recommandation spécifique.";
        }
    }

    private boolean generatePDFReport(String outputPath) {
        String filePath = outputPath + File.separator + "RapportIntrusion.pdf";

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12); // Utilisation de la police standard
                contentStream.setLeading(14.5f);
                contentStream.newLineAtOffset(50, 750);

                contentStream.showText("Rapport d'alertes d'intrusion");
                contentStream.newLine();
                contentStream.showText("==================================");
                contentStream.newLine();

                for (IntrusionAlert alert : alerts) {
                    contentStream.showText("Type: " + alert.getType());
                    contentStream.newLine();
                    contentStream.showText("Intrusion: " + alert.getIntrusion());
                    contentStream.newLine();
                    contentStream.showText("Timestamp: " + alert.getTimestampString());
                    contentStream.newLine();
                    contentStream.showText("Degré de Danger: " + alert.getDangerLevel());
                    contentStream.newLine();
                    contentStream.showText("Recommandation: " + alert.getRecommendations());
                    contentStream.newLine();
                    contentStream.showText("----------------------------------");
                    contentStream.newLine();
                }

                contentStream.endText();
            }

            document.save(filePath);
            System.out.println("Rapport PDF généré avec succès dans : " + filePath);
            return true;

        } catch (IOException e) {
            System.err.println("Erreur lors de la génération du rapport PDF : " + e.getMessage());
            return false;
        }
    }

    private boolean generateCSVReport(String outputPath) {
        String filePath = outputPath + File.separator + "RapportIntrusions.csv";

        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeNext(new String[]{"Type", "Détails", "Timestamp", "Degré de Danger"});

            for (IntrusionAlert alert : alerts) {
                writer.writeNext(new String[]{
                        alert.getType(),
                        alert.getIntrusion(),
                        String.valueOf(alert.getTimestamp()),
                        alert.getDangerLevel()
                });
            }

            System.out.println("Rapport CSV généré avec succès dans : " + filePath);
            return true;

        } catch (IOException e) {
            System.err.println("Erreur lors de la génération du rapport CSV : " + e.getMessage());
            return false;
        }
    }

    private String selectSaveDirectory() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Sélectionnez un dossier pour enregistrer le rapport");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        } else {
            return null;
        }
    }




    public static void main(String[] args) {
        // Création de quelques alertes pour tester
        IntrusionAlert alert1 = new IntrusionAlert("DoS Attack", "DDoS détecté depuis 192.168.1.1", System.currentTimeMillis(), null);
        IntrusionAlert alert2 = new IntrusionAlert("Port Scan", "Balayage des ports détecté sur 10.0.0.5", System.currentTimeMillis(), null);

        // Initialisation du générateur de rapport
        ReportGenerator generator = new ReportGenerator(Arrays.asList(alert1, alert2));

        // Génération d'un rapport PDF et CSV
        //String chemin = "C:\\Rapports"; // Chemin sous Windows
        generator.generateReport("PDF");
        generator.generateReport("CSV");
    }
}
