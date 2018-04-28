package com.gzsolartech.schedule.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gzsolartech.bpmportal.entity.AACEquipmentData;
import com.gzsolartech.bpmportal.entity.COMdetails;
import com.gzsolartech.bpmportal.entity.COMdetailsSum;
import com.gzsolartech.bpmportal.entity.ComModel;
import com.gzsolartech.bpmportal.entity.ComPwd;
import com.gzsolartech.smartforms.service.BaseDataService;




@Service
public class IT35Service extends  BaseDataService {
	private static final Logger LOG = LoggerFactory
			.getLogger(IT35Service.class);
	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
//		List<Map<String,Object>>  data = new ArrayList<Map<String,Object>>();
//		String url = "jdbc:oracle:thin:@10.162.224.90:1521:bpmdev";
//		Connection connection = DriverManager.getConnection(url,"smartform","smartform1");
//
//		
//		//根据类型获取数据
//		String sql = "select * from AAC_EMPLOYEE where EMP_NUM = '60001347'";
//		
//		
//		PreparedStatement prepareStatement = connection.prepareStatement(sql);
//		ResultSet resultSet = prepareStatement.executeQuery();
//		while (resultSet.next()) {
//			System.out.println(resultSet);
//		}
//		
//		resultSet.close();
//		prepareStatement.close();  
//		connection.close();
		
		
		
//		Statement sql;
//		ResultSet rs;
//		//String driverName = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
//		String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";   //加载JDBC驱动  
//
//		//String dbURL = "jdbc:sqlserver://10.176.148.13:1433; DatabaseName=V16CNDCZ01SQL02;integratedSecurity=True";   //连接服务器和数据库sample  
//		String dbURL = "jdbc:sqlserver://10.176.148.13:1433; DatabaseName=CM_CZC";
//
//		String userName = "S-BPMDBreader";   //默认用户名 
//		String userPwd = "123456!a";   //密码  
//		Connection dbConn;  
//		try {  
////https://zhidao.baidu.com/question/41148488.html
//			Class.forName(driverName);  
//			dbConn = DriverManager.getConnection(dbURL, userName, userPwd); 
//			sql=dbConn.createStatement();
//			//rs=sql.executeQuery("select MachineID,Date00,minutesTotal00,SerialNumber0,Netbios_Name0 from POWER_MANAGEMENT_DAY_DATA");
//			rs=sql.executeQuery("select MachineID,Date00,minutesTotal00,SerialNumber0,Netbios_Name0 from POWER_MANAGEMENT_DAY_DATA,v_R_System,v_GS_PC_BIOS where POWER_MANAGEMENT_DAY_DATA.MachineID = v_R_System.ResourceID and POWER_MANAGEMENT_DAY_DATA.MachineID = v_GS_PC_BIOS.ResourceID");
//			List<ComModel> list = new ArrayList<ComModel>();
//			while(rs.next()){
//				//System.out.println(rs.getString("MachineID"));
//				ComModel cm = new ComModel();
//				cm.setComputerName(rs.getString("Date00"));
//				list.add(cm);
//			}
//			rs.close();
//			sql.close();
//			dbConn.close();
//			System.out.println(list.size());
//		}catch(Exception e){
//			System.out.println("111");
//			e.printStackTrace();  
//		}
		
		
	}
	
	//从SCCN获取数据
	public List<ComModel> getSCCNData(){
		List<COMdetails> comList = selectCom();
		System.out.println(comList.size());
		Statement sql;
		ResultSet rs;
		String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";   //加载JDBC驱动  
		String dbURL = "jdbc:sqlserver://10.176.148.13:1433; DatabaseName=CM_CZC";
		String userName = "S-BPMDBreader";   //默认用户名 
		String userPwd = getSCCMPwd().getPassWord();   //密码  
		Connection dbConn;  
		List<ComModel> list = new ArrayList<ComModel>();
		try {  
			Class.forName(driverName);  
			dbConn = DriverManager.getConnection(dbURL, userName, userPwd); 
			sql=dbConn.createStatement();
			if (comList.isEmpty() ) {
				rs=sql.executeQuery("select MachineID,Date00,minutesTotal00,SerialNumber0,Netbios_Name0 from POWER_MANAGEMENT_DAY_DATA,v_R_System,v_GS_PC_BIOS where POWER_MANAGEMENT_DAY_DATA.MachineID = v_R_System.ResourceID and POWER_MANAGEMENT_DAY_DATA.MachineID = v_GS_PC_BIOS.ResourceID");
			}else{
				rs=sql.executeQuery("select MachineID,Date00,minutesTotal00,SerialNumber0,Netbios_Name0 from POWER_MANAGEMENT_DAY_DATA,v_R_System,v_GS_PC_BIOS where POWER_MANAGEMENT_DAY_DATA.MachineID = v_R_System.ResourceID and POWER_MANAGEMENT_DAY_DATA.MachineID = v_GS_PC_BIOS.ResourceID and CONVERT(varchar(12) ,Date00, 105 )>=CONVERT(varchar(12) ,dateadd(day,-1,getdate()), 105 )");
			}
			while(rs.next()){
				ComModel cm = new ComModel();
				cm.setComputerName(rs.getString("Netbios_Name0"));
				cm.setSerialNumber(rs.getString("SerialNumber0"));
				cm.setTimeDate(rs.getString("Date00"));
				cm.setComputerhourly(rs.getString("minutesTotal00"));
				list.add(cm);
			}
			rs.close();
			sql.close();
			dbConn.close();
			System.out.println(list.size());
		}catch(Exception e){
			e.printStackTrace();  
		}
		return list;
	}
	
	
	//把从SCCN获取的数据存在BPM数据库里
	public void instComData(){
		List<ComModel> list = getSCCNData();
		//String hql = "from COMdetails";
		//List<COMdetails> comList = gdao.queryHQL(hql);
		//gdao.delete(comList);
		//String sql = "delete from COM_DETAILS where 1=1";
		//gdao.executeJDBCSqlQuery(sql);  
		if (!list.isEmpty() ) {
			int size=list.size();
			for (int i=0; i<size; i++) {
				ComModel com = list.get(i);
				COMdetails comDe = new COMdetails();
				String uuid = UUID.randomUUID().toString().replaceAll("-", "");
				comDe.setId(uuid);
				System.out.println(uuid);
				comDe.setComputerName(com.getComputerName());
				System.out.println(com.getComputerName());
				comDe.setComputerhourly(com.getComputerhourly());
				comDe.setSerialNumber(com.getSerialNumber());
				comDe.setTimeDate(com.getTimeDate());
				gdao.saveOrUpdate(comDe);
				if (i>0 && i%50==0) {
					gdao.getSession().flush();
					gdao.getSession().clear();
				}
			}
		}
		gdao.getSession().flush();
		gdao.getSession().clear();
	} 
	
	
	//查询是否是第一次从SCCM获取数据
	public List<COMdetails> selectCom(){
		String hql = "from COMdetails";
		List<COMdetails> comList = gdao.queryHQL(hql);
		return comList;
	}
	
	//获取CSSN的用户名和密码
	public ComPwd getSCCMPwd(){
		ComPwd compwd = new ComPwd();
		String hql = "from ComPwd where sccmName = 'S-BPMDBreader'";
		List<ComPwd> comList = gdao.queryHQL(hql);
		compwd = comList.get(0);
		System.out.println(compwd.getPassWord());
		return compwd;
		
	}
	
	/**
	 * 汇总台账信息
	   Tsy
	 * 2018年3月20日下午5:14:50
	 */
	public void ledgerSum(){
		List<COMdetailsSum> list=new ArrayList<>();
		//获取系统时间的上个月的台账信息
		String sql="select max(COMPUTERNAME) as COMPUTERNAME,sum(COMPUTERHOURLY) as COMPUTERHOURLY,max(SERIALNUMBER) as SERIALNUMBER,count(distinct TIMEDATE) as ACTIVITYDAY,to_char(add_months(sysdate, -1),'yyyy-mm') as DAYS"
				+" from"
				+"("
				+"select  *"
				+"from COM_DETAILS where to_char(to_timestamp(COM_DETAILS.TIMEDATE,'yyyy-mm-dd hh24:mi:ss.ff9')+0,'yyyy-mm')=to_char(add_months(sysdate, -1),'yyyy-mm') and COM_DETAILS.COMPUTERHOURLY is not null"
				+")"
				+"DET group by SERIALNUMBER,COMPUTERNAME";
		List<Map<String, String>> datas=gdao.executeJDBCSqlQuery(sql,new ArrayList<>());
		for (int i = 0; i < datas.size(); i++) {
			Map<String, String> data=new TreeMap<>();
			data=datas.get(i);
			COMdetailsSum coMdetailsSum=new COMdetailsSum();
			//计算机名
			String COMPUTERNAME=(String) data.get("COMPUTERNAME");
			coMdetailsSum.setComputerName(COMPUTERNAME);
			//活动总时间
			Object COMPUTERHOURLY=data.get("COMPUTERHOURLY");
			coMdetailsSum.setComputerHourly(COMPUTERHOURLY.toString());
			//出厂编号
			String SERIALNUMBER=(String) data.get("SERIALNUMBER");
			coMdetailsSum.setSerialnumber(SERIALNUMBER);
			//活动天数
			Object ACTIVITYDAY=data.get("ACTIVITYDAY");
			coMdetailsSum.setActivityday(ACTIVITYDAY.toString());
			//平均活动时长
			Double dou=Double.parseDouble(COMPUTERHOURLY.toString())/Double.parseDouble(ACTIVITYDAY.toString());
			dou = (double)Math.round(dou*100)/100/60;
			dou = (double)Math.round(dou*100)/100;
			coMdetailsSum.setActivityhr(dou.toString());
			//年月日
			Object days=data.get("DAYS");
			coMdetailsSum.setDays(days.toString());
			list.add(coMdetailsSum);
		}
		/**
		 * 开始批量删除台账汇总信息表
		 */
		if(list.size()>0){
			String del="delete COM_DETAILS_SUM WHERE DAYS=?";
			List<Object> params = new ArrayList<Object>();
			params.add(list.get(0).getDays());
			gdao.executeJDBCSql(del, params);
			gdao.getSession().flush();
			gdao.getSession().clear();
		} 
		/**
		 * 开始批量保存台账汇总信息表
		 */
		for (int i = 0; i < list.size(); i++) {
			gdao.save(list.get(i));
			gdao.getSession().flush();
			gdao.getSession().clear();
		}
		/**
		 * 开始批量插入到台账信息详情表
		 */
		insertAACEquipmentData();
	}
	/**
	 * 开始批量插入到台账信息表
	   Tsy
	 * 2018年3月21日下午5:38:22
	 */
	public void insertAACEquipmentData(){
		String sql="select * from com_details_sum where com_details_sum.DAYS=to_char(add_months(sysdate, -1),'yyyy-mm')";
		List<Map<String, String>> datas=gdao.executeJDBCSqlQuery(sql,new ArrayList<>());
		List<Map<String, Object>> list=new ArrayList<>();
		for (int i = 0; i < datas.size(); i++) {
			String viewSql="select * from AAC_EQUIPMENT_DATA where FACTORY_NUM=?";
			Map<String, String> coMdetailsSum=datas.get(i);
			List<Object> params = new ArrayList<Object>();
			params.add(coMdetailsSum.get("SERIALNUMBER"));
			List<Map<String, Object>> obj = gdao.executeJDBCSqlQuery(viewSql,params);
			if(obj.size()>0){
				Map<String, Object> map=new TreeMap<>();
				//平均活动时长
				Double activityhr=Double.parseDouble(coMdetailsSum.get("ACTIVITYHR"));
				//活动天数
				int activityday=Integer.parseInt(coMdetailsSum.get("ACTIVITYDAY"));
				map.put("FACTORY_NUM",coMdetailsSum.get("SERIALNUMBER"));
				//高
				if(activityhr>=4||activityday>=16){
					map.put("FREQUENCY", "高");
					map.put("ACTIVITYHR", activityhr);
					map.put("ACTIVITYDAY", activityday);
				}else if((2<=activityhr&&activityhr<=3)||(8<=activityday&&activityday<=15)){//中
					map.put("FREQUENCY", "中");
					map.put("ACTIVITYHR", activityhr);
					map.put("ACTIVITYDAY", activityday);
				}else{//低
					map.put("FREQUENCY", "低");
					map.put("ACTIVITYHR", activityhr);
					map.put("ACTIVITYDAY", activityday);
				}
				list.add(map);
			}
		}
		/**
		 * 开始批量更改台账信息表
		   Tsy
		 * 2018年3月21日下午6:27:38
		 */
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map=list.get(i);
			String hql="from AACEquipmentData where factoryNum='"+map.get("FACTORY_NUM")+"'";
			List<AACEquipmentData> data=gdao.queryHQL(hql);
			if(!data.isEmpty()){
				AACEquipmentData aacEquipmentData=data.get(0);
				if(aacEquipmentData!=null){
					aacEquipmentData.setActivityhr(map.get("ACTIVITYHR").toString());
					aacEquipmentData.setActivityday(map.get("ACTIVITYDAY").toString());
					aacEquipmentData.setFrequency(map.get("FREQUENCY").toString());
					gdao.update(aacEquipmentData);
					gdao.getSession().flush();
					gdao.getSession().clear();
				}
			}
		}
	}
}
