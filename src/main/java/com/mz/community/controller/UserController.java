package com.mz.community.controller;

import com.mz.community.Service.UserService;
import com.mz.community.annotation.LoginRequired;
import com.mz.community.entity.User;
import com.mz.community.util.CommunityUtil;
import com.mz.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;
    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }
    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "Please select a picture");
            return "/site/setting";
        }

        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "File format is incorrect");
            return "/site/setting";
        }

        //create random string
        filename = CommunityUtil.generateUUID() + suffix;
        //ensure store file path
        File dest = new File(uploadPath + "/" + filename);
        try {
            //store file
            headerImage.transferTo(dest);
        } catch (IOException e) {
            LOGGER.error("File upload failed", e.getMessage());
            throw new RuntimeException("File upload failed,server error", e);
        }
        //update current user header Url
        //http://localhost:8887/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + filename;
        userService.updateHeader(user.getId(),headerUrl);
        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{fileName}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        //server store path
        fileName = uploadPath+"/"+fileName;
        //File extension
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //Response picture
        response.setContentType("image/" + suffix);
        try (FileInputStream fis = new FileInputStream(fileName);
             ServletOutputStream outputStream = response.getOutputStream();) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                outputStream.write(buffer, 0, b);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to read avatar", e.getMessage());
        }
    }
    @LoginRequired
    @RequestMapping(path = "/changePass",method = RequestMethod.POST)
    public String changePassword(@CookieValue("ticket") String ticket,Model model, String oldPassword, String newPassword,String cPassword){
        if (StringUtils.isBlank(oldPassword)||StringUtils.isBlank(newPassword)||StringUtils.isBlank(cPassword)){
            model.addAttribute("opassError","password can not be blank");
            return "/site/setting";
        }
        if(!newPassword.equals(cPassword)){
            model.addAttribute("cpassError","The passwords entered twice do not match!");
            return "/site/setting";
        }
        User user = hostHolder.getUser();
        oldPassword = CommunityUtil.md5(oldPassword+user.getSalt());
        if(!oldPassword.equals(user.getPassword())){
            model.addAttribute("opassError","The original password is wrong!!!!!");
            return "/site/setting";
        }
        newPassword = CommunityUtil.md5(newPassword+user.getSalt());
        if(oldPassword.equals(newPassword)){
            model.addAttribute("npassError","The new password cannot be the same as the original password!!!");
            return "/site/setting";
        }
        userService.updatePassword(user.getId(),newPassword);
        userService.logout(ticket);
        return "redirect:/login";
    }
}
