package ibeere.config;

import freemarker.template.TemplateException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import java.io.IOException;

@Configuration
public class FreemarkerConfig {

    @Bean
    public FreeMarkerConfigurationFactoryBean getFreeMarkerConfiguration() throws IOException, TemplateException {
        FreeMarkerConfigurationFactoryBean bean = new FreeMarkerConfigurationFactoryBean();
        bean.setTemplateLoaderPath("/templates/");
        bean.afterPropertiesSet();
        return bean;
    }
}