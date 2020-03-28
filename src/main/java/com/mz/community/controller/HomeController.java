package com.mz.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {
    @RequestMapping(path = "/",method = RequestMethod.GET)
    public String root() {
        return "forward:/index";
    }
    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String index() {
        return "index";
    }
    @RequestMapping(path = "/error",method = RequestMethod.GET)
    public String getErrorPage(){
        return "/error/500";
    }
    @RequestMapping(path = "/denied", method = RequestMethod.GET)
    public String getDeniedPage() {
        return "/error/404";
    }
}
