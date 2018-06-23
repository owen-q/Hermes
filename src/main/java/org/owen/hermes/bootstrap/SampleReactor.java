package org.owen.hermes.bootstrap;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

/**
 * Created by owen_q on 2018. 6. 23..
 */
public class SampleReactor {
    private Logger logger = LoggerFactory.getLogger(SampleReactor.class);


    @Test
    public void testReactor(){

        Flux<?> testFlux = Flux.just(null);
        testFlux.subscribe(msg->{
            System.out.println(msg);
        });

    }
}
