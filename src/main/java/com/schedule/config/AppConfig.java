package com.schedule.config;

import com.ibm.icu.text.RuleBasedNumberFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

@Configuration
public class AppConfig {
    @Bean
    public RuleBasedNumberFormat ruleBasedNumberFormat() {
        return new RuleBasedNumberFormat(Locale.forLanguageTag("en"), RuleBasedNumberFormat.SPELLOUT);
    }
}
