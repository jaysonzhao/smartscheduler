package com.gzsolartech.schedule.quartz.task;

import java.sql.Timestamp;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.gzsolartech.bpmportal.entity.BpmActivityCostTime;
import com.gzsolartech.schedule.service.BpmActivityCostTimeService;
import com.gzsolartech.schedule.service.HR0002Service;

@Component
public class BpmActivityCostTimeTask extends BaseTask{
	private static final Logger LOG = LoggerFactory
			.getLogger(BpmActivityCostTimeTask.class);

	@Override
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		// TODO Auto-generated method stub
		this.applicationContext=applicationContext;
	}

	@Override
	public void run(String jobId) {
		LOG.debug("开始调用考勤接口计算环节审批用时");
		BpmActivityCostTimeService bpmActivityCostTimeService = (BpmActivityCostTimeService) applicationContext.getBean(BpmActivityCostTimeService.class);
		try {
			List<BpmActivityCostTime> empty =  bpmActivityCostTimeService.getCostTimeEmpty();
			int now = 1;
			List<BpmActivityCostTime> infos = bpmActivityCostTimeService.listNeedUpdateObejct();
			System.out.println("本次新增前一天到调度时或更新"+infos.size()+"条数据");
			for(BpmActivityCostTime info :infos){
				System.out.println("第一项插入--为本次插入中第"+now +"条数据开始查询考勤接口");
				bpmActivityCostTimeService.saveCountCostTime(info);
				now++;
			}
			
			System.out.println("本次SAP考勤时间为空的"+empty.size()+"条数据");
			for(BpmActivityCostTime info :empty){
				System.out.println("第二项插入--为本次插入中第"+now +"条数据开始查询考勤接口");
				bpmActivityCostTimeService.saveCountCostTime(info);
				now++;
			}
			List<BpmActivityCostTime> submits = bpmActivityCostTimeService.getSubmitTimeEmpty();
			System.out.println("本次更新提交时间为空的"+submits.size()+"条数据");
			for(BpmActivityCostTime info :submits){
				System.out.println("第三项插入--为本次插入中第"+now +"条数据开始查询考勤接口");
				info.setSubmitTime(new Timestamp(System.currentTimeMillis()));
				bpmActivityCostTimeService.saveCountCostTime(info);
				now++;
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("调用考勤接口计算环节审批用时失败",e);
		}
		LOG.debug("调用考勤接口计算环节审批用时成功");
	}

}
