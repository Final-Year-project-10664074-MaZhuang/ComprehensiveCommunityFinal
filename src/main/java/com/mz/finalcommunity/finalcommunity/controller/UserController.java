package com.mz.finalcommunity.finalcommunity.controller;

import com.mz.finalcommunity.finalcommunity.annotation.LoginRequired;
import com.mz.finalcommunity.finalcommunity.entity.User;
import com.mz.finalcommunity.finalcommunity.service.UserService;
import com.mz.finalcommunity.finalcommunity.util.CommunityUtil;
import com.mz.finalcommunity.finalcommunity.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
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
    public String uoloadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "No picture selected");
            return "/site/setting";
        }
        String fileName = headerImage.getOriginalFilename();
        String substring = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(substring)) {
            model.addAttribute("error", "File format is incorrect");
            return "/site/setting";
        }
        //Generate random file name
        fileName = CommunityUtil.generateUUID() + substring;
        //Determine the file storage path
        File dest = new File(uploadPath + "/" + fileName);
        try {
            //save file
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("File upload failed: " + e.getMessage());
            throw new RuntimeException("File upload failed", e);
        }
        //update user's headerUrl
        User user = hostHolder.getUserThreadLocal();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);
        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        //Server storage path
        fileName = uploadPath + "/" + fileName;
        //File extension
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //Response picture
        response.setContentType("image/" + suffix);
        try (FileInputStream fis = new FileInputStream(fileName);
             ServletOutputStream outputStream = response.getOutputStream();){
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                outputStream.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("Failed to read avatar", e.getMessage());
        }
    }
}
