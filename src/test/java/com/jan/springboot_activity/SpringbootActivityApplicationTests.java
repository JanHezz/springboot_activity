package com.jan.springboot_activity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootActivityApplicationTests {

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
    @Test
    public void contextLoads() {
    }

    /**
     * 发布流程
     * @param id
     * @return
     */
   @Test
    public void deploy() {

       String id="32506";
        //获取模型
        Model modelData = repositoryService.getModel(id);
        byte[] bytes = repositoryService.getModelEditorSource(modelData.getId());

        if (bytes == null) {
            System.out.println("模型数据不准为空");
        }
        JsonNode modelNode = null;
        try {
            modelNode = new ObjectMapper().readTree(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("模型数据不准为空");
        }
        BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
        if (model.getProcesses().size() == 0) {
            System.out.println("模型数据不准为空");
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
            System.out.println("模型数据不准为空");
        }
        modelData.setDeploymentId(deployment.getId());
        repositoryService.saveModel(modelData);


    }



    /*
    开始流程
     */
    @Test
    public void startProcessInstanceByKey() {

        identityService.setAuthenticatedUserId("000");
        // 启动流程
        ProcessInstance procIns = runtimeService.startProcessInstanceByKey("qingjia","111111");


    }

    /**
     * 获取所有任务
     */
    @Test
    public void getToDoTaskList()
    {
        List<Task> tasks = taskService
                .createTaskQuery()
                .taskAssignee("张三")
                .list();

        System.out.println("获得正在进行的任务");
        for (Task demo:tasks
             ) {
            System.out.println(demo.toString());
        }
       /* ProcessInstance pi = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .singleResult();
        return pi.getBusinessKey();*/
    }

    @Test
    public void compelete()
    {
        taskService.complete("45002");


    }

}
