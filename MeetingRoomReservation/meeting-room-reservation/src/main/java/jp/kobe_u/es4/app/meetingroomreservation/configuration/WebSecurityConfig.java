package jp.kobe_u.es4.app.meetingroomreservation.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import jp.kobe_u.es4.app.meetingroomreservation.domain.service.UserService;

@Configuration
@EnableWebSecurity // (1) Spring Securityを使うための設定
public class WebSecurityConfig {
    @Autowired
    private UserService userService; // 予約アプリのユーザサービス
    @Autowired
    private PasswordEncoder passwordEncoder; // アプリ共通のパスワードエンコーダ
    @Autowired
    private AdminConfigration adminConfig; // 管理者の設定

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.formLogin(
                // (1) 【ログインの処理】
                login -> login
                        .loginPage("/login") // ログインページURL
                        .usernameParameter("uid") // ユーザIDのパラメータ
                        .passwordParameter("password") // パスワードのパラメータ
                        .loginProcessingUrl("/authenticate") // 認証時のPOST先
                        .defaultSuccessUrl("/reservations", true) // ランディングページ
                        .failureUrl("/login?error") // 失敗ページ
                        .permitAll())
                // (2) 【ログアウトの処理】
                .logout(
                        logout -> logout
                                .logoutUrl("/logout") // ログアウトページURL
                                .logoutSuccessUrl("/login?logout") // ログアウト成功
                                .deleteCookies("JSESSIONID") // クッキー消去
                                .invalidateHttpSession(true) // セッション無効化
                                .permitAll() // ログアウトはいつでもアクセスできる
                )
                // (3) 【認可の設定】URLごとに異なるセキュリティ設定を行う
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/img/**").permitAll()
                        .requestMatchers("/css/**").permitAll()
                        .requestMatchers("/js/**").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/users/**").hasRole("ADMIN") // ユーザ管理は管理者のみ許可
                        .requestMatchers("/rooms/**").hasRole("ADMIN") // 会議室管理は管理者のみ許可
                        .anyRequest().authenticated());
        return http.build();
    }
    
    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        // (4) 認証方法の実装の設定を行う
        auth.userDetailsService(userService) // 認証は予約アプリのユーザサービスを使う
                .passwordEncoder(passwordEncoder); // パスワードはアプリ共通のものを使う
        // ついでにここで管理者ユーザを登録しておく
        userService.registerAdmin(adminConfig.getUsername(), adminConfig.getPassword());
    }
}