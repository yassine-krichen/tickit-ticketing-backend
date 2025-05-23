package com.sofrecom.tickit.controller;

import com.sofrecom.tickit.dto.*;
import com.sofrecom.tickit.model.*;
import com.sofrecom.tickit.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "AI", description = "AI-powered analysis and predictions endpoints")
public class AIController {

    private final AIService aiService;
    private final TicketService ticketService;
    private final ActeTraitementService acteTraitementService;
    private final LedgerService ledgerService;
    private final TicketActivityService activityService;

    @PostMapping("/analyse")
    @Operation(summary = "Analyze ticketing system statistics")
    @ApiResponse(responseCode = "200", description = "Analysis completed successfully")
    public ResponseEntity<String> analyzeSystem() {
        // Get ticket statistics
        List<TicketCount> statusCounts = ticketService.getTicketCountByStatus();
        List<TicketCount> typeCounts = ticketService.getTicketCountByType();

        // Build the prompt
        StringBuilder prompt = new StringBuilder();
        prompt.append("Analyze the following ticketing system statistics:\n\n");

        prompt.append("Tickets by Status:\n");
        for (TicketCount count : statusCounts) {
            prompt.append("- ").append(count.getCategory()).append(": ")
                  .append(count.getCount()).append("\n");
        }

        prompt.append("\nTickets by Type:\n");
        for (TicketCount count : typeCounts) {
            prompt.append("- ").append(count.getCategory()).append(": ")
                  .append(count.getCount()).append("\n");
        }

        prompt.append("\nProvide a comprehensive analysis including:\n");
        prompt.append("1. Key trends and patterns\n");
        prompt.append("2. Areas of concern\n");
        prompt.append("3. Recommendations for improvement\n");

        return ResponseEntity.ok(aiService.query(prompt.toString()));
    }

    @PostMapping("/predict/{treatmentActId}")
    @Operation(summary = "Predict if a treatment act is likely to be blocked")
    @ApiResponse(responseCode = "200", description = "Prediction generated successfully")
    public ResponseEntity<AIPredictionResponse> predictBlockage(
            @Parameter(description = "ID of the treatment act to analyze")
            @PathVariable UUID treatmentActId) {

        // Load treatment act
        ActeTraitementResponse act = acteTraitementService.getActeTraitementById(treatmentActId.toString());
        
        // Get ledger entries
        List<LedgerEntry> ledgerEntries = ledgerService.getLedgerForCase(
            act.getCaseId().toString(),
            "TREATMENT_ACT"
        );

        // Build prompt
        StringBuilder prompt = new StringBuilder();
        prompt.append("Analyze this treatment act and predict blockage:\n\n");
        
        prompt.append("Treatment Act Details:\n");
        prompt.append("- ID: ").append(act.getIdacte()).append("\n");
        prompt.append("- Type: ").append(act.getType_element()).append("\n");
        prompt.append("- Duration: ").append(act.getDuree()).append(" days\n");
        prompt.append("- Priority: ").append(act.getPriorite()).append("\n");

        prompt.append("\nLedger History:\n");
        for (LedgerEntry entry : ledgerEntries) {
            prompt.append("- ").append(entry.getTimestamp())
                  .append(" : ").append(entry.getActionType())
                  .append("\n");
        }

        prompt.append("\nIs this treatment act likely to be BLOCKED? ");
        prompt.append("Answer with 'Yes' or 'No' followed by a brief explanation.\n");

        String response = aiService.query(prompt.toString());
        boolean isLikelyBlocked = response.toLowerCase().startsWith("yes");
        String explanation = response.replaceFirst("(?i)yes|no", "").trim();

        return ResponseEntity.ok(AIPredictionResponse.builder()
            .likelyBlocked(isLikelyBlocked)
            .explanation(explanation)
            .build());
    }

    @PostMapping("/suggest/{ticketId}")
    @Operation(summary = "Get AI suggestions for ticket resolution")
    @ApiResponse(responseCode = "200", description = "Suggestions generated successfully")
    public ResponseEntity<String> suggestResolution(
            @Parameter(description = "ID of the ticket to analyze")
            @PathVariable UUID ticketId) {

        // Load ticket with activities
        Ticket ticket = ticketService.getTicketById(ticketId);
        Page<TicketActivityResponse> activities = activityService.getTimeline(
            ticketId, null, null, null, 0, 10, "startTime", "desc"
        );

        // Build prompt
        StringBuilder prompt = new StringBuilder();
        prompt.append("Analyze this ticket and suggest resolution steps:\n\n");

        prompt.append("Ticket Details:\n");
        prompt.append("Subject: ").append(ticket.getSubject()).append("\n");
        prompt.append("Description: ").append(ticket.getDescription()).append("\n");
        prompt.append("Status: ").append(ticket.getStatus()).append("\n");
        prompt.append("Priority: ").append(ticket.getPriority()).append("\n");
        prompt.append("Type: ").append(ticket.getType()).append("\n");

        prompt.append("\nRecent Activities:\n");
        for (TicketActivityResponse activity : activities.getContent()) {
            prompt.append("- ").append(activity.getType())
                  .append(" (").append(activity.getStartTime()).append(")")
                  .append(": ").append(activity.getDescription())
                  .append("\n");
        }

        prompt.append("\nBased on this context, suggest 2-3 concrete steps to resolve this ticket. ");
        prompt.append("For each step, provide a clear action and brief justification.\n");

        return ResponseEntity.ok(aiService.query(prompt.toString()));
    }
}
