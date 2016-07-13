package com.chinacreator.controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import com.chinacreator.common.Global;
import com.chinacreator.entity.SendTableModel;
import com.chinacreator.service.DataOprService;

/**
 * @Description
 * 客户端入口，界面初始化
 * 
 * @Author qiang.zhu
 * @Datetime 2016年5月10日 上午10:15:51
 * @Version
 * @Copyright (c) 2013 湖南科创信息技术股份有限公司
 */
public class MainAction {
	
    public static void main(String[] args) {
    	creatFrame();
    }
    
    public static void creatFrame(){
    	final JFrame frame=new JFrame(Global.frameName);
    	JPanel panel=new JPanel();
    	final JLabel lab_ip=new JLabel(Global.labIp);
    	final JLabel lab_port=new JLabel(Global.labPort);
    	final JTextField txf_ip=new JTextField(20);
    	txf_ip.setText(Global.defaultIp);
    	final JTextField txf_port=new JTextField(20);
    	txf_port.setText(Global.defaultPort);
    	JButton btn_file=new JButton(Global.btnChooseFile);
    	JButton btn_clearfile=new JButton(Global.btnClearFile);
    	JButton btn_send=new JButton(Global.btnSendFile);
    	JButton btn_sendFast=new JButton(Global.btnSendFileFast);
    	JButton btn_check=new JButton(Global.btnCheckFile);
    	JButton btn_receive=new JButton(Global.btnReceiveFile);
    	panel.add(lab_ip);
    	panel.add(txf_ip);
    	panel.add(lab_port);
    	panel.add(txf_port);
    	panel.setLayout(new GridLayout(1,4));
    	frame.setLayout(new BorderLayout());
    	frame.setSize(800,600);
    	frame.setLocale(Locale.CHINA);
    	frame.add(panel,BorderLayout.NORTH);
    	JPanel westPan=new JPanel();
    	westPan.setLayout(new GridLayout(2,1));
    	JPanel upWestPan=new JPanel();
    	upWestPan.setLayout(new GridLayout(2,1));
    	upWestPan.add(btn_file);
    	upWestPan.add(btn_clearfile);
    	westPan.add(upWestPan);
    	westPan.add(btn_check);
    	frame.add(westPan,BorderLayout.WEST);
    	JPanel eastPan=new JPanel();
    	eastPan.setLayout(new GridLayout(2,1));
    	JPanel upeastPan=new JPanel();
    	upeastPan.setLayout(new GridLayout(2,1));
    	upeastPan.add(btn_send);
    	upeastPan.add(btn_sendFast);
    	eastPan.add(upeastPan);
    	eastPan.add(btn_receive);
    	frame.add(eastPan,BorderLayout.EAST);
    	JPanel southPan=new JPanel();
    	final JLabel lab_download=new JLabel(Global.downLoadPath);
		//lab_download.setForeground(Color.RED);
    	final JTextField txf_download=new JTextField(20);
    	txf_download.setText(Global.defaultSavePath);
    	JButton btn_chooseSavePath=new JButton(Global.btnDownLoadPath);
    	southPan.add(lab_download);
    	southPan.add(txf_download);
    	southPan.add(btn_chooseSavePath);
    	southPan.setLayout(new GridLayout(1,2));
    	frame.add(southPan,BorderLayout.SOUTH);
    	Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
    	Dimension framesize = frame.getSize(); 
    	int x = (int)screensize.getWidth()/2 - (int)framesize.getWidth()/2; 
    	int y = (int)screensize.getHeight()/2 - (int)framesize.getHeight()/2; 
    	frame.setLocation(x,y);
    	frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent arg0) {
				System.exit(0);
			}
    	});
    	
    	JTable sendTab=new JTable();
    	final DefaultTableModel sendModel = new DefaultTableModel(Global.columns,0);
    	sendTab.setModel(sendModel);
    	sendTab.setEnabled(false);
    	sendTab.setRowHeight(18);
    	initColumn(sendTab,Global.colnumsWidth,false);
    	JScrollPane sendJsp=new JScrollPane();
    	sendJsp.setViewportView(sendTab);
    	txf_port.setDragEnabled(true);
    	txf_port.setTransferHandler(new TransferHandler(){
			private static final long serialVersionUID = 1L;
			public boolean importData(JComponent c, Transferable t) {
                try {
                    String filePath = t.getTransferData(DataFlavor.javaFileListFlavor).toString();
                    filePath=filePath.substring(1, filePath.length()-1);
                    String[] paths = filePath.split(", ");
                    for(String path : paths){
	                    SendTableModel stm=new SendTableModel();
	                    stm.setFilePath(path);
	                    File f=new File(path);
	                    if(f.isDirectory()){
	                    	stm.setFileType(Global.isDirectory);
	                    }else{
	                    	stm.setFileType(Global.isFile);
	                    }
	                    String[] temp=path.split("\\".equals(File.separator)?"\\\\":File.separator);
	                    stm.setFileName(temp[temp.length-1]);
	                    DataOprService.getInstance().insertRowForSendTable(sendModel, stm);
                    }
                    return true;
                } catch (UnsupportedFlavorException ufe) {
                    ufe.printStackTrace();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
 
            public boolean canImport(JComponent c, DataFlavor[] flavors) {
                return true;
            }
    	});
    	
    	final DefaultTableModel receiveModel = new DefaultTableModel(Global.columnsWithBox,0);
    	final JTable receiveTab=new JTable(receiveModel){
			private static final long serialVersionUID = 1L;
			public boolean isCellEditable(int row,int column){
    			if(column==0){
    				return true;
    			}
    			return false;
    		}
    	};
    	initColumn(receiveTab,Global.colnumsWidthWithBox,true);
    	receiveTab.setRowHeight(18);
    	TableColumn tc=receiveTab.getColumnModel().getColumn(0);
    	tc.setCellEditor(receiveTab.getDefaultEditor(Boolean.class));
    	tc.setCellRenderer(receiveTab.getDefaultRenderer(Boolean.class));
    	JScrollPane receiveJsp=new JScrollPane();
    	receiveJsp.setViewportView(receiveTab);
    	receiveTab.addMouseListener(new MouseAdapter() {
    		public void mouseClicked(MouseEvent e){
    			if(e.getClickCount()==2){
    				int row=receiveTab.getSelectedRow();
    				if((Boolean)receiveModel.getValueAt(row, 0)){
    					receiveModel.setValueAt(false, row, 0);
    				}else{
    					receiveModel.setValueAt(true, row, 0);
    				}
    			}
    		}
		});
    	receiveTab.getTableHeader().addMouseListener(new MouseAdapter() {
    		public void mouseClicked(MouseEvent e){
    			if(receiveTab.getColumnModel().getColumnIndexAtX(e.getX())==0){
	    			int row=receiveModel.getRowCount();
	    			boolean flag=true;
	    			for(int i=0;i<row;i++){
	    				if(!(Boolean)receiveModel.getValueAt(i, 0)){
	    					flag=false;
	    					break;
	    				}
	    			}
	    			if(flag){
	    				for(int i=0;i<row;i++){
	    					DataOprService.getInstance().selectData(receiveModel, i, 0, false);
	        			}
	    			}else{
	    				for(int i=0;i<row;i++){
	    					DataOprService.getInstance().selectData(receiveModel, i, 0, true);
	        			}
	    			}
    			}
    		}
		});
    	lab_ip.addMouseListener(new MouseAdapter() {
    		public void mouseClicked(MouseEvent e){
    		}
		});
    	lab_port.addMouseListener(new MouseAdapter() {
    		public void mouseClicked(MouseEvent e){
    			Socket socket = new Socket();
    			DataInputStream dis = null;
    			try{
	    			try {
						socket.connect(new InetSocketAddress(txf_ip.getText(),Integer.parseInt(txf_port.getText())),
						    3 * 1000);
		    			DataOprService.getInstance().sendSocketString(socket,Global.getString);
		    			dis = new DataInputStream(socket.getInputStream());
		                //客户端socket信息流中第1到10个字节是上传文件名称的长度
		                byte[] strLens=new byte[10];
		                dis.read(strLens, 0, 10);
		                //int strLen=Integer.parseInt(new String(strLens,Global.charFormat));
		                //第11个字节后面接着就是粘贴板内容
		                ByteArrayOutputStream baos=new ByteArrayOutputStream();
		                byte[] inputByte=new byte[Global.defaultFresh];
		                int length=0;
		                while ((length=dis.read(inputByte, 0, inputByte.length)) > 0) {
		                	baos.write(inputByte,0,length);
		                }
		                DataOprService.getInstance().setClipboardInfo("string", new String(baos.toByteArray(),Global.charFormat));
		                //JOptionPane.showMessageDialog(frame, "接收服务端粘贴板成功！", "警告",JOptionPane.WARNING_MESSAGE);
					}catch (Exception ex) {
						ex.printStackTrace();
					}finally{
						if(dis!=null)
							dis.close();
						if(socket!=null)
							socket.close();
						
					}
    			}catch(Exception ee){
    				
    			}
    		}
		});
    	
    	lab_download.addMouseListener(new MouseAdapter() {
    		SystemClipboardMonitor tmp = new SystemClipboardMonitor(sendModel,txf_ip.getText(),Integer.parseInt(txf_port.getText()));
    		public void mouseClicked(MouseEvent e){
    			if(lab_download.getForeground().getRed()==255){
    				lab_download.setForeground(Color.BLACK);
        	        tmp.stop(); //结束监视
    			}else{
    				lab_download.setForeground(Color.RED);
    				tmp = new SystemClipboardMonitor(sendModel,txf_ip.getText(),Integer.parseInt(txf_port.getText()));
        	        tmp.begin(); //重新监视
    			}
    		}
		});
    	JPanel centerPan=new JPanel();
    	centerPan.setLayout(new GridLayout(2,1));
    	centerPan.add(sendJsp);
    	centerPan.add(receiveJsp);
    	frame.add(centerPan,BorderLayout.CENTER);

    	final JFileChooser addChooser=new JFileChooser();
    	addChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    	addChooser.setMultiSelectionEnabled(true);
    	btn_file.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int returnval=addChooser.showOpenDialog(frame);   
		        if(returnval==JFileChooser.APPROVE_OPTION) 
		        { 
		            File[] files=addChooser.getSelectedFiles(); 
		            for (File file : files) {
		            	if(DataOprService.getInstance().findSameFile(sendModel,file.getName())){
		            		continue;
		            	}
		            	SendTableModel stm=new SendTableModel();
	                    stm.setFilePath(file.getAbsolutePath());
	                    if(file.isDirectory()){
	                    	stm.setFileType(Global.isDirectory);
	                    }else{
	                    	stm.setFileType(Global.isFile);
	                    }
	                    stm.setFileName(file.getName());
	                    DataOprService.getInstance().insertRowForSendTable(sendModel, stm);
		            }
		        } 
			}
    		
    	});
    	
    	final JFileChooser addChooserSavePath=new JFileChooser();
    	addChooserSavePath.setFileSelectionMode(JFileChooser.FILES_ONLY);
    	btn_chooseSavePath.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int returnval=addChooserSavePath.showOpenDialog(frame);   
		        if(returnval==JFileChooser.APPROVE_OPTION) 
		        { 
		        	txf_download.setText(addChooserSavePath.getSelectedFile().getAbsolutePath());
		        } 
			}
    		
    	});
    	btn_clearfile.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sendModel.setRowCount(0);
			}
    		
    	});
    	btn_send.addActionListener(new SendFileAction(frame,txf_ip,txf_port,sendModel));
    	btn_sendFast.addActionListener(new SendFileFastAction(frame,txf_ip,txf_port,sendModel));
    	btn_check.addActionListener(new CheckFileAction(frame,txf_ip,txf_port,receiveModel));
    	btn_receive.addActionListener(new ReceiveFileAction(frame,txf_ip,txf_port,txf_download,receiveModel));
    	frame.setVisible(true);
    }
    
    private static void initColumn(JTable tab,int[] colnumWidth,boolean withBox){
    	TableColumnModel tcm=tab.getColumnModel();
    	for(int i=0;i<colnumWidth.length;i++){
    		TableColumn column = tcm.getColumn(i);  
            column.setPreferredWidth(colnumWidth[i]);
            column.setResizable(true);
            //最后面两个字段隐藏
            if(i>=colnumWidth.length-2){
            	column.setMaxWidth(0);
            	column.setMinWidth(0);
            	column.setPreferredWidth(0);
            }
    	}
    	tab.setColumnModel(tcm);
    	
        TableColumn tableColumn = withBox==true?tab.getColumn(Global.columnsWithBox[3]):tab.getColumn(Global.columns[2]);
        DefaultTableCellRenderer cellRanderer = new DefaultTableCellRenderer();  
        cellRanderer.setForeground(Color.RED);  
        tableColumn.setCellRenderer(cellRanderer); 
    }
}
