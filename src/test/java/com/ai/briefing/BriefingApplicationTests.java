package com.ai.briefing;

import com.ai.briefing.dto.BriefingRequestDTO;
import com.ai.briefing.dto.BriefingResponseDTO;
import com.ai.briefing.model.BriefingCategory;
import com.ai.briefing.model.BriefingStatus;
import com.ai.briefing.service.BriefingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BriefingApplicationTests {

    @Autowired
    private BriefingService briefingService;

    @Test
    void contextLoads() {
        assertThat(briefingService).isNotNull();
    }

    @Test
    void createBriefing_shouldPersistAndReturn() {
        BriefingRequestDTO req = new BriefingRequestDTO();
        req.setTitle("Test Briefing");
        req.setSummary("Test summary");
        req.setCategory(BriefingCategory.GENERAL);
        req.setCreatedBy("tester");

        BriefingResponseDTO result = briefingService.createBriefing(req);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Briefing");
        assertThat(result.getStatus()).isEqualTo(BriefingStatus.DRAFT);
    }

    @Test
    void publishBriefing_shouldChangeStatus() {
        BriefingRequestDTO req = new BriefingRequestDTO();
        req.setTitle("To Publish");
        req.setCategory(BriefingCategory.TECHNOLOGY);

        BriefingResponseDTO created = briefingService.createBriefing(req);
        BriefingResponseDTO published = briefingService.publishBriefing(created.getId());

        assertThat(published.getStatus()).isEqualTo(BriefingStatus.PUBLISHED);
    }

    @Test
    void searchBriefings_shouldReturnMatchingResults() {
        BriefingRequestDTO req = new BriefingRequestDTO();
        req.setTitle("Unique Keyword XYZ briefing");
        req.setCategory(BriefingCategory.SCIENCE);
        briefingService.createBriefing(req);

        List<BriefingResponseDTO> results = briefingService.searchBriefings("XYZ");

        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getTitle()).contains("XYZ");
    }

    @Test
    void deleteBriefing_shouldRemoveRecord() {
        BriefingRequestDTO req = new BriefingRequestDTO();
        req.setTitle("Delete Me");
        req.setCategory(BriefingCategory.GENERAL);

        BriefingResponseDTO created = briefingService.createBriefing(req);
        briefingService.deleteBriefing(created.getId());

        List<BriefingResponseDTO> all = briefingService.getAllBriefings();
        assertThat(all.stream().noneMatch(b -> b.getId().equals(created.getId()))).isTrue();
    }
}
