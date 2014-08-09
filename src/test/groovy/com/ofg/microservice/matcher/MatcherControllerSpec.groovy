package com.ofg.microservice.matcher

import com.ofg.microservice.base.MicroserviceMvcWiremockSpec
import org.springframework.http.MediaType

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.ofg.infrastructure.base.dsl.WireMockHttpRequestMapper.wireMockPut
import static org.springframework.http.HttpStatus.OK
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class MatcherControllerSpec extends MicroserviceMvcWiremockSpec {

    String correctMatchJson = """
        {
            "celebrity" : {
              "id": "tnurkiewicz",
              "facebook": "tnurkiewicz",
              "twitter": "tnurkiewicz",
              "googleplus": "tnurkiewicz",
              "name": "tnurkiewicz",
              "github": "tnurkiewicz",
              "rss": "tnurkiewicz.pl"
            },
            "peasant" : {
              "id": "erebtowski",
              "facebook": "erebtowski",
              "twitter": "erebtowski",
              "googleplus": "erebtowski",
              "name": "erebtowski",
              "github": "erebtowski",
              "rss": "erbetowski.pl"
            },
            "pairId" : 1
        }
        """

    def "should send pairId and userId to collectors"() {
        given:

            List<String> services = ["/twitterCollector/erebtowski/1",
                                     "/twitterCollector/tnurkiewicz/1",
                                     "/githubCollector/erebtowski/1",
                                     "/githubCollector/tnurkiewicz/1",
                                     "/googlePlusCollector/erebtowski/1",
                                     "/googlePlusCollector/tnurkiewicz/1"
            ]
            collectorsRespondsOk(services)

            //mockMvc.configureFor("localhost", 8888)
        when:

            mockMvc.perform(post('/match').contentType(MediaType.APPLICATION_JSON).content(correctMatchJson))
                    .andExpect(status().isOk())
        then:
            services.each {
                wireMock.verifyThat(putRequestedFor(urlEqualTo(it)))
            }

//            ValueMatchingStrategy matchingStrategy = new ValueMatchingStrategy()
//            matchingStrategy.setContains('{"pairId": "1","rssUrl": "http://tomaszdziurko.pl/feed"}')
//            matchingStrategy.setJsonCompareMode(JSONCompareMode.LENIENT)
            //wireMock.verifyThat(putRequestedFor(urlEqualTo('/blogCollector/')))//.withRequestBody(matchingStrategy))

    }

    def collectorsRespondsOk(List<String> services) {
        services.each {
            stubInteraction(wireMockPut(it), aResponse().withStatus(OK.value()))
        }
        stubInteraction(wireMockPut('/blogCollector'), aResponse().withStatus(OK.value()))

    }
}
