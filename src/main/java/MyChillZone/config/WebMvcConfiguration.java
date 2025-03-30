package MyChillZone.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableMethodSecurity
public class WebMvcConfiguration implements WebMvcConfigurer {

    //SecurityFilterChain- начин ,по който Sring Security разбира как да се прилага за нашето приложение
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        //authorizeHttpRequests - за група ендпойнти
        //requestMatchers - достъп до даден ендпойнт
        //permitAll - всеки има достъп до този ендпойнт
        //anyRequest - всички заявки които не съм изброил
        //authenticated() - за да имаш достъп трябва да си влязъл в твоя акаунт
        http
                .authorizeHttpRequests(matchers -> matchers
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/users").hasRole("ADMIN")
                        .requestMatchers("/","/register").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
//                        .usernameParameter("username")
//                        .passwordParameter("password")
                        .defaultSuccessUrl("/home", true)
                        .failureUrl("/login?error=true")
                        .permitAll())
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                        .logoutSuccessUrl("/")
                );

        return http.build();
    }

}
