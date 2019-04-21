package com.example.ugasoft.config;


import com.example.ugasoft.service.Receiver;
import com.example.ugasoft.service.ScheduledRunner;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class MainConfig {

    public static final String topicExchangeName = "spring-boot-exchange";

    static final String queueName = "spring-boot";

    private ApplicationContext context;
    private ConnectionFactory connectionFactory;
    private ScheduledRunner scheduledRunner;

    @Value("${receivers.qty}")
    private int receiversQty;

    public MainConfig(ApplicationContext context,
                      ConnectionFactory connectionFactory,
                      ScheduledRunner scheduledRunner) {
        this.context = context;
        this.connectionFactory = connectionFactory;
        this.scheduledRunner = scheduledRunner;
    }

    @Bean
    Queue queue() {
        return new Queue(queueName, false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(topicExchangeName);
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("foo.bar.#");
    }

    @PostConstruct
    public void createContainers() {
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) context.getAutowireCapableBeanFactory();
        for (int i = 1; i <= receiversQty; i++) {
            MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(new Receiver(scheduledRunner), "listenForRabbit");
            registry.registerBeanDefinition("listener" + i,
                    BeanDefinitionBuilder.genericBeanDefinition(SimpleMessageListenerContainer.class)
                            .addPropertyValue("connectionFactory", connectionFactory)
                            .addPropertyValue("messageListener", listenerAdapter)
                            .getBeanDefinition());
            SimpleMessageListenerContainer listener = (SimpleMessageListenerContainer) context.getBean("listener" + i);
            listener.setQueueNames(queueName);
        }
    }
}
