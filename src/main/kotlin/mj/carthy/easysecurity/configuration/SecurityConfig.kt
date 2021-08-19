package mj.carthy.easysecurity.configuration

import com.google.gson.Gson
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration class SecurityConfig {
  /* BEANS */
  @Bean fun gson(): Gson = Gson()
}