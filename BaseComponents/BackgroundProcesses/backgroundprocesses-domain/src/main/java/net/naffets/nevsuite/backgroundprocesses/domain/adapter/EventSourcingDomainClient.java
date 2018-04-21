package net.naffets.nevsuite.backgroundprocesses.domain.adapter;

import net.naffets.nevsuite.eventsourcing.domain.dto.EventDescriptorDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @author br4sk4 / created on 21.04.2018
 */
@Component
@FeignClient("eventsourcing")
public interface EventSourcingDomainClient {

    @RequestMapping(method = RequestMethod.GET, value = "/DomainService/eventdescriptors")
    List<EventDescriptorDTO> getEventDescriptors();

}
