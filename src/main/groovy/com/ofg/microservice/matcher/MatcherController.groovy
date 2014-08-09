package com.ofg.microservice.matcher
import com.ofg.infrastructure.discovery.ServiceResolver
import com.ofg.infrastructure.web.exception.BadParametersException
import com.ofg.infrastructure.web.resttemplate.RestTemplate
import groovy.transform.Canonical
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@TypeChecked
@RestController
@Slf4j
//@Api(value = "quotes", description = "Async quotes call to test correlation id")
class MatcherController {

    private RestTemplate restTemplate
    private ServiceResolver serviceResolver
    private Map<String, Closure> dependencies = [
            "twitterCollector" : {UserLink it ->it.twitter},
            "githubCollector" : {UserLink it -> it.github},
            "googlePlusCollector" : {UserLink it -> it.googleplus},
            "blogCollector" : {UserLink it -> URLEncoder.encode(it.rss, "UTF-8")}
    ]

    @Autowired
    MatcherController(ServiceResolver serviceResolver, RestTemplate restTemplate) {
        this.serviceResolver = serviceResolver
        this.restTemplate = restTemplate
    }

    @RequestMapping(value = "/match", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    //@ApiOperation(value = "Async call with CorrelationID", notes = "This will asynchronously call the server, proving that correlation id works in that case")
    String callReturnigCorrelationIdAndUsingItInAsync(@RequestBody InputData inputData, BindingResult result) {
        checkIfResultHasErrors(result)

        dependencies.each { Map.Entry<String, Closure> entry ->
            com.google.common.base.Optional<String> optionalUrl = serviceResolver.getUrl(entry.key)
            if(optionalUrl.isPresent()) {
                String url = optionalUrl.get()
                restTemplate.put("$url/{login}/{pairId}", new HttpEntity<Object>(), entry.value.call(inputData.peasant), inputData.pairId)
                restTemplate.put("$url/{login}/{pairId}", new HttpEntity<Object>(), entry.value.call(inputData.celebrity), inputData.pairId)
            }
        }
//        String twitterCollectorUrl = serviceResolver.getUrl('twitterCollector').get()
//        restTemplate.put("$twitterCollectorUrl/{login}/{pairId}", new HttpEntity<Object>(), inputData.peasant.twitter , inputData.pairId )
//        restTemplate.put("$twitterCollectorUrl/{login}/{pairId}", new HttpEntity<Object>(), inputData.celebrity.twitter , inputData.pairId )
//
//        String cachingGithubCollectorUrl = serviceResolver.getUrl('githubCollector').get()
//        restTemplate.put("$cachingGithubCollectorUrl/{login}/{pairId}", new HttpEntity<Object>(), inputData.peasant.github , inputData.pairId )
//        restTemplate.put("$cachingGithubCollectorUrl/{login}/{pairId}", new HttpEntity<Object>(), inputData.celebrity.github , inputData.pairId )

        return "dupa"
    }


private void checkIfResultHasErrors(BindingResult result) {
        if(result.hasErrors()){
            throw new BadParametersException(result.getAllErrors())
        }
    }

}

@Canonical
@TypeChecked
class InputData {
    UserLink celebrity
    UserLink peasant
    Long pairId
}

@Canonical
@TypeChecked
class UserLink {
    String id
    String facebook
    String twitter
    String googleplus
    String name
    String rss
    String github
}