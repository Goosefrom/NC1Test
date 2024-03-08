package com.goose.nc1test.config;

import com.goose.nc1test.dto.NewsDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class Util {

    /** time interval for scheduled task **/
    public static final int TIME_INTERVAL_IN_MILLISECONDS = 1000 * 60 * 20;
    public static final String CRON_INTERVAL_FOR_CLEANUP = "0 0 0 * * *";

    /** url for parsing **/
    public static final String URL = "https://www.bbc.com/ukrainian";
    /** request referrer url **/
    public static final String REFERRER = "https://google.com";
    /** request user agent **/
    public static final String USER_AGENT = "Mozilla";
    /** request timeout **/
    public static final int TIMEOUT = 5000;

    /** parse selector patterns **/
    public static final String FIRST_MAIN_PAGE_HEADLINE_SELECTOR = ".bbc-uk8dsi";
    public static final String SECOND_MAIN_PAGE_HEADLINE_SELECTOR = ".bbc-xs8nl8";
    public static final String HEADLINE_SELECTOR = ".bbc-csfh25";
    public static final String DESCRIPTION_SELECTOR = ".bbc-1y32vyc";
    public static final String PUBLICATION_TIME_SELECTOR = ".bbc-j3wi5n";

    /** select attributes **/
    public static final String HREF = "href";
    public static final String DATETIME = "datetime";

    /** time words **/
    public static final String HOUR = "годин";
    public static final String MINUTE = "хвилин";

    /** jsoup request method **/
    public static Document makeRequest(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .timeout(TIMEOUT)
                .referrer(REFERRER)
                .get();
    }

    /** parse news from one article **/
    public static NewsDTO parseArticle(String href, LocalTime timeNow) throws IOException {
        NewsDTO dto = new NewsDTO();
        Document article = makeRequest(href);

        List<String> headlineResults = article.select(HEADLINE_SELECTOR).eachText();
        if (headlineResults.size() == 0) return null;
        String headline = headlineResults.stream().findFirst().get();
        dto.setHeadline(headline);

        Elements textNodes = article.select(DESCRIPTION_SELECTOR);
        List<String> text = new ArrayList<>();
        for(Element el : textNodes) {
            text.add(el.childrenSize() > 0 ? el.child(0).text() : el.text());
        }
        dto.setDescription(String.join(" ", text));

        List<String> timeResults = article.select(PUBLICATION_TIME_SELECTOR).eachText();
        if (timeResults.size() == 0) return null;
        // parse only news from today
        if (LocalDate.parse(article.select(PUBLICATION_TIME_SELECTOR).eachAttr(DATETIME).get(0))
                .isBefore(LocalDate.now())) return null;


        String ago = timeResults.stream().findFirst().get();
        LocalTime time = ago.contains(HOUR) ? timeNow.minusHours(Integer.parseInt(ago.split(" ")[0]))
                : ago.contains(MINUTE) ? timeNow.minusMinutes(Integer.parseInt(ago.split(" ")[0]))
                : timeNow;
        dto.setTime(time);

        return dto;
    }
}
