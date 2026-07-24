package org.bank.portfolioservice.consumer;

import org.bank.sharedevents.event.trade.TradeExecutedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;



@Component
public class TradeDeadLetterConsumer {


    @KafkaListener(
            topics = "trade-executed.DLT",
            groupId = "portfolio-dlt-group"
    )
    public void consume(
            TradeExecutedEvent event
    ){


        System.err.println(
                "DLT EVENT RECEIVED : "
                        + event.tradeId()
        );


        /*
          Ici plus tard :

          - envoyer notification
          - créer alerte
          - sauvegarder incident
        */

    }

}