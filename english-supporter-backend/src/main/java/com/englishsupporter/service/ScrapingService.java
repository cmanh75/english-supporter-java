package com.englishsupporter.service;

import com.englishsupporter.entity.*;
import com.englishsupporter.repository.WordRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class ScrapingService {
    
    private static final Logger logger = LoggerFactory.getLogger(ScrapingService.class);
    
    private final WordRepository wordRepository;
    private final RestTemplate restTemplate;
    
    private static final String CAMBRIDGE_BASE_URL = "https://dictionary.cambridge.org/vi/search/direct/";
    private static final String TRATU_BASE_URL = "http://tratu.soha.vn/dict/en_vn/";
    
    @Autowired
    public ScrapingService(WordRepository wordRepository, RestTemplate restTemplate) {
        this.wordRepository = wordRepository;
        this.restTemplate = restTemplate;
    }
    
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36");
        headers.set("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        // Apache HttpClient sẽ tự động xử lý gzip/deflate, nhưng không hỗ trợ br/zstd
        headers.set("Accept-Encoding", "gzip, deflate");
        headers.set("Accept-Language", "vi,en-US;q=0.9,en;q=0.8");
        headers.set("Connection", "keep-alive");
        headers.set("Referer", "https://dictionary.cambridge.org/vi/");
        return headers;
    }
    
    @Transactional
    public Word scrapeWord(String wordText) {
        logger.info("Scraping word: {}", wordText);
        
        // Check if word already exists
        if (wordRepository.existsByText(wordText)) {
            logger.info("Word {} already exists in database", wordText);
            return wordRepository.findByText(wordText).orElse(null);
        }
        
        try {
            // Step 1: Scrape English data from Cambridge Dictionary
            ScrapeResult result = scrapeCambridgeDictionary(wordText);
            if (result == null || result.word == null) {
                logger.error("Failed to scrape Cambridge Dictionary for word: {}", wordText);
                return null;
            }
            
            Word word = result.word;
            String htmlEng = result.html;
            
            // Step 2: Scrape Vietnamese data from tratu.soha.vn
            // Only proceed if Vietnamese scraping succeeds
            boolean vietnameseSuccess = scrapeTratuDictionary(wordText, word);
            if (!vietnameseSuccess) {
                logger.error("Failed to scrape Vietnamese dictionary (tratu.soha.vn) for word: {}", wordText);
                // Transaction will rollback, word will not be saved
                return null;
            }
            
            // Step 3: Scrape English definitions and examples using the same HTML
            scrapeEnglishDefinitions(htmlEng, word);
            
            // Step 4: Only save to database if both English and Vietnamese scraping succeeded
            return wordRepository.save(word);
            
        } catch (Exception e) {
            logger.error("Error while scraping word {}: {}", wordText, e.getMessage(), e);
            // Transaction will rollback automatically
            throw new RuntimeException("Failed to scrape word: " + wordText, e);
        }
    }
    
    private static class ScrapeResult {
        Word word;
        String html;
        
        ScrapeResult(Word word, String html) {
            this.word = word;
            this.html = html;
        }
    }
    
    private ScrapeResult scrapeCambridgeDictionary(String wordText) {
        int maxRetries = 3;
        int backoff = 1;
        
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                // Build URL with query parameters
                URI uri = UriComponentsBuilder.fromHttpUrl(CAMBRIDGE_BASE_URL)
                        .queryParam("datasetsearch", "english")
                        .queryParam("q", wordText)
                        .build()
                        .toUri();
                
                logger.debug("Fetching Cambridge Dictionary URL (attempt {}): {}", attempt + 1, uri);
                
                HttpHeaders headers = createHeaders();
                headers.set("Host", "dictionary.cambridge.org");
                HttpEntity<String> entity = new HttpEntity<>(headers);
                
                ResponseEntity<String> response = restTemplate.exchange(
                        uri,
                        HttpMethod.GET,
                        entity,
                        String.class
                );
                
                String html = response.getBody();
                
                if (html == null || html.isEmpty()) {
                    logger.warn("HTML response is null or empty for word: {} (attempt {}), status: {}", 
                            wordText, attempt + 1, response.getStatusCode());
                    if (attempt < maxRetries - 1) {
                        try {
                            Thread.sleep(backoff * 1000);
                            backoff *= 2;
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                        continue;
                    }
                    return null;
                }
                
                logger.debug("Successfully fetched HTML, length: {}", html.length());
                
                Document doc = Jsoup.parse(html);
                Element posElement = doc.selectFirst(".pos");
                Element pronElement = doc.selectFirst(".pron");
                
                String type = safeText(posElement);
                String pronunciation = safeText(pronElement);
                
                if (type == null || pronunciation == null) {
                    logger.warn("Missing required fields for {}: type={}, pron={}", wordText, type, pronunciation);
                    return null;
                }
                
                Word word = new Word();
                word.setText(wordText);
                word.setType(type);
                word.setPronunciation(pronunciation);
                
                return new ScrapeResult(word, html);
                
            } catch (Exception e) {
                logger.error("Error scraping Cambridge Dictionary for {} (attempt {}): {}", 
                        wordText, attempt + 1, e.getMessage(), e);
                if (attempt < maxRetries - 1) {
                    try {
                        Thread.sleep(backoff * 1000);
                        backoff *= 2;
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    return null;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Scrape Vietnamese dictionary from tratu.soha.vn
     * @param wordText the word to scrape
     * @param word the Word entity to populate
     * @return true if successful, false if failed after all retries
     */
    private boolean scrapeTratuDictionary(String wordText, Word word) {
        int maxRetries = 3;
        int backoff = 1;
        
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                String url = TRATU_BASE_URL + URLEncoder.encode(wordText, StandardCharsets.UTF_8);
                logger.debug("Fetching tratu.soha.vn URL (attempt {}): {}", attempt + 1, url);
                
                HttpHeaders headers = createHeaders();
                headers.remove("Referer"); // Remove referer for tratu
                HttpEntity<String> entity = new HttpEntity<>(headers);
                
                ResponseEntity<String> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        String.class
                );
                
                String html = response.getBody();
                
                if (html == null || html.isEmpty()) {
                    logger.warn("HTML response is null or empty for tratu.soha.vn word: {} (attempt {}), status: {}", 
                            wordText, attempt + 1, response.getStatusCode());
                    if (attempt < maxRetries - 1) {
                        try {
                            Thread.sleep(backoff * 1000);
                            backoff *= 2;
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                        continue;
                    }
                    logger.error("Failed to scrape tratu.soha.vn after {} attempts for word: {}", maxRetries, wordText);
                    return false; // Failed after all retries
                }
                
                logger.debug("Successfully fetched tratu HTML, length: {}", html.length());
            
                Document doc = Jsoup.parse(html);
                Elements categories = doc.select(".section-h3");
                
                // Check if we found any categories (data validation)
                if (categories.isEmpty()) {
                    logger.warn("No categories found in tratu.soha.vn for word: {} (attempt {})", wordText, attempt + 1);
                    if (attempt < maxRetries - 1) {
                        try {
                            Thread.sleep(backoff * 1000);
                            backoff *= 2;
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                        continue;
                    }
                    logger.error("No categories found after {} attempts for word: {}", maxRetries, wordText);
                    return false; // No data found after all retries
                }
                
                for (Element categoryElement : categories) {
                    Element categoryNameElement = categoryElement.selectFirst(".mw-headline");
                    if (categoryNameElement == null) {
                        continue;
                    }
                    
                    String categoryName = categoryNameElement.text().trim();
                    Category category = new Category();
                    category.setCategory(categoryName);
                    category.setWord(word);
                    if (word.getCategories() == null) {
                        word.setCategories(new java.util.ArrayList<>());
                    }
                    word.getCategories().add(category);
                    
                    Elements meanings = categoryElement.select(".section-h5");
                    for (Element meaningElement : meanings) {
                        Element meanEl = meaningElement.selectFirst(".mw-headline");
                        if (meanEl == null) {
                            continue;
                        }
                        
                        String meaningText = meanEl.text().trim();
                        String synonym = "";
                        
                        if ("Từ đồng nghĩa".equals(categoryName) || "Từ trái nghĩa".equals(categoryName)) {
                            Element synElement = meaningElement.selectFirst("dd");
                            if (synElement != null) {
                                synonym = synElement.text().trim();
                            }
                        }
                        
                        Meaning meaning = new Meaning();
                        meaning.setMeaning((meaningText + " " + synonym).trim());
                        meaning.setCategory(category);
                        if (category.getMeanings() == null) {
                            category.setMeanings(new java.util.ArrayList<>());
                        }
                        category.getMeanings().add(meaning);
                    }
                }
                
                logger.info("Successfully scraped Vietnamese dictionary for word: {}", wordText);
                return true; // Success, exit retry loop
                
            } catch (Exception e) {
                logger.error("Error scraping tratu.soha.vn for {} (attempt {}): {}", 
                        wordText, attempt + 1, e.getMessage(), e);
                if (attempt < maxRetries - 1) {
                    try {
                        Thread.sleep(backoff * 1000);
                        backoff *= 2;
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    logger.error("Failed to scrape tratu.soha.vn after {} attempts for word: {}", maxRetries, wordText);
                    return false; // Failed after all retries
                }
            }
        }
        
        // If we reach here, all retries failed
        logger.error("Failed to scrape tratu.soha.vn after {} attempts for word: {}", maxRetries, wordText);
        return false;
    }
    
    private void scrapeEnglishDefinitions(String htmlEng, Word word) {
        try {
            if (htmlEng == null || htmlEng.isEmpty()) {
                logger.warn("HTML is null or empty for definitions of word: {}", word.getText());
                return;
            }
            
            logger.debug("Parsing English definitions from HTML, length: {}", htmlEng.length());
            
            Document doc = Jsoup.parse(htmlEng);
            Elements defBlocks = doc.select(".def-block");
            
            for (Element defBlock : defBlocks) {
                Element defElement = defBlock.selectFirst(".def");
                if (defElement == null) {
                    continue;
                }
                
                String definitionText = safeText(defElement);
                if (definitionText == null || definitionText.isEmpty()) {
                    continue;
                }
                
                EngDefinition engDefinition = new EngDefinition();
                engDefinition.setDefinition(definitionText);
                engDefinition.setWord(word);
                if (word.getDefinitions() == null) {
                    word.setDefinitions(new java.util.ArrayList<>());
                }
                word.getDefinitions().add(engDefinition);
                
                Elements examples = defBlock.select(".eg");
                for (Element exampleElement : examples) {
                    String exampleText = safeText(exampleElement);
                    if (exampleText == null || exampleText.isEmpty()) {
                        continue;
                    }
                    
                    Example example = new Example();
                    example.setExample(exampleText);
                    example.setDefinition(engDefinition);
                    if (engDefinition.getExamples() == null) {
                        engDefinition.setExamples(new java.util.ArrayList<>());
                    }
                    engDefinition.getExamples().add(example);
                }
            }
            
        } catch (Exception e) {
            logger.error("Error scraping English definitions for {}: {}", word.getText(), e.getMessage(), e);
        }
    }
    
    private String safeText(Element element) {
        if (element == null) {
            return null;
        }
        try {
            return element.text().trim();
        } catch (Exception e) {
            return null;
        }
    }
}

