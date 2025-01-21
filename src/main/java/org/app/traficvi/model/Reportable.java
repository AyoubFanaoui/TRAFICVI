package org.app.traficvi.model;

public interface Reportable {
    /**
     * Génère un rapport dans le format spécifié.
     * @param format Le format du rapport ("PDF" ou "CSV").
     * @return Un boolean indiquant si le rapport a été généré avec succès.
     */
     boolean generateReport(String format);
}
