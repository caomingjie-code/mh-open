package com.mh.base.eslog.event;

import com.mh.base.eslog.appender.RabbitMQAppender;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @Description:
 * @Auther: create by cmj on 2020/8/18 10:09
 */
public class ContextRefreshedEventChild extends ContextRefreshedEvent {
    /**
     * Create a new ContextRefreshedEvent.
     *
     * @param source the {@code ApplicationContext} that has been initialized
     *               or refreshed (must not be {@code null})
     */
    public ContextRefreshedEventChild(ApplicationContext source) {
        super(source);
    }

    public void startMq(){
        RabbitMQAppender bean = this.getApplicationContext().getBean(RabbitMQAppender.class);
       // bean.startMq();
    }


}
