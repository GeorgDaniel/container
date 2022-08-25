package org.opentosca.container.api.controller;

import java.net.URI;
import java.util.Collection;
import java.util.Set;

import javax.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.opentosca.container.api.dto.situations.SituationDTO;
import org.opentosca.container.api.dto.situations.SituationListDTO;
import org.opentosca.container.api.dto.situations.SituationTriggerDTO;
import org.opentosca.container.api.dto.situations.SituationTriggerInstanceDTO;
import org.opentosca.container.api.dto.situations.SituationTriggerListDTO;
import org.opentosca.container.core.common.uri.UriUtil;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.Situation;
import org.opentosca.container.core.next.model.SituationTrigger;
import org.opentosca.container.core.next.model.SituationTriggerProperty;
import org.opentosca.container.core.next.services.instances.NodeTemplateInstanceService;
import org.opentosca.container.core.next.services.instances.ServiceTemplateInstanceService;
import org.opentosca.container.core.next.services.instances.SituationInstanceService;
import org.opentosca.container.core.service.CsarStorageService;
import org.springframework.stereotype.Component;

@Path("/situationsapi")
@Component
public class SituationsController {

    @Context
    UriInfo uriInfo;

    @Inject
    private NodeTemplateInstanceService nodeTemplateInstanceService;
    @Inject
    private ServiceTemplateInstanceService serviceTemplateInstanceService;
    @Inject
    private SituationInstanceService situationInstanceService;
    @Inject
    private CsarStorageService csarService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getRoot() {
        return Response.ok("Situations").build();
    }

    @GET
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/situations")
    public Response getSituations() {
        final SituationListDTO dto = new SituationListDTO();
        this.situationInstanceService.getSituations().forEach(x -> dto.add(SituationDTO.Converter.convert(x)));
        return Response.ok(dto).build();
    }

    @PUT
    @Consumes( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/situations/{situation}")
    public Response updateSituation(@PathParam("situation") final Long situationId, final SituationDTO situation) {
        final Situation sit = this.situationInstanceService.getSituation(situation.getId());

        sit.setActive(situation.getActive());
        sit.setEventProbability(situation.getEventProbability());
        sit.setEventTime(situation.getEventTime());

        this.situationInstanceService.updateSituation(sit);

        final URI instanceURI = this.uriInfo.getAbsolutePath();

        return Response.ok(instanceURI).build();
    }

    @PUT
    @Consumes( {MediaType.TEXT_PLAIN})
    @Path("/situations/{situation}/active")
    public Response updateSituationActivity(@PathParam("situation") final Long situationId, final String body) {
        final Situation sit = this.situationInstanceService.getSituation(situationId);

        boolean active;
        if (body.equalsIgnoreCase("true") || body.equalsIgnoreCase("false")) {
            active = Boolean.valueOf(body);
        } else {
            return Response.notAcceptable(null).build();
        }

        sit.setActive(active);

        this.situationInstanceService.updateSituation(sit);

        final URI instanceURI = this.uriInfo.getAbsolutePath();

        return Response.ok(instanceURI).build();
    }

    @POST
    @Consumes( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/situations")
    public Response createSituation(final SituationDTO situation) {
        final Situation sit = this.situationInstanceService.createNewSituation(situation.getThingId(),
            situation.getSituationTemplateId(), situation.getActive(), situation.getEventProbability(),
            situation.getEventTime());

        final URI instanceURI = UriUtil.generateSubResourceURI(this.uriInfo, sit.getId().toString(), false);

        return Response.ok(instanceURI).build();
    }

    @GET
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/situations/{situation}")
    public Response getSituation(@PathParam("situation") final Long situationId) {
        return Response.ok(SituationDTO.Converter.convert(this.situationInstanceService.getSituation(situationId))).build();
    }

    @DELETE
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/situations/{situation}")
    public Response deleteSituation(@PathParam("situation") final Long situationId) {
        this.situationInstanceService.removeSituation(situationId);
        return Response.ok().build();
    }

    @GET
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/triggers")
    public Response getSituationTriggers() {
        final SituationTriggerListDTO dto;
        try {
            dto = new SituationTriggerListDTO();
            this.situationInstanceService.getSituationTriggers().forEach(x -> dto.add(SituationTriggerDTO.Converter.convert(x)));
        } catch (final Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
        return Response.ok(dto).build();
    }

    @POST
    @Path("/triggers")
    @Consumes( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createSituationTrigger(final SituationTriggerDTO situationTrigger) {
        final Collection<Situation> sits = Lists.newArrayList();

        for (final Long situationId : situationTrigger.getSituationIds()) {
            final Situation situation = this.situationInstanceService.getSituation(situationId);
            sits.add(situation);
        }

        ServiceTemplateInstance serviceInstance = null;
        if (situationTrigger.getServiceInstanceId() != null) {
            try {
                serviceInstance = this.serviceTemplateInstanceService.getServiceTemplateInstance(situationTrigger.getServiceInstanceId(), false);
            } catch (final NotFoundException e) {
                serviceInstance = null;
            }
        }

        NodeTemplateInstance nodeInstance = null;
        if (situationTrigger.getNodeInstanceId() != null) {
            nodeInstance = this.nodeTemplateInstanceService.getNodeTemplateInstance(situationTrigger.getNodeInstanceId());
        }

        final Set<SituationTriggerProperty> inputs = Sets.newHashSet();

        float eventProbability = -1.0f;
        if (Float.compare(situationTrigger.getEventProbability(), eventProbability) != 0) {
            eventProbability = situationTrigger.getEventProbability();
        }

        String eventTime = null;
        if (situationTrigger.getEventTime() != null) {
            eventTime = situationTrigger.getEventTime();
        }

        situationTrigger.getInputParams()
            .forEach(x -> inputs.add(new SituationTriggerProperty(x.getName(), x.getValue(), x.getType())));

        SituationTrigger sitTrig = new SituationTrigger(sits, csarService.findById(new CsarId(situationTrigger.getCsarId())).id(),
            situationTrigger.isOnActivation(), situationTrigger.isSingleInstance(), situationTrigger.getInterfaceName(), situationTrigger.getOperationName());
        sitTrig = this.situationInstanceService.createNewSituationTrigger(sitTrig, serviceInstance, nodeInstance, inputs, eventProbability, eventTime);

        final URI instanceURI = UriUtil.generateSubResourceURI(this.uriInfo, sitTrig.getId().toString(), false);
        return Response.ok(instanceURI).build();
    }

    @DELETE
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/triggers/{situationtrigger}")
    public Response deleteSituationTrigger(@PathParam("situationtrigger") final Long situationTriggerId) {

        this.situationInstanceService.removeSituationTrigger(situationTriggerId);
        return Response
            .ok()
            .build();
    }

    @GET
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/triggers/{situationtrigger}")
    public Response getSituationTrigger(@PathParam("situationtrigger") final Long situationTriggerId) {
        return Response
            .ok(SituationTriggerDTO.Converter.convert(this.situationInstanceService.getSituationTrigger(situationTriggerId)))
            .build();
    }

    @GET
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/triggers/{situationtrigger}/{situationtriggerinstance}")
    public Response getSituationTriggerInstance(@PathParam("situationtrigger") final Long situationTriggerId,
                                                @PathParam("situationtriggerinstance") final Long situationTriggerInstanceId) {
        return Response
            .ok(SituationTriggerInstanceDTO.Converter.convert(this.situationInstanceService.getSituationTriggerInstance(situationTriggerInstanceId)))
            .build();
    }
}
