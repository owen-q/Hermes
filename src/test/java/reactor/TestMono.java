package reactor;

import org.junit.Test;
import reactor.core.publisher.Mono;

/**
 * Created by dongqlee on 2018. 5. 17..
 */
public class TestMono {
    @Test
    public void testMono(){

        int data=100;
        Mono<?> tmp=Mono.just(data)
                .map((num)->{
                    System.out.println("First map");
                    int a=10/0;
                    return Mono.error(new Throwable("asd"));
                })
                .doOnError((e)->{
                    System.out.println("do On error");
                    System.out.println(e.getMessage());
                })
                .onErrorMap((e1)->{
                    System.out.println("First onErrorMap");
                    System.out.println(e1.getMessage());

                    return e1;
                })
                .onErrorMap((e2)->{
                    System.out.println("Second onErrorMap");
                    System.out.println(e2.getMessage());

                    return e2;
                })
                .map((what)->{
                    System.out.println("what?");
                    System.out.println(what);

                    return 1;
                });


        tmp.subscribe();

    }
}
