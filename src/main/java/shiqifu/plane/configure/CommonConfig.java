package shiqifu.plane.configure;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shiqifu.plane.mapper.RedisChatMemoryStore;
import shiqifu.plane.service.CousultantService;


@Configuration
public class CommonConfig {
    @Autowired
    private OpenAiChatModel model;
    @Autowired
    private RedisChatMemoryStore store;
    @Autowired
    private StreamingChatModel streamingChatModel;

    @Value("${shiqi.ai.base-url}")
    private String url;

    @Value("${shiqi.ai.api-key}")
    private String apiKey;

    @Bean
    public CousultantService cousultantService(){
       return AiServices.builder(CousultantService.class)
               .chatModel(model)
               .streamingChatModel(streamingChatModel)
               .chatMemoryProvider(chatMemoryProvider())
               .build();
    }
    @Bean
    public ChatMemory chatMemory(){
        return MessageWindowChatMemory.builder()
                .maxMessages(20)
                .build();
    }
    @Bean
    public ChatMemoryProvider chatMemoryProvider(){
        return new ChatMemoryProvider() {
            @Override
            public ChatMemory get(Object o) {
                return MessageWindowChatMemory.builder()
                        .id(o)
                        .maxMessages(20)
                        .chatMemoryStore(store)
                        .build();
            }
        };
    }
}
