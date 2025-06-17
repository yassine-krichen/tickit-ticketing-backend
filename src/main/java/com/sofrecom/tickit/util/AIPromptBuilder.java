package com.sofrecom.tickit.util;

import com.sofrecom.tickit.dto.ActeTraitementResponse;
import com.sofrecom.tickit.model.LedgerEntry;
import com.sofrecom.tickit.model.Ticket;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class AIPromptBuilder {
    
    public static String buildSystemAnalysisPrompt(Map<String, Double> resolutionMetrics, Map<String, Object> trends) {
        return String.format(
            "Analyze FTTH deployment system performance focusing on treatment acts blockage patterns:\n\n" +
            "Current Metrics:\n" +
            "- Average Resolution: %.2f hours\n" +
            "- Fastest Resolution: %.2f hours\n" +
            "- Slowest Resolution: %.2f hours\n" +
            "- Total Cases: %d\n\n" +
            "Provide detailed analysis with these specific sections:\n\n" +
            "1. Performance Metrics:\n" +
            "- Treatment act success rates by type (BRAME, Grafic, etc.)\n" +
            "- Documentation completion rates (fiche navette, bandeau)\n" +
            "- SLA compliance metrics\n\n" +
            "2. Trends:\n" +
            "- Most common blockage causes (SI-Terrain mismatch, missing documentation)\n" +
            "- Treatment act types with highest block rates\n" +
            "- Peak blockage periods or patterns\n\n" +
            "3. Recommendations:\n" +
            "Please list actionable recommendations under a “Recommendations:” section, each prefixed by a dash.\\n" +
            "4. Risk Areas:\n" +
            "- Documentation compliance risks\n" +
            "- Infrastructure verification needs\n" +
            "- Inter-commune coverage issues",
            resolutionMetrics.get("averageResolutionHours"),
            resolutionMetrics.get("fastestResolutionHours"),
            resolutionMetrics.get("slowestResolutionHours"),
            trends.get("totalTickets")
        );
    }

    public static String buildResolutionSuggestionPrompt(Ticket ticket, List<Ticket> similarTickets, List<LedgerEntry> caseHistory) {
        return String.format(
            "Suggest resolution actions for this FTTH deployment ticket following standard intervention patterns:\n\n" +
            "Current Ticket:\n" +
            "ID: %s\n" +
            "Type: %s\n" +
            "Subject: %s\n" +
            "Status: %s\n\n" +
            "Similar Cases: %s\n\n" +
            "History: %s\n\n" +
            "IMPORTANT: You must provide a resolution with ALL sections below:\n\n" +
            "1. Primary Action:\n" +
            "- Recommendations can be actions such as send email to relevant 3rd parties or call client\n" +
            "- Must include next follow-up action such as 'Resend email'\n" +
            "- Describe the main action to be taken\n\n" +
            "2. Alternative Actions:\n" +
            "- List backup solutions\n\n" +
            "3. Required Steps:\n" +
            "- Implementation steps\n\n" +
            "4. Known Risks:\n" +
            "- Potential issues\n\n" +
            "5. Required Resources:\n" +
            "- Tools and personnel needed\n\n" +
            "Note: Primary Action section is mandatory and must follow the exact format specified.",
            ticket.getId(),
            ticket.getType(),
            ticket.getSubject(),
            ticket.getStatus(),
            formatSimilarTickets(similarTickets),
            formatCaseHistory(caseHistory)
        );
    }

    public static String buildBlockagePredictionPrompt(ActeTraitementResponse act, List<LedgerEntry> similarActs) {
        return String.format(
            "Analyze this FTTH treatment act for blockage risks:\n\n" +
            "Treatment Act:\n" +
            "- ID: %s\n" +
            "- Type: %s\n" +
            "- Status: %s\n" +
            "- Priority: %s\n\n" +
            "Similar Acts History:\n%s\n\n" +
            "Provide structured risk assessment:\n" +
            "1. Risk Level (LOW/MEDIUM/HIGH)\n" +
            "2. Detailed Explanation (max 500 chars)\n" +
            "3. Risk Score (0-100)\n" +
            "4. Key Risk Factors",
            act.getIdacte(),
            act.getPrestationName(),
            act.getStatus(),
            act.getPriorite(),
            formatSimilarActs(similarActs)
        );
    }

    private static String formatSimilarTickets(List<Ticket> tickets) {
        StringBuilder sb = new StringBuilder();
        for (Ticket t : tickets) {
            sb.append(String.format("- %s: %s (Resolved in %d hours)\n",
                t.getId(),
                t.getSubject(),
                Duration.between(t.getCreatedAt(), t.getResolvedAt()).toHours()
            ));
        }
        return sb.toString();
    }

    private static String formatCaseHistory(List<LedgerEntry> entries) {
        StringBuilder sb = new StringBuilder();
        for (LedgerEntry entry : entries) {
            sb.append(String.format("- %s: %s\n",
                entry.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                entry.getActionType()
            ));
        }
        return sb.toString();
    }

    private static String formatSimilarActs(List<LedgerEntry> acts) {
        StringBuilder sb = new StringBuilder();
        for (LedgerEntry act : acts) {
            sb.append(String.format("- %s: %s\n",
                act.getEntityId(),
                act.getActionType()
            ));
        }
        return sb.toString();
    }
}