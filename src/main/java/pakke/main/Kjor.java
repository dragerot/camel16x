package pakke.main;



import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;

import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.ConnectionFactory;

public class Kjor {
    final Logger logger = LoggerFactory.getLogger(Kjor.class);


    public static void main(String[] arg){
    Kjor kjor= new Kjor();
//        kjor.run();
       kjor.demo();
   }

    public void run()  {
        CamelContext camelContext = new DefaultCamelContext();
        logger.info("Starter...2");

        try{
        ProducerTemplate template = camelContext.createProducerTemplate();
//        template.setDefaultEndpointUri("timer://tickTock?period=1000");
//        template.sendBody("HALLO");


        camelContext.addRoutes(new RouteBuilder() {
            public void configure() {
                from("timer://tickTock?period=1000").
                //from("direct:start").to("file://java/toregard.txt")
                //from("test-jms:queue:test.queue").to("file://java/toregard.txt")
                to("log:out", "mock:test");
            }
        });
        camelContext.start();
//            for (int i = 0; i < 10; i++) {
//                template.sendBody("test-jms:queue:test.queue", "Test Message: " + i);
//            }
        }catch(Exception e){
            logger.info("Fail..."+e.getMessage());
        }
        logger.info("End...");

    }

    public void demo()  {
        try{
        // START SNIPPET: e1
        CamelContext context = new DefaultCamelContext();
        // END SNIPPET: e1
        // Set up the ActiveMQ JMS Components
        // START SNIPPET: e2
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        // Note we can explicit name the component
        context.addComponent("test-jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        // END SNIPPET: e2
        // Add some configuration by hand ...
        // START SNIPPET: e3
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("test-jms:queue:test.queue").to("file:c://temp/camel/out");
            }
        });
        // END SNIPPET: e3
        // Camel template - a handy class for kicking off exchanges
        // START SNIPPET: e4
        ProducerTemplate template = context.createProducerTemplate();
        // END SNIPPET: e4
        // Now everything is set up - lets start the context
        context.start();
        // Now send some test text to a component - for this case a JMS Queue
        // The text get converted to JMS messages - and sent to the Queue
        // test.queue
        // The file component is listening for messages from the Queue
        // test.queue, consumes
        // them and stores them to disk. The content of each file will be the
        // test we sent here.
        // The listener on the file component gets notified when new files are
        // found ... that's it!
        // START SNIPPET: e5
        for (int i = 0; i < 10; i++) {
            template.sendBody("test-jms:queue:test.queue", "Test Message: " + i);
        }
        // END SNIPPET: e5

        // wait a bit and then stop
        Thread.sleep(1000);
        context.stop();
    }catch(Exception e){
        logger.info("Fail..."+e.getMessage());
    }
    logger.info("End...");
    }

}
