package me.eatnows.newcruit.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

//@ComponentScan("me.eatnows.newcruit.controller")
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("me.eatnows.newcruit.controller"))  // 패키지 RequestMapping URI 추출
                .paths(PathSelectors.ant("/**"))  // 경로 패턴 URI만 추출
                .build()
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false);
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "newcruit REST API ", //title
                "newcruit 프로젝트", //description
                "v2", //version
                "서비스 약관 URL", //termsOfServiceUrl
                "linked2ev", //contactName
                "License", //license
                "localhost:8080"); //licenseUrl
    }
}
