import java.io.*;
import javax.swing.text.*;
import javax.swing.text.StyleContext.NamedStyle;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import static javax.swing.JFrame.*; //引入JFrame的静态常量
import java.awt.event.*;
import java.awt.*;
import java.util.*;
public class ListJFrame extends JFrame {
	int x,y;
	ImageLabel bg;
	JButton add_song;
	JList<String> song_list;
	private MyExtendsJFrame mainframe;
	JFileChooser chooser;
	public ListJFrame(final MyExtendsJFrame mainframe)
	{
		this.mainframe = mainframe;
		setUndecorated(true);  //关闭边框
		setBackground(new Color(0,0,0,0));
		setTitle("Song List");
		setBounds(1105,375,400,610);
		setLayout(null);
		setVisible(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		//setAlwaysOnTop(true);
		init();
	}
	void init()
	{
		chooser = new JFileChooser(new File("./"));//文件选择器
		
		bg = new ImageLabel(new ImageIcon("./locals/background.png"));
		bg.setBounds(0, 0, 544, 510);
		bg.setAlpha(0.9f);
	    getLayeredPane().add(bg, new Integer(Integer.MIN_VALUE));//背景图片控件置于最底层
		((JPanel)getContentPane()).setOpaque(false); //控件透明
		
		song_list = new JList<String>();
		String[] arr = mainframe.songs.toArray(new String[mainframe.songs.size()]);
		song_list.setListData(arr);
		song_list.setVisibleRowCount(10);
		song_list.setPreferredSize(new Dimension(100, 100));
		song_list.setBounds(50, 50, 300, 400);
		song_list.setFont(new Font("楷体", Font.PLAIN, 26));
		song_list.setOpaque(false);
		song_list.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if(e.getClickCount()==2){	//When double click JList
					int ans = song_list.getSelectedIndex();//得到定位的是哪个歌
					if(mainframe.path.size()>0) {
						mainframe.music_player.start(mainframe.path.get(ans));
						mainframe.b_play.setIcon(new ImageIcon("./locals/stop.png"));
						mainframe.Playingmusic_index = ans;
						mainframe.Playinglyrics_index=-1;
						mainframe.MusicTime=mainframe.getMusicTime(mainframe.path.get(ans));
						mainframe.LyricsFile_pre.setText("");
    			        mainframe.LyricsFile_now.setText("");
    			        mainframe.LyricsFile_next.setText("");
				        mainframe.LoadLyrics(mainframe.path.get(ans).replaceAll(".wav", ".txt"));
				        mainframe.name.setText(mainframe.songs.get(ans).replaceAll(".wav", ""));
				        int tmp = mainframe.MusicTime/1000;
				        
				        mainframe.progress_button.setMaximum(tmp);
				        
				        mainframe.time.setText(tmp/60+":"+tmp%60);
				        mainframe.cover.setIcon(new ImageIcon(mainframe.path.get(ans).replaceAll(".wav", ".jpg")));
				        //System.out.println(mainframe.cover.getIcon().toString());
				        mainframe.timerFun();
					}
				}
			}
		});
		song_list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jlist, Object o, int i, boolean bln, boolean bln1) {
                Component listCellRendererComponent = super.getListCellRendererComponent(jlist, o, i, bln, bln1);
                JLabel label=(JLabel) listCellRendererComponent;
                label.setOpaque(false);
                return label;
            }
        });
		
		getLayeredPane().add(song_list);
		
		add_song = new JButton(new ImageIcon("./locals/add_song.png"));
		add_song.setBounds(175,425,50,50);
		add_song.setOpaque(false);
		add_song.setMargin(new Insets(0,0,0,0));
		add_song.setBorderPainted(false);//不打印边框  
		add_song.setBorder(null);//除去边框  
		add_song.setText(null);//除去按钮的默认名称  
		add_song.setFocusPainted(false);//除去焦点的框  
		add_song.setContentAreaFilled(false);//除去默认的背景填充
		add_song.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				setAlwaysOnTop(false);
			    chooser.showDialog(new JLabel(), "选择");
			    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			    
			    File file=chooser.getSelectedFile();//文件选择
			    try {
				   //System.out.println("文件:"+file.getAbsolutePath());
				    if(chooser.getSelectedFile().getName().endsWith(".wav")) {
				    	mainframe.path.add(file.getAbsolutePath());
					   	mainframe.songs.add(chooser.getSelectedFile().getName());
					   	String[] arr = mainframe.songs.toArray(new String[mainframe.songs.size()]);
						song_list.setListData(arr);
				    }else {
				    	JOptionPane.showMessageDialog(null, "请选择以.wav格式结尾的文件Orz");  
				    	//System.out.println("文件类型选择错误");
				    }
				}catch(NullPointerException ex) {
			    	//System.out.println("文件未选择");
			    }
				setAlwaysOnTop(true);
	        }
	        public void mouseEntered(MouseEvent e) {
	        	add_song.setIcon(new ImageIcon("./locals/add_song2.png"));
	        }
	        public void mouseExited(MouseEvent e) {
	        	add_song.setIcon(new ImageIcon("./locals/add_song.png"));
            }
		});
		getLayeredPane().add(add_song);
	}
}