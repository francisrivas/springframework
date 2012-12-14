/**
 * 
 */
package org.springframework.test.web.servlet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Tests for SPR-10093 (support for OPTIONS requests).
 * @author Arnaud Cogoluègnes
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration
public class Spr10093Tests {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).dispatchOptions(true).build();
    }

    @Test
    public void test() throws Exception {
        MyController controller = wac.getBean(MyController.class);
        int initialCount = controller.counter.get();
        this.mockMvc.perform(options("/myUrl")).andExpect(status().isOk());
        Assert.assertEquals(initialCount+1,controller.counter.get());
    }


    @Configuration
    @EnableWebMvc
    static class WebConfig extends WebMvcConfigurerAdapter {

        @Bean
        public MyController myController() {
            return new MyController();
        }
    }

    @Controller
    private static class MyController {
        
        private AtomicInteger counter = new AtomicInteger(0);

        @RequestMapping(value="/myUrl",method=RequestMethod.OPTIONS)
        @ResponseBody
        public void handle() {
            counter.incrementAndGet();
        }
    }

    
}
