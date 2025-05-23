package net.menoni.pil.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties
@EnableScheduling
@ConfigurationPropertiesScan(basePackages = {
        "net.menoni.ws.discord",
        "net.menoni.pil.bot",
        "net.menoni.spring.commons"
})
@PropertySource(value = {
        "classpath:commons-application.properties"
})
@ComponentScan(basePackages = {
        "net.menoni.ws.discord",
        "net.menoni.pil.bot"
})
public class PILDiscordBot {
    public static void main( String[] args ) {
        SpringApplication.run(PILDiscordBot.class, args);
    }
}
