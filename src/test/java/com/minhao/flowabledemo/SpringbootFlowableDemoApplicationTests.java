package com.minhao.flowabledemo;


import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class SpringbootFlowableDemoApplicationTests {

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

    @Test
    void contextLoads() {
    }


    String staffId = "1000";
    /**
     * 开启一个流程
     */
    @Test
    void askForLeave() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("leaveTask", staffId);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("ask_for_leave", map);
        runtimeService.setVariable(processInstance.getId(), "name", "javaboy");
        runtimeService.setVariable(processInstance.getId(), "reason", "休息一下");
        runtimeService.setVariable(processInstance.getId(), "days", 10);
        System.out.printf("创建请假流程 processId：" + processInstance.getId());
    }


    String zuzhangId = "90";
    /**
     * 提交给组长审批
     */
    @Test
    void submitToZuzhang() {
        //员工查找到自己的任务，然后提交给组长审批
        List<Task> list = taskService.createTaskQuery().taskAssignee(staffId).orderByTaskId().desc().list();
        for (Task task : list) {
            System.out.println("任务 ID："+task.getId()+"；任务处理人："+task.getAssignee()+"；任务是否挂起："+task.isSuspended() );
            Map<String, Object> map = new HashMap<>();
            //提交给组长的时候，需要指定组长的 id
            map.put("zuzhangTask", zuzhangId);
            taskService.complete(task.getId(), map);
        }
    }


    String managerId = "1";
    /**
     * 组长审批-批准
     */
    @Test
    void zuZhangApprove() {
        List<Task> list = taskService.createTaskQuery().taskAssignee(zuzhangId).orderByTaskId().desc().list();
        for (Task task : list) {
            System.out.println("组长 "+task.getAssignee()+" 在审批 "+task.getId()+" 任务");
            Map<String, Object> map = new HashMap<>();
            //组长审批的时候，如果是同意，需要指定经理的 id
            map.put("managerTask", managerId);
            map.put("checkResult", "通过");
            taskService.complete(task.getId(), map);
        }
    }

}
