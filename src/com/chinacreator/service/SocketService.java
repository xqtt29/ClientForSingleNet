package com.chinacreator.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.text.DecimalFormat;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import com.chinacreator.common.Global;
import com.chinacreator.entity.ReceiveTableModel;

/**
 * @Description
 * socket操作服务类
 * 
 * @Author qiang.zhu
 * @Datetime 2016年5月10日 上午10:23:46
 * @Version
 * @Copyright (c) 2013 湖南科创信息技术股份有限公司
 */
public class SocketService {
	private JFrame frame;
	private String ip;
	private int port;
	
	public SocketService(JFrame frame,String ip,int port){
		this.frame=frame;
		this.ip=ip;
		this.port=port;
	}
	/**
	 * @Description
	 * 客户端上传文件
	 * 输入socket信息流：前1个字节是操作指令，接着10个字节是上传文件的名称长度，后面接着文件名称，再接着文件信息流
	 * @Author qiang.zhu
	 * @param model
	 * @param no
	 * @param filePath
	 * @param fileName
	 */
	public boolean sendFile(final DefaultTableModel model,final int no,String filePath,String fileName,String fileType){
        int length = 0;
        double sended=0;
        byte[] sendBytes = null;
        Socket socket = null;
        DataOutputStream dos = null;
        DataInputStream dis = null;
        FileInputStream fis = null;
        try {
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(ip,port),
                               3 * 1000);
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());
                File file = new File(filePath);
                if(file.isDirectory()){
                	for(File f : file.listFiles()){
                		if(!sendFile(model,no,f.getAbsolutePath(),fileName+File.separator+f.getName(),fileType)){
                			return false;
                		}
                	}
                }else{
	                long totalSize=file.length();
	                fis = new FileInputStream(file);
	                byte[] fnx=fileName.getBytes(Global.charFormat);
	                String inx=DataOprService.getInstance().initData(fnx.length,10);
	                String head=Global.sendFiles+fileType+inx+fileName;
	                dos.write(head.getBytes(Global.charFormat), 0, head.getBytes(Global.charFormat).length);
	                dos.flush();
	                byte[] result=new byte[1];
	                dis.read(result, 0, 1);
	                if("1".equals(new String(result,Global.charFormat))){
	                	DataOprService.getInstance().clearData(model,no-1,Global.sendTabFresh,"服务器已存在");
	                	return false;
	                }
	                if(totalSize==0){
	                	if(Global.isDirectory.equals(fileType))
	                		DataOprService.getInstance().clearData(model,no-1,Global.sendTabFresh,"100.00%");
	                	else
	                		DataOprService.getInstance().freshData(model,no-1,Global.sendTabFresh,"100.00%");
	                    return true;
	                }
	                sendBytes = new byte[Global.defaultFresh];
	                final DecimalFormat df = new DecimalFormat("0.00");   
	                while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0) {
	                    dos.write(sendBytes, 0, length);
	                    dos.flush();
	                    sended+=length;
	                    if(Global.isDirectory.equals(fileType))
	                    	DataOprService.getInstance().clearData(model,no-1,Global.sendTabFresh,df.format(sended/totalSize*100)+"%");
	                    else
	                    	DataOprService.getInstance().freshData(model,no-1,Global.sendTabFresh,df.format(sended/totalSize*100)+"%");
	                }
                }
                return true;
            } finally {
                if (dis != null)
                    dis.close();
                if (dos != null)
                    dos.close();
                if (fis != null)
                    fis.close();
                if (socket != null)
                    socket.close();
            }
        }catch (ConnectException e) {
        	e.printStackTrace();
        	DataOprService.getInstance().clearData(model,no-1,Global.sendTabFresh,"服务器连接失败");
        	return false;
        }catch (Exception e) {
        	e.printStackTrace();
        	DataOprService.getInstance().clearData(model,no-1,Global.sendTabFresh,"失败");
        	JOptionPane.showMessageDialog(frame, filePath+"上传失败："+e.fillInStackTrace().getMessage(), "警告",JOptionPane.WARNING_MESSAGE);
        	return false;
        }
	}
	/**
	 * @Description
	 * 客户端下载文件
	 * 输入socket信息流：前1个字节是操作指令，接着10个字节是下载文件的名称长度，后面接着文件绝对路径名称
	 * 输出socket信息流：前10个字节是文件长度，后面接着文件信息流
	 * @Author qiang.zhu
	 * @param model
	 * @param no
	 * @param filePath
	 * @param fileName
	 * @param savePath
	 */
	public void receiveFile(final DefaultTableModel model,final int no,String filePath,String fileName,String savePath,String fileType){
		byte[] inputByte = null;
        int length = 0;
        double sended=0;
		Socket socket = null;
        DataOutputStream dos = null;
        DataInputStream dis = null;
        FileOutputStream fos = null;
        savePath=savePath==null||savePath.length()==0?Global.defaultSavePath:savePath;
        try {
            try {
            	socket = new Socket();
                socket.connect(new InetSocketAddress(ip,port),
                               3 * 1000);
            	dos = new DataOutputStream(socket.getOutputStream());
            	byte[] fnx=filePath.getBytes(Global.charFormat);
                String inx=DataOprService.getInstance().initData(fnx.length,10);
                String head=Global.receiveFiles+fileType+inx+filePath;
            	dos.write(head.getBytes(Global.charFormat),0,head.getBytes(Global.charFormat).length);
            	dos.flush();
            	dis = new DataInputStream(socket.getInputStream());
            	if(fileType.equals(Global.isDirectory)){
            		byte[] fileCounts=new byte[4];
	                dis.read(fileCounts, 0, 4);
	                int sfilecount=Integer.parseInt(new String(fileCounts,Global.charFormat));
	                byte[] sfileTotalSize=new byte[10];
	                dis.read(sfileTotalSize, 0, 10);
	                long sTotalSize=Long.parseLong(new String(sfileTotalSize,Global.charFormat));
	                boolean flag=true;
	                for(int i =0 ;i<sfilecount;i++){
	                	byte[] sfileNameLens=new byte[10];
	                    dis.read(sfileNameLens, 0, 10);
	                    String sfileNameLen=new String(sfileNameLens,Global.charFormat);
	                    //第12个字节后面接着就是文件名称，后面就是文件信息字节流
	                    byte[] sfileNames=new byte[Integer.parseInt(sfileNameLen)];
	                    dis.read(sfileNames, 0, sfileNames.length);
	                    String sfileName=new String(sfileNames,Global.charFormat);
	                    byte[] sfileLens=new byte[10];
	                    dis.read(sfileLens, 0, 10);
	                    long sfileLen=Long.parseLong(new String(sfileLens,Global.charFormat));
	                    File fff=new File(savePath+File.separator+sfileName.substring(0, sfileName.indexOf(File.separator)));
	                	if(flag&&fff.exists()){
	                		DataOprService.getInstance().clearData(model,no-1,Global.receiveTabFresh,"本地已存在");
	                		return;
	                	}
	                    File ff=new File(savePath+File.separator+sfileName.substring(0, sfileName.lastIndexOf(File.separator)));
	                	if(!ff.exists()){
	                		ff.mkdirs();
	                		flag=false;
	                	}
	                	File f=new File(savePath+File.separator+sfileName);
		                fos = new FileOutputStream(f);
		                if(sTotalSize==0){
		                	fos.flush();
		                    DataOprService.getInstance().freshData(model,no-1,Global.receiveTabFresh,"100.00%");
		                }
		                inputByte = new byte[Global.defaultFresh];
		                DecimalFormat df = new DecimalFormat("0.00");  
		                int len=sfileLen<=inputByte.length?(int)sfileLen:inputByte.length;
		                long lened=0;
		                while ((length = dis.read(inputByte, 0, len)) > 0) {
		                    fos.write(inputByte, 0, length);
		                    fos.flush();
		                    sended+=length;
		                    lened+=length;
		                    if(sfileLen-lened<=length){
		                    	len=Integer.parseInt(String.valueOf(sfileLen-lened));
		                    }
		                    DataOprService.getInstance().freshData(model,no-1,Global.receiveTabFresh,df.format(sended/sTotalSize*100)+"%");
		                }
		                if (fos != null)
		                    fos.close();
	                }
            	}else{
                	byte[] fileTypes=new byte[1];
                    dis.read(fileTypes, 0, 1);
	            	byte[] fileNameLens=new byte[10];
	                dis.read(fileNameLens, 0, 10);
	                long totalSize=Long.parseLong(new String(fileNameLens,Global.charFormat));
	                File dir=new File(savePath);
	                if(!dir.exists())
	                	dir.mkdir();
	                File f=new File(savePath+File.separator+fileName);
	                if(f.exists()){
	                	DataOprService.getInstance().clearData(model,no-1,Global.receiveTabFresh,"本地已存在");
	                	return;
	                }
	                fos = new FileOutputStream(f);
	                if(totalSize==0){
	                	fos.flush();
	                    DataOprService.getInstance().freshData(model,no-1,Global.receiveTabFresh,"100.00%");
	                }
	                inputByte = new byte[Global.defaultFresh];
	                DecimalFormat df = new DecimalFormat("0.00");  
	                while ((length = dis.read(inputByte, 0, inputByte.length)) > 0) {
	                    fos.write(inputByte, 0, length);
	                    fos.flush();
	                    sended+=length;
	                    DataOprService.getInstance().freshData(model,no-1,Global.receiveTabFresh,df.format(sended/totalSize*100)+"%");
	                }
            	}
            } finally {
                if (fos != null)
                    fos.close();
                if (dis != null)
                    dis.close();
                if (dos != null)
                	dos.close();
                if (socket != null)
                    socket.close();
            }
        }catch (ConnectException e) {
        	e.printStackTrace();
        	DataOprService.getInstance().clearData(model,no-1,Global.receiveTabFresh,"服务器连接失败");
        }catch (SocketException e) {
        	e.printStackTrace();
        	DataOprService.getInstance().clearData(model,no-1,Global.receiveTabFresh,"失败");
        }catch (Exception e) {
        	e.printStackTrace();
        	JOptionPane.showMessageDialog(frame, filePath+"下载失败："+e.fillInStackTrace().getMessage(), "警告",JOptionPane.WARNING_MESSAGE);
        }
	}
	/**
	 * @Description
	 * 客户端查询文件
	 * 输入socket信息流：前1个字节是操作指令
	 * 输出socket信息流：前4个字段是文件个数，后10个字节是文件长度，后面接着文件绝对路径名称，多个文件，依此循环拼接
	 * @Author qiang.zhu
	 * @param model
	 */
	public void checkFile(final DefaultTableModel model){
        Socket socket = null;
        DataOutputStream dos = null;
        DataInputStream dis = null;
        FileOutputStream fos = null;
        try {
            try {
            	socket = new Socket();
                socket.connect(new InetSocketAddress(ip,port),
                               3 * 1000);
            	dos = new DataOutputStream(socket.getOutputStream());
            	dos.write(Global.checkFiles.getBytes(Global.charFormat),0,1);
            	dos.flush();
            	dis = new DataInputStream(socket.getInputStream());
                byte[] fileLens=new byte[4];
                dis.read(fileLens, 0, 4);
                String fileLen=new String(fileLens,Global.charFormat);
                for(int i=0;i<Integer.parseInt(fileLen);i++){
            		byte[] fileTypes=new byte[1];
                    dis.read(fileTypes, 0, 1);
                    String fileType=new String(fileTypes,Global.charFormat);
            		byte[] fileNameLens=new byte[10];
                    dis.read(fileNameLens, 0, 10);
                    String fileNameLen=new String(fileNameLens,Global.charFormat);
                    byte[] fileNames=new byte[Integer.parseInt(fileNameLen)];
                    dis.read(fileNames, 0, fileNames.length);
                    String fileName=new String(fileNames,Global.charFormat);
	            	ReceiveTableModel rtm=new ReceiveTableModel();
	            	rtm.setFileBox(true);
	            	rtm.setFilePath(fileName);
	            	String[] temp=fileName.split("\\".equals(File.separator)?"\\\\":File.separator);
	            	rtm.setFileName(temp[temp.length-1]);
	            	rtm.setFileType(fileType);
	            	DataOprService.getInstance().insertRowForReceiveTable(model, rtm);
                }
            } finally {
                if (fos != null)
                    fos.close();
                if (dis != null)
                    dis.close();
                if (dos != null)
                	dos.close();
                if (socket != null)
                    socket.close();
            }
        }catch (Exception e) {
        	e.printStackTrace();
        	JOptionPane.showMessageDialog(frame, e.fillInStackTrace().getMessage(), "警告",JOptionPane.WARNING_MESSAGE);
        }
	}
}
