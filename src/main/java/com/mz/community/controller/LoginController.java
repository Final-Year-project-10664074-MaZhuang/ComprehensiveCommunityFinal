package com.mz.community.controller;

import com.google.code.kaptcha.Producer;
import com.mz.community.entity.AccessToken;
import com.mz.community.entity.User;
import com.mz.community.service.UserService;
import com.mz.community.util.CommunityConstant;
import com.mz.community.util.CommunityUtil;
import com.mz.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController implements CommunityConstant {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.uri}")
    private String redirectUri;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;
    @Autowired
    private Producer kaptchaProducer;

    @RequestMapping(path = "/register",method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user){
        Map<String, Object> map = userService.register(user);
        if(map==null||map.isEmpty()){
            model.addAttribute("msg","Registration is successful, we have sent an activation email to your email");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }
    //http://localhost:8080/community/activation/101/code
    @RequestMapping(path = "/activation/{userId}/{code}",method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId,@PathVariable("code") String code){
        int result = userService.activation(userId, code);
        if(result==ACTIVATION_SUCCESS){
            model.addAttribute("msg","Activated successfully, your account is ready to use");
            model.addAttribute("target","/login");
        }else if (result==ACTIVATION_REPEAT){
            model.addAttribute("msg","Invalid operation, the account has been activated");
            model.addAttribute("target","/index");
        }else {
            model.addAttribute("msg","Activation failed, the verification code you provided is incorrect");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }

    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response) {
        // Generate verification code
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        // Store verification code into session

        //Verification code attribution
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        // Store verification code into redis
        String redisKey = RedisKeyUtil.getKaptchKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey, text, 60, TimeUnit.SECONDS);

        // Output burst image to browser
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            LOGGER.error("Response verification code failed:" + e.getMessage());
        }
    }

    @RequestMapping(path = "/login",method = RequestMethod.POST)
    public String login(Model model, String username, String password,String code,
                        boolean rememberme,HttpServletResponse response,
                        @CookieValue("kaptchaOwner") String kaptchaOwner){
        //check kaptcha
        String kaptcha = null;
        if(StringUtils.isNotBlank(kaptchaOwner)){
            String redisKey =RedisKeyUtil.getKaptchKey(kaptchaOwner);
            kaptcha= (String) redisTemplate.opsForValue().get(redisKey);
        }
        if(StringUtils.isBlank(kaptcha)||StringUtils.isBlank(code)||!kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","Incorrect verification code");
            return "/site/login";
        }
        //check username and password
        int expiredSeconds = rememberme?REMEMBER_EXPIRED_SECONDS:DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if(map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        }else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    @RequestMapping(path = "/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/login";
    }
    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           HttpServletResponse response,Model model) {
        AccessToken accessToken = new AccessToken();
        accessToken.setClient_id(clientId);
        accessToken.setClient_secret(clientSecret);
        accessToken.setCode(code);
        accessToken.setRedirect_uri(redirectUri);
        accessToken.setState(state);
        String access_Token = userService.getAccessToken(accessToken);
        if (access_Token!=null){
            Map<String, Object> map = userService.getUser(access_Token);
            if (map != null) {
                if (map.containsKey("emailMsg")) {
                    return "/site/BindMail";
                } else if (map.containsKey("ticket")) {
                    Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                    cookie.setPath(contextPath);
                    cookie.setMaxAge(3600 * 24 * 100 * 1000);
                    response.addCookie(cookie);
                    return "redirect:/index";
                }else if(map.containsKey("usernameMsg")){
                    model.addAttribute("usernameMsg", map.get("usernameMsg"));
                    return "/site/login";
                }
            }
            model.addAttribute("usernameMsg", "Github user login failed");
            return "/site/login";
        }
        model.addAttribute("usernameMsg", "Github user login failed");
        return "/site/login";
    }

    @RequestMapping(path = "/bind", method = RequestMethod.POST)
    public String bind(Model model, String email) {
        String mail = userService.bind(email);
        if (mail != null || !mail.isEmpty()) {
            model.addAttribute("msg", "Bind successful, we have sent an activation email to your email, please activate as soon as possible!");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else if (mail == "1") {
            model.addAttribute("emailMsg", email + ": The mailbox is already bound");
            return "/site/BindMail";
        } else {
            model.addAttribute("emailMsg", email);
            return "/site/BindMail";
        }
    }
}
