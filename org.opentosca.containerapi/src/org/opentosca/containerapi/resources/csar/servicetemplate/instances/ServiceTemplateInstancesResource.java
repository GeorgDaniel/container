package org.opentosca.containerapi.resources.csar.servicetemplate.instances;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.containerapi.instancedata.ExistenceChecker;
import org.opentosca.containerapi.instancedata.exception.GenericRestException;
import org.opentosca.containerapi.instancedata.model.SimpleXLink;
import org.opentosca.containerapi.instancedata.utilities.Constants;
import org.opentosca.containerapi.osgi.servicegetter.IOpenToscaControlServiceHandler;
import org.opentosca.containerapi.osgi.servicegetter.InstanceDataServiceHandler;
import org.opentosca.containerapi.osgi.servicegetter.PlanInvocationEngineHandler;
import org.opentosca.containerapi.osgi.servicegetter.ToscaServiceHandler;
import org.opentosca.containerapi.resources.utilities.JSONUtils;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.containerapi.resources.xlink.Reference;
import org.opentosca.containerapi.resources.xlink.References;
import org.opentosca.containerapi.resources.xlink.XLinkConstants;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.instancedata.service.IInstanceDataService;
import org.opentosca.model.instancedata.ServiceInstance;
import org.opentosca.model.tosca.TBoolean;
import org.opentosca.model.tosca.extension.transportextension.TParameterDTO;
import org.opentosca.model.tosca.extension.transportextension.TPlanDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author User christian.endres@iaas.uni-stuttgart.de
 * @author Florian Haupt <florian.haupt@iaas.uni-stuttgart.de>
 * @author Marcus Eisele <marcus.eisele@gmail.com>
 *
 */
public class ServiceTemplateInstancesResource {
	
	private final Logger log = LoggerFactory.getLogger(ServiceTemplateInstancesResource.class);
	private final CSARID csarId;
	private final QName serviceTemplateID;
	
	
	public ServiceTemplateInstancesResource(CSARID csarid, QName serviceTemplateID) {
		csarId = csarid;
		this.serviceTemplateID = serviceTemplateID;
		log.debug("Created \"{}\":\"{}\";", serviceTemplateID.getNamespaceURI(), serviceTemplateID.getLocalPart());
	}
	
	@GET
	@Produces(ResourceConstants.LINKED_XML)
	public Response doGetXML(@Context UriInfo uriInfo, @QueryParam("BuildPlanCorrelationId") String buildPlanCorrId) {
		
		References refs = getRefs(uriInfo, buildPlanCorrId);
		
		return Response.ok(refs.getXMLString()).build();
	}
	
	@GET
	@Produces(ResourceConstants.LINKED_JSON)
	public Response doGetJSON(@Context UriInfo uriInfo, @QueryParam("BuildPlanCorrelationId") String buildPlanCorrId) {
		
		References refs = getRefs(uriInfo, buildPlanCorrId);
		
		return Response.ok(refs.getJSONString()).build();
	}
	
	public References getRefs(UriInfo uriInfo, String buildPlanCorrId) {
		
		// URI serviceInstanceIdURI = null;
		// QName serviceTemplateIDQName = null;
		// try {
		// if (serviceInstanceID != null) {
		// serviceInstanceIdURI = new URI(serviceInstanceID);
		// if (!IdConverter.isValidServiceInstanceID(serviceInstanceIdURI)) {
		// throw new Exception("Error converting serviceInstanceID: invalid
		// format!");
		// }
		// }
		// if (serviceTemplateID != null) {
		// serviceTemplateIDQName = QName.valueOf(serviceTemplateID);
		// }
		// } catch (Exception e1) {
		// throw new GenericRestException(Status.BAD_REQUEST, "Bad Request due
		// to bad variable content: " + e1.getMessage());
		// }
		
		//		try {
		
		References refs = new References();
		
		// get all instance ids
		if (null == buildPlanCorrId || buildPlanCorrId.equals("") || !BuildCorrelationToInstanceMapping.instance.knowsCorrelationId(buildPlanCorrId)) {
			
			IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
			
			List<ServiceInstance> serviceInstances = service.getServiceInstancesWithDetails(csarId, serviceTemplateID, null);
			// List<ServiceInstance> serviceInstances =
			// service.getServiceInstances(serviceInstanceIdURI,
			// serviceTemplateName, serviceTemplateIDQName);
			log.debug("Returning all known Service Template instance IDs ({}).", serviceInstances.size());
			
			for (ServiceInstance serviceInstance : serviceInstances) {
				
				log.debug("ST ID of service \"{}\":\"{}\" vs. path \"{}\":\"{}\"", serviceInstance.getServiceTemplateID().getNamespaceURI(), serviceInstance.getServiceTemplateID().getLocalPart(), serviceTemplateID.getNamespaceURI(), serviceTemplateID.getLocalPart());
				if (serviceInstance.getServiceTemplateID().equals(serviceTemplateID)) {
					
					int instanceId = serviceInstance.getDBId();
					refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), Integer.toString(instanceId)), XLinkConstants.SIMPLE, Integer.toString(instanceId)));
				}
				// URI urlToServiceInstance =
				// LinkBuilder.linkToServiceInstance(uriInfo,
				// serviceInstance.getDBId());
				//
				// // build simpleXLink with the internalID as LinkText
				// // TODO: is the id the correct linkText?
				// links.add(new SimpleXLink(urlToServiceInstance,
				// serviceInstance.getDBId() + ""));
			}
		}
		// get instance id of plan correlation only
		else {
			
			int instanceId = BuildCorrelationToInstanceMapping.instance.getServiceTemplateInstanceIdForBuildPlanCorrelation(buildPlanCorrId);
			refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), Integer.toString(instanceId)), XLinkConstants.SIMPLE, Integer.toString(instanceId)));
			
			log.debug("Returning only the Service Template instance ID for correlation {} ({}).", buildPlanCorrId, instanceId);
			
			// URI urlToServiceInstance =
			// LinkBuilder.linkToServiceInstance(uriInfo, instanceId);
			//
			// // build simpleXLink with the internalID as LinkText
			// // TODO: is the id the correct linkText?
			// links.add(new SimpleXLink(urlToServiceInstance, instanceId +
			// ""));
			
		}
		
		// selflink
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		
		// ServiceInstanceList sil = new
		// ServiceInstanceList(LinkBuilder.selfLink(uriInfo), links);
		return refs;
		//		} catch (Exception e) {
		//			throw new GenericRestException(Status.INTERNAL_SERVER_ERROR, e.getMessage());
		//		}
		
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces({ MediaType.APPLICATION_XML, MediaType.TEXT_XML })
	public Response createServiceInstance(@Context UriInfo uriInfo, String xml) {
		
		log.debug("Create a instance of CSAR = \"{}\" Service Template = \"{}\"", csarId, serviceTemplateID);
		
		IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
		try {
			
			ServiceInstance createdServiceInstance = service.createServiceInstance(csarId, serviceTemplateID);
			
			// create xlink with the link to the newly created serviceInstance,
			// the link text is the internal serviceInstanceID
			
			String corr = xml.substring(xml.indexOf(">") + 1, xml.indexOf("</"));
			
			int serviceTemplateInstanceId = createdServiceInstance.getDBId();
			String instanceURL = createdServiceInstance.getServiceInstanceID().toString();
			log.debug(corr + " : " + corr + " - " + instanceURL);
			
			// correlate true Service Template instance id with temporary one
			{
				BuildCorrelationToInstanceMapping.instance.correlateCorrelationIdToServiceTemplateInstanceId(corr, serviceTemplateInstanceId);
				PlanInvocationEngineHandler.planInvocationEngine.correctCorrelationToServiceTemplateInstanceIdMapping(csarId, serviceTemplateID, corr, serviceTemplateInstanceId);
			String redirectUrl = uriInfo.getAbsolutePath().toString();
			SimpleXLink response = null;
			if (redirectUrl.endsWith("/")) {
				response = new SimpleXLink(uriInfo.getAbsolutePath().toString() + serviceTemplateInstanceId, "simple");
			} else {
				response = new SimpleXLink(uriInfo.getAbsolutePath().toString() + "/" + serviceTemplateInstanceId,
						"simple");
			}

			log.debug("Returning following link: " + response.getHref());
			return Response.ok(response).build();
		} catch (Exception e) {
			e.printStackTrace();
			throw new GenericRestException(Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		
	}
	
	@Path("{" + Constants.ServiceInstanceListResource_getServiceInstance_PARAM + "}")
	public Object getServiceInstance(@PathParam(Constants.ServiceInstanceListResource_getServiceInstance_PARAM) int id) {
		IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
		ExistenceChecker.checkServiceInstanceWithException(id, service);
		return new ServiceTemplateInstanceResource(csarId, serviceTemplateID, id);
	}
	
	/**
	 * POST for BUILD plans which have no CSAR-Instance-ID yet.
	 *
	 * @param planElement the BUILD PublicPlan
	 * @return Response
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 */
	@POST
	@Consumes(ResourceConstants.TEXT_PLAIN)
	@Produces(ResourceConstants.APPLICATION_JSON)
	public Response postBUILDJSONReturnJSON(@Context UriInfo uriInfo, String json) throws URISyntaxException, UnsupportedEncodingException {
		String url = postManagementPlanJSON(uriInfo, json);
		JsonObject ret = new JsonObject();
		ret.addProperty("PlanURL", url);
		return Response.created(new URI(url)).entity(ret.toString()).build();
	}
	
	/**
	 * POST for BUILD plans which have no CSAR-Instance-ID yet.
	 *
	 * @param planElement the BUILD PublicPlan
	 * @return Response
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 */
	@POST
	@Consumes(ResourceConstants.TEXT_PLAIN)
	@Produces(ResourceConstants.TOSCA_XML)
	public Response postBUILDJSONReturnXML(@Context UriInfo uriInfo, String json) throws URISyntaxException, UnsupportedEncodingException {
		
		String url = postManagementPlanJSON(uriInfo, json);
		// return Response.ok(postManagementPlanJSON(uriInfo, json)).build();
		return Response.created(new URI(url)).build();
	}
	
	/**
	 * POST for BUILD plans which have no CSAR-Instance-ID yet.
	 *
	 * @param planElement the BUILD PublicPlan
	 * @return Response
	 * @throws UnsupportedEncodingException
	 */
	private String postManagementPlanJSON(UriInfo uriInfo, String json) throws UnsupportedEncodingException {
		
		log.debug("Received a build plan for CSAR " + csarId + "\npassed entity:\n   " + json);
		
		JsonParser parser = new JsonParser();		
		JsonObject object = parser.parse(json).getAsJsonObject();
		
		log.debug(object.toString());
		
		log.trace(JSONUtils.withoutQuotationMarks(object.get("ID").toString()));
		
		TPlanDTO plan = new TPlanDTO();
		
		plan.setId(new QName(JSONUtils.withoutQuotationMarks(object.get("ID").toString())));
		plan.setName(JSONUtils.withoutQuotationMarks(object.get("Name").toString()));
		plan.setPlanType(JSONUtils.withoutQuotationMarks(object.get("PlanType").toString()));
		plan.setPlanLanguage(JSONUtils.withoutQuotationMarks(object.get("PlanLanguage").toString()));
		
		JsonArray array = object.get("InputParameters").getAsJsonArray();
		Iterator<JsonElement> iterator = array.iterator();
		while (iterator.hasNext()) {
			TParameterDTO para = new TParameterDTO();
			JsonObject tmp = iterator.next().getAsJsonObject();
			para.setName(JSONUtils.withoutQuotationMarks(tmp.get("InputParameter").getAsJsonObject().get("Name").toString()));
			para.setRequired(TBoolean.fromValue(JSONUtils.withoutQuotationMarks(tmp.get("InputParameter").getAsJsonObject().get("Required").toString())));
			para.setType(JSONUtils.withoutQuotationMarks(tmp.get("InputParameter").getAsJsonObject().get("Type").toString()));
			// if a parameter value is not set, just add "" as value
			if (null != tmp.get("InputParameter").getAsJsonObject().get("Value")) {
				para.setValue(JSONUtils.withoutQuotationMarks(tmp.get("InputParameter").getAsJsonObject().get("Value").toString()));
			} else {
				para.setValue("");
			}
			plan.getInputParameters().getInputParameter().add(para);
		}
		array = object.get("OutputParameters").getAsJsonArray();
		iterator = array.iterator();
		while (iterator.hasNext()) {
			TParameterDTO para = new TParameterDTO();
			JsonObject tmp = iterator.next().getAsJsonObject();
			para.setName(JSONUtils.withoutQuotationMarks(tmp.get("OutputParameter").getAsJsonObject().get("Name").toString()));
			para.setRequired(TBoolean.fromValue(JSONUtils.withoutQuotationMarks(tmp.get("OutputParameter").getAsJsonObject().get("Required").toString())));
			para.setType(JSONUtils.withoutQuotationMarks(tmp.get("OutputParameter").getAsJsonObject().get("Type").toString()));
			plan.getOutputParameters().getOutputParameter().add(para);
		}
		
		String namespace = ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper().getNamespaceOfPlan(csarId, plan.getId().getLocalPart());
		plan.setId(new QName(namespace, plan.getId().getLocalPart()));
		
		log.debug("Post of the Plan " + plan.getId());
		
		String correlationID = IOpenToscaControlServiceHandler.getOpenToscaControlService().invokePlanInvocation(csarId, serviceTemplateID, -1, plan);
		
		log.debug("Return correlation ID of running plan: " + correlationID);
		
		String url = uriInfo.getBaseUri().toString() + "CSARs/" + csarId.getFileName() + "/ServiceTemplates/" + URLEncoder.encode(serviceTemplateID.toString(), "UTF-8") + "/Instances?BuildPlanCorrelationId=" + correlationID;
		
		return url;
		
	}
}