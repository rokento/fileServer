package com.lxd.web.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class helloController {

    @RequestMapping("/index")
    public ModelAndView hello(){
        ModelAndView mv =  new ModelAndView("index");
        return mv;
    }
}