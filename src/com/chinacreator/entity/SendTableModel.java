package com.chinacreator.entity;

/**
 * @Description
 * 发送文件列表对象
 * 
 * @Author qiang.zhu
 * @Datetime 2016年5月11日 上午9:43:01
 * @Version
 * @Copyright (c) 2013 湖南科创信息技术股份有限公司
 */
public class SendTableModel {

	//文件编号
	private String fileNo;
	//文件绝对路径
	private String filePath;
	//文件进度
	private String fileBar;
	//文件名称
	private String fileName;
	//文件类型(1文件2文件夹)
	private String fileType;
	
	public String getFileNo() {
		return fileNo;
	}
	public void setFileNo(String fileNo) {
		this.fileNo = fileNo;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFileBar() {
		return fileBar;
	}
	public void setFileBar(String fileBar) {
		this.fileBar = fileBar;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
}
