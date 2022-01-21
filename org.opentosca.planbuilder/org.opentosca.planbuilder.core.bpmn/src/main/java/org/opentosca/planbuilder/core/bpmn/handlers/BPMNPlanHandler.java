package org.opentosca.planbuilder.core.bpmn.handlers;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.javatuples.Pair;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.RelationshipTemplateActivity;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import org.opentosca.planbuilder.core.bpmn.fragments.BPMNProcessFragments;

public class BPMNPlanHandler {

    private final static Logger LOG = LoggerFactory.getLogger(BPMNPlanHandler.class);
    private final DocumentBuilderFactory documentBuilderFactory;
    private final DocumentBuilder documentBuilder;
    private final BPMNScopeHandler bpmnScopeHandler;
    private final BPMNProcessFragments fragmentclass;

    public BPMNPlanHandler() throws ParserConfigurationException {
        this.documentBuilderFactory = DocumentBuilderFactory.newInstance();
        this.documentBuilderFactory.setNamespaceAware(true);
        this.documentBuilder = this.documentBuilderFactory.newDocumentBuilder();
        this.bpmnScopeHandler = new BPMNScopeHandler();
        // test
        this.fragmentclass = new BPMNProcessFragments();
    }

    public BPMNPlan createEmptyBPMNPlan(final String processNamespace, final String processName,
                                        final AbstractPlan abstractPlan, final String inputOperationName) {
        BPMNPlanHandler.LOG.debug("Creating BuildPlan for ServiceTemplate {}",
            abstractPlan.getServiceTemplate().getId());

        final BPMNPlan buildPlan =
            new BPMNPlan(abstractPlan.getId(), abstractPlan.getType(), abstractPlan.getDefinitions(),
                abstractPlan.getServiceTemplate(), abstractPlan.getActivites(), abstractPlan.getLinks());
        initializeXMLElements(buildPlan);
        initializeScriptDocuments(buildPlan);

        return buildPlan;
    }

    public void initializeScriptDocuments(final BPMNPlan newBuildPlan) {
        ArrayList<String> scripts = new ArrayList<>();
        newBuildPlan.setBpmnScript(scripts);
        String script = "";
        try {
            String[] scriptNames = {"CreateServiceInstance", "CreateNodeInstance", "CreateRelationshipInstance", "DataObject", "SetProperties", "SetState"};
            for (String name : scriptNames) {
                script = fragmentclass.createScript(name);
                scripts.add(script);
            }
            newBuildPlan.setBpmnScript(scripts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initializeXMLElements(final BPMNPlan newBuildPlan) {
        newBuildPlan.setBpmnDocument(this.documentBuilder.newDocument());
        newBuildPlan.setBpmnDefinitionElement(newBuildPlan.getBpmnDocument().createElementNS(BPMNPlan.bpmnNamespace,
            "bpmn:definitions"));
        newBuildPlan.getBpmnDocument().appendChild(newBuildPlan.getBpmnDefinitionElement());
        // declare xml schema namespace
        newBuildPlan.getBpmnDefinitionElement().setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:bpmn",
            "http://www.omg.org/spec/BPMN/20100524/MODEL");
        newBuildPlan.getBpmnDefinitionElement().setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:camunda","http://camunda.org/schema/1.0/bpmn");

        // TODO: set other attribute for bpmn:definitions

        // initialize and append extensions element to process
        newBuildPlan.setBpmnProcessElement((newBuildPlan.getBpmnDocument().createElementNS(BPMNPlan.bpmnNamespace,
            "bpmn:process")));

        newBuildPlan.setBpmnDiagramElement(newBuildPlan.getBpmnDocument().createElementNS(BPMNPlan.bpmnNamespace,
            "bpmn:BPMNDiagram"));
        newBuildPlan.getBpmnProcessElement().setAttribute("id", "Process_Random");
        newBuildPlan.getBpmnProcessElement().setAttribute("isExecutable", "true");

        // process and diagram element belong to definition
        newBuildPlan.getBpmnDefinitionElement().appendChild(newBuildPlan.getBpmnProcessElement());
        newBuildPlan.getBpmnDefinitionElement().appendChild(newBuildPlan.getBpmnDiagramElement());
        Element planeElement = newBuildPlan.getBpmnDocument().createElement("bpmndi:BPMNPlane");
        planeElement.setAttribute("id", "Plane_Id");

        // every elements in bpmn:BPMNDiagram needs to have a matching element in bpmn:process
        planeElement.setAttribute("bpmnElement", "Process_Id");
        newBuildPlan.getBpmnDiagramElement().appendChild(planeElement);

        // create start event from fragment -> just a test, has to be done by plugin!
        try {
            Element startEvent = (Element) newBuildPlan.getBpmnDocument().importNode((Element) fragmentclass.createBPMNStartEventAsNode("lustigerEventName", "tollerFlow"), true);
            Element endEvent = (Element) newBuildPlan.getBpmnDocument().importNode((Element) fragmentclass.createBPMNEndEventAsNode("lustigerEventName", "tollerFlow"), true);
            newBuildPlan.setBpmnStartEvent(startEvent);
            newBuildPlan.getBpmnProcessElement().appendChild(newBuildPlan.getBpmnStartEvent());
            newBuildPlan.setBpmnEndEvent(endEvent);
            newBuildPlan.getBpmnProcessElement().appendChild(newBuildPlan.getBpmnEndEvent());
        } catch (Exception e){
            BPMNPlanHandler.LOG.debug("error with fragments:", e);
        }
    }

    /**
     * Instantiate all BPMN Process elements with class BPMNScope for later plugin refinement
     * @param plan
     * @param csar
     */
    public void initializeBPMNSkeleton(final BPMNPlan plan, final Csar csar) {
        plan.setCsarName(csar.id().csarName());

        final Map<AbstractActivity, BPMNScope> abstract2bpmnMap = new HashMap<>();

        // TODO: instantiate and add "Create Service Template Instance" BPMN script task
        BPMNScope startEvent = bpmnScopeHandler.createStartEvent(plan);
        BPMNScope createServiceTemplateInstanceTask = bpmnScopeHandler.createServiceTemplateInstanceTask(plan);
        bpmnScopeHandler.createSequenceFlow(startEvent, createServiceTemplateInstanceTask, plan);

        // a graph of AbstractActivity used for BFS
        final Map<AbstractActivity, Set<AbstractActivity>> aaGraph = new HashMap<>();
        // [curA : prevB]
        Deque<Pair<AbstractActivity, BPMNScope>> q = new LinkedList<>();
        Set<AbstractActivity> visited = new HashSet<>();

        // build graph for iteration
        for (final AbstractPlan.Link link : plan.getLinks()) {
            AbstractActivity src = link.getSrcActiv();
            AbstractActivity trg = link.getTrgActiv();
            aaGraph.putIfAbsent(src, new HashSet<>());
            aaGraph.get(src).add(trg);
        }

        for (final AbstractActivity activity : plan.getSources()) {
            q.offer(new Pair<>(activity, createServiceTemplateInstanceTask));
        }

        AbstractActivity curA = null;
        BPMNScope prevB = null;

        while (!q.isEmpty()) {
            int size = q.size();
            while (size > 0) {
                Pair<AbstractActivity, BPMNScope> p = q.poll();
                size -= 1;

                curA = p.getValue0();
                prevB = p.getValue1();

                if (aaGraph.containsKey(curA)) {
                    List<AbstractActivity> order = new ArrayList<>();
                    order.add(curA);
                    Set<AbstractActivity> nxtSet = aaGraph.get(curA);
                    for (AbstractActivity nxtA : nxtSet) {
                        // need to go one more step to reach another NodeTemplate
                        if (nxtA instanceof RelationshipTemplateActivity) {
                            AbstractActivity nxtnxtA = aaGraph.get(nxtA).iterator().next();
                            order.add(nxtnxtA);
                        }
                        // add RelationshipTemplate last
                        order.add(nxtA);
                    }

                    // TODO: consider parallel case
                    // iterating to instantiate BPMNScope
                    for (int i = 0; i < order.size(); i += 1) {
                        curA = order.get(i);

                        // make sure no duplicate is created
                        BPMNScope curB = abstract2bpmnMap.getOrDefault(curA, bpmnScopeHandler.createTemplateBuildPlan(curA, plan));
                        abstract2bpmnMap.put(curA, curB);
                        bpmnScopeHandler.createSequenceFlow(prevB, curB, plan);
                        prevB = curB;

                        // enqueue last element
                        if (i == order.size() - 1) {
                            Pair<AbstractActivity, BPMNScope> nxtPair = new Pair<>(curA, curB);
                            q.offer(nxtPair);
                        }
                    }
                }
            }
        }

        // TODO: instantiate and set "Set Service Template State" BPMN script task
        BPMNScope setSTStateTask = bpmnScopeHandler.createSetServiceTemplateStateTask(plan);
        bpmnScopeHandler.createSequenceFlow(prevB, setSTStateTask, plan);
        BPMNScope endEvent = bpmnScopeHandler.createEndEvent(plan);
        bpmnScopeHandler.createSequenceFlow(setSTStateTask, endEvent, plan);
    }
}