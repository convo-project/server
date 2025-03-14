package com.bj.convo.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        String jwt = "JWT";

        SecurityScheme securityScheme = new SecurityScheme()
                .name(jwt)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);

        Components components = new Components().addSecuritySchemes(jwt, securityScheme);

        OpenAPI openAPI = new OpenAPI()
                .components(components)
                .info(apiInfo())
                .addSecurityItem(securityRequirement);

        openAPI.path("/api/user/login", new PathItem().post(getLoginOperation()));
        openAPI.path("/api/user/reissue", new PathItem().post(getReissueOperation()));

        return openAPI;
    }

    private Info apiInfo() {
        return new Info()
                .title("Convo Server API")
                .version("1.0.0");
    }

    private Operation getLoginOperation() {
        return new Operation()
                .tags(List.of("User"))
                .summary("로그인")
                .requestBody(new RequestBody()
                        .content(new Content()
                                .addMediaType("application/json", new MediaType()
                                        .schema(new Schema<>()
                                                .type("object")
                                                .properties(getLoginProperties())
                                        )
                                )
                        )
                )
                .responses(new ApiResponses()
                        .addApiResponse("200", new ApiResponse().description("Successful login"))
                        .addApiResponse("400", new ApiResponse().description("Bad request"))
                );
    }

    private Map<String, Schema<?>> getLoginProperties() {
        Map<String, Schema<?>> properties = new HashMap<>();
        properties.put("email", new Schema<>().type("string"));
        properties.put("password", new Schema<>().type("string"));
        return properties;
    }

    private Operation getReissueOperation() {
        return new Operation()
                .tags(List.of("User"))
                .summary("토큰 재발급")
                .description("Cookie에 저장되어 있는 RT 서버 측에서 자동 파싱")
                .addSecurityItem(new SecurityRequirement().addList("JWT")) // JWT 인증 필요
                .responses(new ApiResponses()
                        .addApiResponse("200", new ApiResponse().description("Token reissued successfully"))
                        .addApiResponse("401", new ApiResponse().description("Unauthorized - Invalid Token"))
                );
    }
}
