package com.sofrecom.tickit.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sofrecom.tickit.dto.AIAnalysisResponse;
import com.sofrecom.tickit.dto.AIResolutionResponse;
import com.sofrecom.tickit.dto.ActeTraitementResponse;
import com.sofrecom.tickit.dto.BlockagePredictionResponse;
import com.sofrecom.tickit.model.LedgerEntry;
import com.sofrecom.tickit.model.Ticket;
import com.sofrecom.tickit.service.AIService;
import com.sofrecom.tickit.service.ActeTraitementService;
import com.sofrecom.tickit.service.LedgerService;
import com.sofrecom.tickit.service.TicketService;
import com.sofrecom.tickit.util.AIPromptBuilder;
import com.sofrecom.tickit.util.AIResponseParser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIServiceImpl implements AIService {

    @Value("${llm.endpoint}")
    private String llmEndpoint;

    @Value("${llm.model}")
    private String modelName;

    @Value("${rag.endpoint:http://localhost:8001}")
    private String ragEndpoint;

    @Value("${rag.enabled:false}")
    private boolean ragEnabled;

    private final RestTemplate restTemplate;
    private final ActeTraitementService acteTraitementService;
    private final TicketService ticketService;
    private final LedgerService ledgerService;
    private final ObjectMapper objectMapper;

    @Override
    public String queryWithContext(String prompt, String additionalContext) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("model", modelName);
            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", buildEnhancedPrompt(prompt, additionalContext));
            body.put("messages", Collections.singletonList(message));
            body.put("temperature", 0.3);
            body.put("max_tokens", 1000);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(llmEndpoint, request, Map.class);
            return new AIResponseParser().extractAIResponse(response);
        } catch (Exception e) {
            log.error("Error querying AI service: {}", e.getMessage(), e);
            return "Error processing AI request";
        }
    }

    @Override
    public AIAnalysisResponse analyzeSystemStatistics() {
        log.info("Starting FTTH system performance analysis");

        try {
            Map<String, Double> resolutionMetrics = ticketService.getResolutionMetrics();
            if (resolutionMetrics == null) resolutionMetrics = new HashMap<>();

            Map<String, Object> trends = ticketService.getTicketTrends(30);
            if (trends == null) trends = new HashMap<>();

            String response = queryWithContext(
                AIPromptBuilder.buildSystemAnalysisPrompt(resolutionMetrics, trends),
                "Analyzing FTTH system performance metrics and trends"
            );

            if (response == null || response.equals("Error processing AI request")) {
                throw new RuntimeException("AI service returned invalid response");
            }

            AIResponseParser parser = new AIResponseParser();
            return AIAnalysisResponse.builder()
                .performanceMetrics(parser.extractPerformanceMetrics(response))
                .trends(parser.extractTrends(response))
                .riskAreas(parser.extractRiskAreas(response))
                .recommendations(parser.extractRecommendations(response))
                .build();

        } catch (Exception e) {
            log.error("Error performing system analysis: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to complete system analysis: " + e.getMessage(), e);
        }
    }

    @Override
    public AIResolutionResponse suggestResolution(UUID ticketId) {
        log.info("Generating resolution suggestions for ticket {}", ticketId);
        StopWatch sw = new StopWatch("ai-pipeline-ticket-" + ticketId);

        try {
            sw.start("contextRetrieval");
            Ticket ticket = ticketService.getTicketById(ticketId);
            List<LedgerEntry> caseHistory = ledgerService.getLedgerForCase(
                ticket.getActeTraitement().getAssociatedCase().getId().toString(),
                "TREATMENT_ACT"
            );

            String prompt;
            if (ragEnabled) {
                String queryText = ticket.getSubject() +
                    (ticket.getDescription() != null ? " " + ticket.getDescription() : "");
                List<Map<String, Object>> ragChunks = retrieveContextFromRAG(queryText);
                prompt = AIPromptBuilder.buildRagResolutionPrompt(ticket, ragChunks, caseHistory);
                log.info("RAG retrieval returned {} chunks for ticket {}", ragChunks.size(), ticketId);
            } else {
                List<Ticket> similarTickets = ticketService.findSimilarResolvedTickets(
                    ticket.getSubject(), ticket.getType(), 5);
                prompt = AIPromptBuilder.buildResolutionSuggestionPrompt(ticket, similarTickets, caseHistory);
            }
            sw.stop();

            sw.start("llmCall");
            String response = queryWithContext(prompt, "Analyzing ticket context and suggesting resolution");
            sw.stop();

            sw.start("responseParser");
            AIResponseParser parser = new AIResponseParser();
            String primarySolution = parser.extractPrimarySolution(response);
            if (primarySolution == null || primarySolution.isEmpty()) {
                log.warn("No primary solution found for ticket {}", ticketId);
            }
            sw.stop();

            long totalMs = sw.getTotalTimeMillis();
            log.info("AI Pipeline timing for ticket {}: total={}ms | contextRetrieval={}ms | llmCall={}ms | parser={}ms",
                ticketId, totalMs,
                sw.getTaskInfo()[0].getTimeMillis(),
                sw.getTaskInfo()[1].getTimeMillis(),
                sw.getTaskInfo()[2].getTimeMillis()
            );

            savePipelineTiming(ticketId.toString(), sw);

            return AIResolutionResponse.builder()
                .primarySolution(primarySolution)
                .alternativeSolutions(parser.extractAlternativeSolutions(response))
                .requiredSteps(parser.extractRequiredSteps(response))
                .potentialRisks(parser.extractPotentialRisks(response))
                .requiredResources(parser.extractRequiredResources(response))
                .confidenceScore(calculateConfidenceScore(3))
                .estimatedEffort(parser.extractEstimatedEffort(response))
                .build();

        } catch (Exception e) {
            log.error("Error generating resolution for ticket {}: {}", ticketId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate resolution suggestions", e);
        }
    }

    @Override
    public BlockagePredictionResponse predictBlockageRiskWithDetails(String treatmentActId) {
        try {
            ActeTraitementResponse act = acteTraitementService.getActeTraitementById(treatmentActId);
            List<LedgerEntry> similarActs = ledgerService.findSimilarTreatmentActs(
                act.getPrestationName(), act.getPriorite(), 10);

            String response = queryWithContext(
                AIPromptBuilder.buildBlockagePredictionPrompt(act, similarActs),
                "Analyzing similar treatment acts for blockage prediction"
            );

            return new AIResponseParser().parseBlockagePrediction(response);
        } catch (Exception e) {
            log.error("Error predicting blockage for act {}: {}", treatmentActId, e.getMessage(), e);
            return new AIResponseParser().createDefaultPrediction();
        }
    }

    @Override
    public List<Map<String, Object>> benchmarkPipeline() {
        List<String[]> syntheticTickets = Arrays.asList(
            new String[]{"Cable damaged during excavation on Rue de la Paix", "Physical fiber cut by contractor"},
            new String[]{"Permit delay for trench work in Zone Industrielle", "Municipal authorization pending"},
            new String[]{"ONT equipment failure at client premises", "Device not powering on after swap"},
            new String[]{"Access denied at residence building for installation", "Concierge unavailable"},
            new String[]{"Missing fiche navette documentation for BRAME act", "Document not submitted"},
            new String[]{"SI-Terrain mismatch in Ariana sector GIS data", "Route shown in system does not exist"},
            new String[]{"Optical signal attenuation too high on new cable", "Loss exceeds -30dB threshold"},
            new String[]{"Weather conditions preventing outdoor splicing work", "Heavy rain forecast for 3 days"},
            new String[]{"Technician resource shortage for GRAFIC deployment", "Team unavailable until next week"},
            new String[]{"Cable route blocked by existing water pipes", "Infrastructure conflict on main street"},
            new String[]{"Fiber splice damaged during BRAME reconnexion", "Fusion splice failed quality test"},
            new String[]{"Client not reachable for appointment confirmation", "Phone number incorrect in system"},
            new String[]{"Sub-contractor delayed on BO RIP phase", "External team behind schedule"},
            new String[]{"Equipment delivery delayed from warehouse", "ONT stock unavailable locally"},
            new String[]{"Permit for aerial cable crossing expired", "Renewal took longer than expected"},
            new String[]{"GC excavation blocked by unmarked underground utilities", "Cable strike risk identified"},
            new String[]{"GEORESO cable route flooded after heavy rain", "Duct system filled with water"},
            new String[]{"Documentation incomplete for MUTATION NATNE act", "Missing signature on bandeau"},
            new String[]{"Optical power meter showing inconsistent readings", "Equipment calibration required"},
            new String[]{"XOIP interconnexion phase blocked by third-party RIP", "Handoff agreement not signed"}
        );

        List<Map<String, Object>> results = new ArrayList<>();

        for (String[] ticket : syntheticTickets) {
            StopWatch sw = new StopWatch();
            Map<String, Object> timing = new HashMap<>();
            timing.put("subject", ticket[0]);
            timing.put("description", ticket[1]);
            timing.put("timestamp", LocalDateTime.now().toString());

            try {
                sw.start("contextRetrieval");
                List<Map<String, Object>> ragChunks = ragEnabled
                    ? retrieveContextFromRAG(ticket[0] + " " + ticket[1])
                    : Collections.emptyList();
                sw.stop();

                String prompt = ragEnabled
                    ? buildSyntheticRagPrompt(ticket[0], ticket[1], ragChunks)
                    : buildSyntheticFlatPrompt(ticket[0], ticket[1]);

                sw.start("llmCall");
                String response = queryWithContext(prompt, "Benchmark run - synthetic ticket");
                sw.stop();

                timing.put("contextRetrievalMs", sw.getTaskInfo()[0].getTimeMillis());
                timing.put("llmCallMs", sw.getTaskInfo()[1].getTimeMillis());
                timing.put("totalMs", sw.getTotalTimeMillis());
                timing.put("status", "success");
                timing.put("ragEnabled", ragEnabled);

            } catch (Exception e) {
                sw.stop();
                timing.put("totalMs", sw.getTotalTimeMillis());
                timing.put("status", "error");
                timing.put("error", e.getMessage());
                log.warn("Benchmark run failed for '{}': {}", ticket[0], e.getMessage());
            }

            results.add(timing);
        }

        saveBenchmarkResults(results);
        return results;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> retrieveContextFromRAG(String query) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("query", query);
            body.put("top_k", 3);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            Map<String, Object> response = restTemplate.postForObject(
                ragEndpoint + "/retrieve", request, Map.class);

            if (response == null) return Collections.emptyList();
            List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
            return results != null ? results : Collections.emptyList();

        } catch (Exception e) {
            log.warn("RAG service unavailable, falling back to DB search: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private String buildSyntheticRagPrompt(String subject, String description, List<Map<String, Object>> ragChunks) {
        StringBuilder sb = new StringBuilder();
        sb.append("Suggest resolution for FTTH ticket:\n");
        sb.append("Subject: ").append(subject).append("\n");
        sb.append("Description: ").append(description).append("\n\n");
        sb.append("Top-3 Similar Historical Records:\n");
        int i = 1;
        for (Map<String, Object> chunk : ragChunks) {
            sb.append(String.format("Record %d: [%s] %s - Resolution: %s\n",
                i++,
                chunk.getOrDefault("blockage_type", "unknown"),
                chunk.getOrDefault("description", ""),
                chunk.getOrDefault("resolution_steps", "")
            ));
        }
        sb.append("\nProvide: 1. Primary Action 2. Required Steps 3. Known Risks");
        return sb.toString();
    }

    private String buildSyntheticFlatPrompt(String subject, String description) {
        return "Suggest resolution for FTTH ticket:\n" +
            "Subject: " + subject + "\n" +
            "Description: " + description + "\n\n" +
            "Provide: 1. Primary Action 2. Required Steps 3. Known Risks";
    }

    private void savePipelineTiming(String ticketId, StopWatch sw) {
        try {
            File dir = new File("measurements");
            dir.mkdirs();
            File file = new File(dir, "pipeline_latency.json");

            List<Map<String, Object>> existing = new ArrayList<>();
            if (file.exists()) {
                existing = objectMapper.readValue(file,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class));
            }

            Map<String, Object> entry = new HashMap<>();
            entry.put("ticketId", ticketId);
            entry.put("timestamp", LocalDateTime.now().toString());
            entry.put("totalMs", sw.getTotalTimeMillis());
            if (sw.getTaskCount() >= 3) {
                entry.put("contextRetrievalMs", sw.getTaskInfo()[0].getTimeMillis());
                entry.put("llmCallMs", sw.getTaskInfo()[1].getTimeMillis());
                entry.put("parserMs", sw.getTaskInfo()[2].getTimeMillis());
            }
            entry.put("ragEnabled", ragEnabled);

            existing.add(entry);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, existing);
        } catch (Exception e) {
            log.warn("Failed to save pipeline timing: {}", e.getMessage());
        }
    }

    private void saveBenchmarkResults(List<Map<String, Object>> results) {
        try {
            File dir = new File("measurements");
            dir.mkdirs();
            File file = new File(dir, "pipeline_latency.json");
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, results);
            log.info("Benchmark results saved to measurements/pipeline_latency.json");
        } catch (Exception e) {
            log.warn("Failed to save benchmark results: {}", e.getMessage());
        }
    }

    private String buildEnhancedPrompt(String originalPrompt, String context) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an AI analyst specializing in FTTH deployment ticketing systems. Focus on:\n")
              .append("- Fiber installation workflow analysis\n")
              .append("- Network deployment ticket patterns\n")
              .append("- Installation act resolution paths\n")
              .append("- SLA compliance predictions\n\n");

        if (context != null && !context.isEmpty()) {
            prompt.append("Context:\n").append(context).append("\n\n");
        }

        prompt.append("Query:\n").append(originalPrompt);
        return prompt.toString();
    }

    private double calculateConfidenceScore(int similarCaseCount) {
        if (similarCaseCount > 10) return 0.9;
        if (similarCaseCount > 5) return 0.8;
        if (similarCaseCount > 2) return 0.7;
        return 0.6;
    }
}
