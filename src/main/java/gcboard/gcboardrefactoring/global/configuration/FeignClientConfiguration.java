package gcboard.gcboardrefactoring.global.configuration;

import feign.codec.ErrorDecoder;
import gcboard.gcboardrefactoring.global.infrastructure.feign.FeignClientErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableFeignClients(basePackages = "gcboard.gcboardrefactoring")
public class FeignClientConfiguration {

    private final FeignClientErrorDecoder feignClientErrorDecoder;

    @Bean
    public ErrorDecoder errorDecoder() {
        return feignClientErrorDecoder;
    }
}
