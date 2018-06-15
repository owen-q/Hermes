package org.owen.hermes.util.lambda;

import java.util.function.Consumer;

/**
 * Created by owen_q on 2018. 6. 15..
 */
public interface PrintConsumer extends Consumer{
    PrintConsumer INSTANCE = output -> System.out.println(output);
}
