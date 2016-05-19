package com.chinacreator.common;

import java.io.File;
import java.util.Map;
import com.chinacreator.service.DataOprService;

public class Global {
	
	public static final Map<String,String> map=DataOprService.getInstance().getProp();

	public static final String isFile="1";
	
	public static final String isDirectory="2";
	
	public static final String sendFiles="1";
	
	public static final String checkFiles="2";
	
	public static final String receiveFiles="3";
	
	public static final String sendString="4";
	
	public static final String getString="5";

	public static final String[] columns = { "队列序号","文件名称", "处理进度" ,"文件名","文件类型"};
	
	public static final int sendTabFresh=2;
	
	public static final	int[] colnumsWidth={4,400,50,0,0};
	
	public static final String[] columnsWithBox = {"全选","队列序号","文件名称", "处理进度" ,"文件名","文件类型"};
	
	public static final int receiveTabFresh=3;
	
	public static final	int[] colnumsWidthWithBox={0,20,400,50,0,0};

	public static final int sendFileThreadCounts=map.get("sendFileThreadCounts")==null?10:Integer.parseInt(map.get("sendFileThreadCounts"));
	
	public static final int receiveFileThreadCounts=map.get("receiveFileThreadCounts")==null?10:Integer.parseInt(map.get("receiveFileThreadCounts"));

	public static final String frameName="传送门客户端";
	
	public static final String labIp="服务器IP：";
	
	public static final String labPort="服务器端口：";
	
	public static final String btnChooseFile="添加文件";
	
	public static final String btnClearFile="清除文件";
	
	public static final String btnSendFile="上传文件";
	
	public static final String btnCheckFile="查询文件";
	
	public static final String btnReceiveFile="下载文件";

	public static final String downLoadPath="	保存路径：";
	
	public static final String btnDownLoadPath="选择保存路径";

	public static final String defaultIp=map.get("defaultIp")==null?"127.0.0.1":map.get("defaultIp");

	public static final String charFormat=map.get("charFormat")==null?"GBK":map.get("charFormat");
	
	public static final String defaultPort=map.get("defaultPort")==null?"8888":map.get("defaultPort");
	
	public static final int defaultFresh=map.get("defaultFresh")==null?102400:Integer.parseInt(map.get("defaultFresh"));
	
	public static final String defaultSavePath=map.get("defaultSavePath")==null?(System.getProperty("user.dir")+File.separator+"收件箱"):map.get("defaultSavePath");
}
