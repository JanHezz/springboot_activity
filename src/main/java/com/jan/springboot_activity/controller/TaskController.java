package com.jan.springboot_activity.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jan.springboot_activity.mapper.UserMapper;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * @CLASSNAME TaskController
 * @Description
 * @Auther Jan  橙寂
 * @DATE 2019/9/11 0011 9:55
 */
@RestController
@RequestMapping("/model")
public class TaskController {


    @Autowired
    TaskService taskService;
    @Autowired
    IdentityService identityService;
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    FormService formService;

    @Autowired
    RepositoryService repositoryService;


    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 发布流程
     * @param id
     * @return
     */
    @RequestMapping("/deploy/{id}")
    public Object deploy(@PathVariable("id") String id ) {

        //获取模型
        Model modelData = repositoryService.getModel(id);
        byte[] bytes = repositoryService.getModelEditorSource(modelData.getId());

        if (bytes == null) {
            return "fail";
        }
        JsonNode modelNode = null;
        try {
            modelNode = new ObjectMapper().readTree(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return "fail";
        }
        BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
        if (model.getProcesses().size() == 0) {
            return "fail";
        }
        byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model);

        //发布流程
        String processName = modelData.getName() + ".bpmn20.xml";
        Deployment deployment = null;
        try {
            deployment = repositoryService.createDeployment()
                    .name(modelData.getName())
                    .addString(processName, new String(bpmnBytes, "UTF-8"))
                    .deploy();
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
            return  "fail";
        }
        modelData.setDeploymentId(deployment.getId());
        repositoryService.saveModel(modelData);

        return "success";
    }


    /**
     *启动流程
     * @param id
     * @return
     */
    @RequestMapping("/startProcessInstanceByKey/{id}")
    public Object startProcessInstanceByKey(@PathVariable("id") String id ) {

        //设置发起者
         identityService.setAuthenticatedUserId(id);
        // 启动流程 key:要启动的模型的key   businessId:业务的id
        ProcessInstance procIns = runtimeService.startProcessInstanceByKey("key","businessId");

        return "success";
    }

}
