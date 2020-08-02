package jp.kobe_u.cs27.app.meetingroomreservation.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

import jp.kobe_u.cs27.app.meetingroomreservation.domain.service.UserService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void configure(WebSecurity web) throws Exception {
        /* 認証なしでアクセスできるファイル */
        web.ignoring().antMatchers("/index.html", "/img/**", "/css/**", "/js/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
        .antMatchers("/login").permitAll()//ログインフォームは許可
        .anyRequest().authenticated();// それ以外は全て認証無しの場合アクセス不許可
        
        //ログインの設定
        http.formLogin()
            .loginPage("/login") // ログインページはコントローラを経由しないのでViewNameとの紐付けが必要
            .loginProcessingUrl("/authenticate") // フォームのSubmitURL、このURLへリクエストが送られると認証処理が実行される
            .defaultSuccessUrl("/reservations")
            .failureUrl("/login?error")
            .usernameParameter("uid") // リクエストパラメータのname属性を明示
            .passwordParameter("password");

        //ログアウトの設定
        http.logout()
            .logoutUrl("/logout") //ログアウトのURL
            .logoutSuccessUrl("/login?logout") //ログアウト完了したらこのページへ
            .deleteCookies("JSESSIONID")
            .invalidateHttpSession(true)
            .permitAll();

    }

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
        userService.registerAdmin("admin", "xxadmin8", "admin@localhost");
    }
    /** テスト用のインメモリアカウント */
    /*
     * public void configure(AuthenticationManagerBuilder auth) throws Exception{
     * auth .inMemoryAuthentication()
     * .withUser("user").password("{noop}password").roles("USER"); }
     */
}