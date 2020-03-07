package com.mz.finalcommunity.finalcommunity.controller;

import com.mz.finalcommunity.finalcommunity.annotation.LoginRequired;
import com.mz.finalcommunity.finalcommunity.entity.DiscussPost;
import com.mz.finalcommunity.finalcommunity.entity.Page;
import com.mz.finalcommunity.finalcommunity.entity.User;
import com.mz.finalcommunity.finalcommunity.service.DiscussPostService;
import com.mz.finalcommunity.finalcommunity.service.FollowService;
import com.mz.finalcommunity.finalcommunity.service.LikeService;
import com.mz.finalcommunity.finalcommunity.service.UserService;
import com.mz.finalcommunity.finalcommunity.util.CommunityConstant;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {
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

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Autowired
    private DiscussPostService discussPostService;

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

    //profile
    @RequestMapping(path = "profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId,Model model){
        User user = userService.findUserById(userId);
        if(user==null){
            throw new RuntimeException("This user does not exist");
        }

        //user information
        model.addAttribute("user",user);

        //likes count
        int LikeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",LikeCount);

        //follow count
        long followeeCount = followService.findFolloweeCount(userId,ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);
        //fans count
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER,userId);
        model.addAttribute("followerCount",followerCount);
        //whether follow
        boolean hasFollowed =false;
        if(hostHolder.getUserThreadLocal()!=null){
            hasFollowed = followService.hasFollowed(hostHolder.getUserThreadLocal().getId(),ENTITY_TYPE_USER,userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);

        return "/site/profile";
    }

    @RequestMapping(path = "/myPost/{userId}",method = RequestMethod.GET)
    public String getMyPostPage(@PathVariable("userId") int userId,Page page, @RequestParam(name = "orderMode", defaultValue = "1") int orderMode,
                                Model model){

        User user = userService.findUserById(userId);
        if (user != null) {
            model.addAttribute("user",user);

        }else {
            throw new RuntimeException("This user does not exist");
        }

        page.setRow(discussPostService.findDiscussPostRows(userId));
        page.setPath("/user/myPost/"+userId+"?orderMode="+orderMode);
        model.addAttribute("row",page.getRow());

        List<DiscussPost> myPostList = discussPostService.findDiscussPosts(userId, page.getOffset(), page.getLimit(), 1);
        List<Map<String, Object>> myPosts = new ArrayList<>();
        if (myPostList != null) {
            for (DiscussPost post : myPostList) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);


                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);

                myPosts.add(map);
            }
        }
        model.addAttribute("myPosts", myPosts);
        model.addAttribute("orderMode",orderMode);
        return "/site/my-post";
    }
}
