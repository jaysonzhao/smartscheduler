package com.gzsolartech.schedule.service;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gzsolartech.smartforms.constant.EntitySwitchSignal;
import com.gzsolartech.smartforms.entity.DatContentTemplate;
import com.gzsolartech.smartforms.entity.OrgEmployee;
import com.gzsolartech.smartforms.entity.bpm.BpmActivityMeta;
import com.gzsolartech.smartforms.entity.bpm.BpmTaskInfo;
import com.gzsolartech.smartforms.service.BaseDataService;
import com.gzsolartech.smartforms.service.DatContentTemplateService;
import com.gzsolartech.smartforms.service.DatDocumentService;
import com.gzsolartech.smartforms.service.OrgEmployeeService;
import com.gzsolartech.smartforms.service.SysConfigurationService;
import com.gzsolartech.smartforms.service.bpm.BpmActivityMetaService;
import com.gzsolartech.smartforms.service.bpm.BpmTaskInfoService;

/**
 * 
* @ClassName: BpmTaskInfoOntimeService 
* @Description: 待办环节固定周期邮件通知
* @author wwd 
* @date 2017年12月11日 上午11:36:24 
*
 */
@Service
public class BpmTaskInfoOnTimeService extends BaseDataService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(BpmTaskInfoOnTimeService.class);
	@Autowired
	private BpmTaskInfoService bpmTaskInfoService;
	@Autowired
	private DatDocumentService datDocumentService;
	@Autowired
	private SysConfigurationService sysConfigurationService;
	@Autowired
	private DatContentTemplateService datContentTemplateService;
	@Autowired
	private BpmActivityMetaService bpmActivityMetaService;
	@Autowired
	private OrgEmployeeService orgEmployeeService;
	@Autowired
	private SendEmailService sendEmailService;

	/**
	 * 固定周期邮件通知
	 *@throws Exception
	 */
	public void execute() throws Exception {
		LOGGER.debug("固定周期邮件通知调度任务开始执行");
		// 获取所的的待办
		List<BpmTaskInfo> bpmTaskInfos = bpmTaskInfoService
				.getAllReceivedTask();
		// 遍历待办信息
		for (BpmTaskInfo bpmTaskInfo : bpmTaskInfos) {
			try {
				// 流程快照
				String snapshotBpdId = bpmTaskInfo.getSnapshotBpdId();
				// 流程图环节ID
				String nodeId = bpmTaskInfo.getNodeId();
				// 获取当前待办所在的环节信息
				BpmActivityMeta bpmActivityMeta = bpmActivityMetaService
						.getBpmActivityMeta(nodeId, snapshotBpdId);
				// 环节信息不为空
				if (bpmActivityMeta != null
						&&EntitySwitchSignal.YES.equals(bpmActivityMeta.getOnTimeNotify())) {
					remind(bpmTaskInfo, bpmActivityMeta);
				}
			} catch (Exception e) {
				LOGGER.error("固定周期邮件通知出现异常", e);
			}
		}
		LOGGER.debug("固定周期邮件通知调度任务结束执行");
	}

	
	
    /**
     * 发送邮件通知
     *@param bpmTaskInfo
     *@param bpmActivityMeta
     */
	public void remind(BpmTaskInfo bpmTaskInfo, BpmActivityMeta bpmActivityMeta) {
		LOGGER.debug("定时邮件通知:" + bpmTaskInfo.getTaskName());
		OrgEmployee employee = gdao.findById(OrgEmployee.class,bpmTaskInfo.getAssignedTo());
		if (employee != null) {
			String email = employee.getEmail();
			Map<String, Object> data = datDocumentService.getDocumentById(bpmTaskInfo.getDocumentId());
			data.put("_taskId", bpmTaskInfo.getTaskId());
			LOGGER.debug("定时邮件提醒: " + employee.getNickName() + " email="+ email);
			DatContentTemplate datContentTemplate = datContentTemplateService.load(bpmActivityMeta.getOnTimeTemplateId());
			String content = "有待办信息需要您及时处理:<br>";
			String title = "定时邮件提醒";
			if (datContentTemplate != null) {
				title = datContentTemplate.getTitle();
				content = datContentTemplate.getTextContent();
				if (StringUtils.isNotBlank(title)) {
					title = pasePlaceholder(title, data);
				}
				if (StringUtils.isNotBlank(content)) {
					content = pasePlaceholder(content, data);
				}
			}
			// 发送邮件
			sendEmailService.sendEmail(email, title, content, null);
		}
	}

	

	/**
	 * 内容替换
	 * @param conent
	 * @param data
	 * @return
	 * @return void 返回类型
	 * @throws
	 */
	public String pasePlaceholder(String conent, Map<String, Object> data) {
		if (StringUtils.isNotBlank(conent) && data != null) {
			String regEx = "\\{[^}]*\\}";
			Pattern pat = Pattern.compile(regEx);
			Matcher mat = pat.matcher(conent);
			while (mat.find()) {
				String tmp = mat.group(0);
				tmp = tmp.replaceAll("\\{", "");
				tmp = tmp.replaceAll("\\}", "");
				String value = data.get(tmp) + "";
				conent = conent.replaceAll("\\{" + tmp + "\\}", value);
			}
		}
		return conent;
	}
}
