package com.clinicai.summarization.app;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LangChainConfig {
    
    @Value("${langchain.openai.api-key}")
    private String apiKey;
    
    @Value("${langchain.openai.model:gpt-3.5-turbo}")
    private String model;
    
    @Value("${langchain.openai.timeout:60}")
    private int timeout;
    
    @Value("${langchain.openai.max-tokens:1000}")
    private int maxTokens;
    
    @Value("${langchain.openai.temperature:0.3}")
    private double temperature;
    
    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(model)
                .timeout(timeout)
                .maxTokens(maxTokens)
                .temperature(temperature)
                .build();
    }
}