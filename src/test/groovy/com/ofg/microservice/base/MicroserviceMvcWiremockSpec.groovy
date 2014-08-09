package com.ofg.microservice.base
import com.ofg.infrastructure.base.MvcWiremockIntegrationSpec
import com.ofg.microservice.Profiles
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore

@ContextConfiguration(classes = [ServiceDiscoveryStubbingApplicationConfiguration], loader = SpringApplicationContextLoader)
@ActiveProfiles(Profiles.TEST)
@Ignore
class MicroserviceMvcWiremockSpec extends MvcWiremockIntegrationSpec {}
