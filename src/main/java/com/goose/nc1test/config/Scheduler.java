package com.goose.nc1test.config;

import com.goose.nc1test.dto.NewsDTO;
import com.goose.nc1test.service.NewsService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Configuration
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class Scheduler {

    private final NewsService service;

    /** scheduled parser from bbc.com/ukrainian for each 20m **/
    @PostConstruct
    @Scheduled(fixedRate = Util.TIME_INTERVAL_IN_MILLISECONDS)
    public void scheduledParse() throws Exception {
        LocalTime timeNow = LocalTime.now();
        Document doc = Util.makeRequest(Util.URL);

        Elements headlines1 = doc.select(Util.FIRST_MAIN_PAGE_HEADLINE_SELECTOR);
        Elements headlines2 = doc.select(Util.SECOND_MAIN_PAGE_HEADLINE_SELECTOR);
        List<String> unsortedHrefs = headlines1.eachAttr(Util.HREF);
        unsortedHrefs.addAll(headlines2.eachAttr(Util.HREF));
        List<String> sortedHrefs = unsortedHrefs.stream().filter(href -> href.contains(Util.URL)).toList();
        for(String sortedHref : sortedHrefs) {
            NewsDTO dto = Util.parseArticle(sortedHref, timeNow);
            if (Objects.nonNull(dto)) {
                NewsDTO savedNews = service.create(dto);
            }
        }
    }

    @PostConstruct
    @Scheduled(cron = Util.CRON_INTERVAL_FOR_CLEANUP)
    public void scheduledCleanup() {
        LocalTime timeNow = LocalTime.now();
        service.deleteAll();
        log.info("On current run task cleanup is done, time is {}", timeNow.toString());
    }
}
