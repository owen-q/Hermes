package org.owen.hermes.util

import spock.lang.Specification

/**
 * Created by owen_q on 2018. 6. 22..
 */
class CheckUtilTest extends Specification {
    def "test checkCollectionNotEmpty() with notEmptyCollection"(){
        given:
        List<Integer> givenEmptyCollection = new ArrayList<>()
        givenEmptyCollection.add(1);

        when:
        CheckUtil.checkCollectionNotEmpty(givenEmptyCollection, "givenEmptyCollection")

        then:
        notThrown(IllegalArgumentException)
    }

    def "test checkCollectionNotEmpty() with emptyCollection"(){
        given:
        List<Integer> givenEmptyCollection = new ArrayList<>()

        when:
        CheckUtil.checkCollectionNotEmpty(givenEmptyCollection, "givenEmptyCollection")

        then:
        def e = thrown(IllegalArgumentException)
    }



}
