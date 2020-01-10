package com.jige;

import com.jige.service.ReadDateService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = "com")
@EnableAsync
public class JigeApplication {
    @Autowired
    ReadDateService readDateService;

    //启动以后自动关闭 不需要修改
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(JigeApplication.class, args);
//        SpringApplication.exit(ctx, () -> 0);
    }

    /**
     * 启动后添加程序步骤
     * 具体程序代码放这个里面
     * 注意: 多线程执行的程序 主线程一定要等待全部子线程完成
     */
    @EventListener
    public void startup(ContextRefreshedEvent event) {
        LoggerFactory.getLogger(getClass()).info("启动...");
        readDateService.waitForData();
    }

    /**
     * 结束的时候添加程序步骤
     */
    @EventListener
    public void startup(ContextClosedEvent event) {
        LoggerFactory.getLogger(getClass()).info("结束...");
    }

}
