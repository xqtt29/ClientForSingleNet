package com.chinacreator.service;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import com.chinacreator.common.Global;
import com.chinacreator.entity.ReceiveTableModel;
import com.chinacreator.entity.SendTableModel;

/**
 * @Description
 * 公共方法服务类
 * 
 * @Author qiang.zhu
 * @Datetime 2016年5月10日 上午10:17:58
 * @Version
 * @Copyright (c) 2013 湖南科创信息技术股份有限公司
 */
public class DataOprService {
	
	private DataOprService(){
		
	}
	
	private static DataOprService refreshDataService;
	
	public static DataOprService getInstance(){
		return refreshDataService==null?new DataOprService():refreshDataService;
	}
	
	/**
	 * @Description
	 * 刷新表控件中的下载进度
	 * @Author qiang.zhu
	 * @param model
	 * @param no
	 * @param column
	 * @param data
	 */
	public void freshData(final DefaultTableModel model,final int no,final int column,final String data){
		new Thread(new Runnable() {
		    public void run() {
		    	if(!"100.00%".equals(model.getValueAt(no, column))&&!"失败".equals(model.getValueAt(no, column))){
			    	model.setValueAt(data, no, column);
		    	}
		    }
		 }).start();
	}
	/**
	 * @Description
	 * 清除表控件中的数据
	 * @Author qiang.zhu
	 * @param model
	 * @param no
	 * @param column
	 * @param data
	 */
	public void clearData(final DefaultTableModel model,final int no,final int column,final String data){
		new Thread(new Runnable() {
		    public void run() {
			    model.setValueAt(data, no, column);
		    }
		 }).start();
	}
	/**
	 * @Description
	 * 勾选表控件中的数据
	 * @Author qiang.zhu
	 * @param model
	 * @param no
	 * @param column
	 * @param data
	 */
	public void selectData(final DefaultTableModel model,final int no,final int column,final boolean data){
		new Thread(new Runnable() {
		    public void run() {
			    model.setValueAt(data, no, column);
		    }
		 }).start();
	}
	/**
	 * @Description
	 * 获取定长数据(不足的以0补齐)
	 * @Author qiang.zhu
	 * @param data
	 * @param size
	 */
	public String initData(int data,int size){
		StringBuffer sb=new StringBuffer();
		int len=size-String.valueOf(data).length();
		for(int i=0;i<len;i++){
			sb.append("0");
		}
		sb.append(data);
		return sb.toString();
	}
	/**
	 * @Description
	 * 获取配置文件
	 * @Author qiang.zhu
	 * @param data
	 * @param size
	 */
	public Map<String, String> getProp(){
    	Map<String,String> m = new HashMap<String,String>();
		try{
	    	Properties p = new Properties();
	    	try {
	    		InputStreamReader isr = new InputStreamReader(new FileInputStream(System.getProperty("user.dir")+File.separator + "conf.properties"), "gbk");
	    		p.load(isr);
	    		for (Iterator localIterator = p.keySet().iterator(); localIterator.hasNext(); ) { 
	    			Object temp = localIterator.next();
	    			m.put((String)temp, p.getProperty((String)temp).trim());
	    		}
	    		isr.close();
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
		}catch(Exception e){
			
		}
    	return m;
    }
	/**
	 * @Description
	 * 查询表控件中是否已存在重复的文件
	 * @Author qiang.zhu
	 * @param model
	 * @param filePath
	 */
	public boolean findSameFile(DefaultTableModel model,String filePath){
    	boolean flag=false;
    	int totalRow=model.getRowCount();
    	for(int i=0;i<totalRow;i++){
    		if(filePath.equals(model.getValueAt(i, 3).toString())){
    			flag=true;
    			break;
    		}
    	}
    	return flag;
    }
	/**
	 * @Description
	 * 检查端口和IP
	 * @Author qiang.zhu
	 * @param model
	 * @param filePath
	 */
	public boolean checkIpAndPort(JFrame frame,String ip,String port){
		if(ip.length()==0){
			JOptionPane.showMessageDialog(frame, "服务器IP为空！", "警告",JOptionPane.WARNING_MESSAGE);
			return false;
		}else{
			try{
				String[] ips=ip.split("\\.");
				boolean flag=true;
				if(Integer.parseInt(ips[0])==0||ips.length!=4){
					flag=false;
				}else{
					for(String temp : ips){
						if(temp.length()==0||Integer.parseInt(temp)<0||Integer.parseInt(temp)>255){
							flag=false;
							break;
						}
					}
				}
				if(!flag){
					JOptionPane.showMessageDialog(frame, "IP地址错误！", "警告",JOptionPane.WARNING_MESSAGE);
					return false;
				}
			}catch(Exception ex){
				ex.printStackTrace();
				JOptionPane.showMessageDialog(frame, "IP地址不合法！", "警告",JOptionPane.WARNING_MESSAGE);
				return false;
			}
		}
		try{
			if(Integer.parseInt(port)>65535){
				JOptionPane.showMessageDialog(frame, "端口错误！", "警告",JOptionPane.WARNING_MESSAGE);
				return false;
			}
		}catch(Exception ex){
			ex.printStackTrace();
			JOptionPane.showMessageDialog(frame, "端口不合法！", "警告",JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
	}

	/**
	 * @Description
	 * 获取文件(重名的加上后缀(*))暂未完成
	 * @Author qiang.zhu
	 * @param model
	 * @param filePath
	 */
	public File getFile(String filePath){
		File f=new File(filePath);
		if(f.exists()){
			int len=filePath.lastIndexOf(".");
			f=getFile(filePath.substring(0, len)+"(1)"+filePath.substring(len));
		}
		return f;
	}
	/**
	 * @Description
	 * 往发送文件列表中插入文件信息
	 * @Author qiang.zhu
	 * @param model
	 * @param data
	 */
	public void insertRowForSendTable(DefaultTableModel model,SendTableModel data){
		int j=model.getRowCount();
		model.addRow(new Object[5]);
		model.setValueAt(j+1, j, 0);
		model.setValueAt(data.getFilePath(), j, 1);
		model.setValueAt(data.getFileName(), j, 3);
		model.setValueAt(data.getFileType(), j, 4);
	}

	/**
	 * @Description
	 * 绑定发送文件列表到对象
	 * @Author qiang.zhu
	 * @param model
	 * @return List
	 */
	public List<SendTableModel> bindSendData(DefaultTableModel model){
		List<SendTableModel> list=new ArrayList<SendTableModel>();
		for(int i=0;i<model.getRowCount();i++){
			SendTableModel rtm=new SendTableModel();
			rtm.setFileNo(model.getValueAt(i, 1)==null?"":model.getValueAt(i, 0).toString());
			rtm.setFilePath(model.getValueAt(i, 1)==null?"":model.getValueAt(i, 1).toString());
			rtm.setFileName(model.getValueAt(i, 1)==null?"":model.getValueAt(i, 3).toString());
			rtm.setFileType(model.getValueAt(i, 1)==null?"":model.getValueAt(i, 4).toString());
			list.add(rtm);
		}
		return list;
	}

	/**
	 * @Description
	 * 往接收文件列表中插入文件信息
	 * @Author qiang.zhu
	 * @param model
	 * @param data
	 */
	public void insertRowForReceiveTable(DefaultTableModel model,ReceiveTableModel data){
		int j=model.getRowCount();
		model.addRow(new Object[6]);
		model.setValueAt(data.getFileBox(), j, 0);
		model.setValueAt(j+1, j, 1);
		model.setValueAt(data.getFilePath(), j, 2);
		model.setValueAt(data.getFileName(), j, 4);
		model.setValueAt(data.getFileType(), j, 5);
	}
	/**
	 * @Description
	 * 绑定接收文件列表到对象
	 * @Author qiang.zhu
	 * @param model
	 * @return List
	 */
	public List<ReceiveTableModel> bindReceiveData(DefaultTableModel model){
		List<ReceiveTableModel> list=new ArrayList<ReceiveTableModel>();
		for(int i=0;i<model.getRowCount();i++){
			ReceiveTableModel rtm=new ReceiveTableModel();
			rtm.setFileBox((Boolean)model.getValueAt(i, 0));
			rtm.setFileNo(model.getValueAt(i, 1)==null?"":model.getValueAt(i, 1).toString());
			rtm.setFilePath(model.getValueAt(i, 1)==null?"":model.getValueAt(i, 2).toString());
			rtm.setFileName(model.getValueAt(i, 1)==null?"":model.getValueAt(i, 4).toString());
			rtm.setFileType(model.getValueAt(i, 1)==null?"":model.getValueAt(i, 5).toString());
			list.add(rtm);
		}
		return list;
	}
	/**
	 * @Description
	 * 获取粘贴板内容
	 * @Author qiang.zhu
	 * @param model
	 * @return List
	 */
	public Map<String,Object> getClipboardInfo(){
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable t=cb.getContents(null);
			if(t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
				List<File> obj=(List)t.getTransferData(DataFlavor.javaFileListFlavor);
				map.put("file", obj);
			}else if(t.isDataFlavorSupported(DataFlavor.stringFlavor)){
				map.put("string", t.getTransferData(DataFlavor.stringFlavor));
			}
		}catch(Exception e){
			
		}
		return map;
	}
	/**
	 * @Description
	 * 设置粘贴板内容
	 * @Author qiang.zhu
	 * @param model
	 * @return List
	 */
	public void setClipboardInfo(String type,final Object obj){
		if("file".equals(type)){
			Transferable trans = new Transferable() {  
	            public DataFlavor[] getTransferDataFlavors() {  
	                return new DataFlavor[] { DataFlavor.javaFileListFlavor };  
	            }  
	  
	            public boolean isDataFlavorSupported(DataFlavor flavor) {  
	                return DataFlavor.javaFileListFlavor.equals(flavor);  
	            }  
	  
	            public Object getTransferData(DataFlavor flavor)  
	                    throws UnsupportedFlavorException, IOException {  
	                if (isDataFlavorSupported(flavor))  
	                    return obj;  
	                throw new UnsupportedFlavorException(flavor);  
	            }  
	  
	        };
	        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
			cb.setContents(trans, null);
		}else if("string".equals(type)){
			Transferable trans = new Transferable() {  
	            public DataFlavor[] getTransferDataFlavors() {  
	                return new DataFlavor[] { DataFlavor.stringFlavor };  
	            }  
	  
	            public boolean isDataFlavorSupported(DataFlavor flavor) {  
	                return DataFlavor.stringFlavor.equals(flavor);  
	            }  
	  
	            public Object getTransferData(DataFlavor flavor)  
	                    throws UnsupportedFlavorException, IOException {  
	                if (isDataFlavorSupported(flavor))  
	                    return obj;  
	                throw new UnsupportedFlavorException(flavor);  
	            }  
	  
	        };
	        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
			cb.setContents(trans, null);
		}
	}
	/**
	 * @Description
	 * 客户端上传字符串
	 * @Author qiang.zhu
	 * @param ip
	 * @param port
	 * @param data
	 */
	public void sendSocketString(String ip,int port,String data){
        Socket socket = null;
        DataOutputStream dos = null;
        FileInputStream fis = null;
        try {
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(ip,port),
                               3 * 1000);
                dos = new DataOutputStream(socket.getOutputStream());
                dos.write(data.getBytes(Global.charFormat), 0, data.getBytes(Global.charFormat).length);
                dos.flush();
            } finally {
                if (dos != null)
                    dos.close();
                if (fis != null)
                    fis.close();
                if (socket != null)
                    socket.close();
            }
        }catch (Exception e) {
        }
	}
	/**
	 * @Description
	 * 客户端上传字符串
	 * @Author qiang.zhu
	 * @param ip
	 * @param port
	 * @param data
	 */
	public void sendSocketString(Socket socket,String data){
        DataOutputStream dos = null;
        try {
            dos = new DataOutputStream(socket.getOutputStream());
            dos.write(data.getBytes(Global.charFormat), 0, data.getBytes(Global.charFormat).length);
            dos.flush();
        }catch (Exception e) {
        }
	}
}
