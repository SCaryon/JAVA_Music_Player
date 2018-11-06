import java.applet.Applet;  
import java.applet.AudioClip; 
import java.net.MalformedURLException;  
import java.net.URL;  
import java.io.*;
import javax.swing.text.*;
import javax.swing.text.StyleContext.NamedStyle;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import static javax.swing.JFrame.*; //引入JFrame的静态常量
import java.awt.event.*;
import java.awt.*;
import java.net.*;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.List;

class MyExtendsJFrame extends JFrame{
	int x,y;
	public static int Playingmusic_index=-1;		//现在正在播放的音乐的编号
	public static int Playinglyrics_index=-1;		//现在正在播放的歌词编号

	String play_hash = new ImageIcon("./locals/play.png").toString();
	String play2_hash = new ImageIcon("./locals/play2.png").toString();
	String stop_hash = new ImageIcon("./locals/stop.png").toString();
	String stop2_hash = new ImageIcon("./locals/stop2.png").toString();
	String pre_hash = new ImageIcon("./locals/pre.png").toString();
	String next_hash = new ImageIcon("./locals/next.png").toString();
	String end_hash = new ImageIcon("./locals/end.png").toString();
	String minial_hash = new ImageIcon("./locals/mininal.png").toString();
	JLabel bg,title,plate,name,time,now,cover,speaker;
	JButton b_play,b_next,b_pre,end,minial,song_list;

	Boolean open,have_Lyrics;
	ListJFrame list;//播放列表的界面
	//JList listLyricsFile;//歌词列表控件
	JTextPane LyricsFile_pre,LyricsFile_now,LyricsFile_next;
	Timer PlayTimer;//进度条定时器对象
	Timer LyricsTimer;//歌词定时器对象
	int MusicTime;//记录当前播放的歌曲的时间（单位：毫秒）
	public ArrayList<Lyrics> lyricslist=new ArrayList<Lyrics>();
	Box play,progress,change_sound,cmd;
	JSlider progress_button,sound;  // 实现进度条
	public List<String> songs,path;
	public audioplay music_player;
	public MyExtendsJFrame() {
		open = false;
		setUndecorated(true);  //关闭边框
		setBackground(new Color(0,0,0,0));
		setTitle("SCaryon's music player");
		setBounds(500,200,1044,810);
		setLayout(null);
		init();
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	int getMusicTime(String dir)
	{
		return (int)(1.0*(new File(dir)).length()/1024/173*1000);
	}
	void LoadLyrics(String path)	//装载对应歌曲的歌词文件
	{
		//System.out.println(path);
		lyricslist=new ArrayList<Lyrics>();
		try{ // 建立一个对象，它把文件内容转成计算机能读懂的语言
			InputStreamReader reader=new InputStreamReader(new FileInputStream(path),"utf-8");
			BufferedReader br=new BufferedReader(reader);
			have_Lyrics=true;
			String line;
	        while ((line=br.readLine())!=null) 
	        {
	        	int tmp=line.indexOf('#');
	        	lyricslist.add(new Lyrics(line.substring(0, tmp).trim()+"\n",Integer.valueOf(line.substring(tmp+1))));
	        }
	        for(int i=0;i<Math.min(8, lyricslist.size());++i) {
				String temp = LyricsFile_next.getText();
				temp += lyricslist.get(i).toString();
				LyricsFile_next.setText(temp);
	        }
	    }
		catch(IOException e) 
		{
			have_Lyrics=false;
	    }
	}
	public  void timerFun()
	{//定时器函数
		if(PlayTimer!=null){PlayTimer.cancel();}//已经有定时器则关闭
		PlayTimer = new Timer();//创建定时器
		PlayTimer.schedule(new TimerTask(){  //匿名类
        	int nPlayTime=0;  
            public void run() { //定时器函数体
            	if(b_play.getIcon().toString()==stop2_hash || b_play.getIcon().toString()==stop_hash ) nPlayTime+=1;
            	progress_button.setValue(nPlayTime);
            }
        },0,1000);
		
		if(LyricsTimer!=null){LyricsTimer.cancel();}//已经有定时器则关闭
		LyricsTimer = new Timer();//创建定时器
		LyricsTimer.schedule(new TimerTask(){  //匿名类
        	int nPlayTime=0;  
            public void run() { //定时器函数体
            	if(b_play.getIcon().toString()==stop_hash || b_play.getIcon().toString()==stop2_hash) 
            		nPlayTime+=1;
            	if(have_Lyrics==true&&nPlayTime>=lyricslist.get(Playinglyrics_index+1).time )
            	{
        	        SimpleAttributeSet attrset = new SimpleAttributeSet();
        	        StyleConstants.setFontSize(attrset,24);
        	        if(Playinglyrics_index>=0)
        	        	LyricsFile_pre.setText(lyricslist.get(Playinglyrics_index).toString());
        	        else
        	        	LyricsFile_pre.setText("");
        	        LyricsFile_now.setText(lyricslist.get(Playinglyrics_index+1).toString());
        	        LyricsFile_next.setText("");
        	        for(int i=Playinglyrics_index+2;i<Math.min(Playinglyrics_index+11, lyricslist.size());++i) {
        	        	String temp = LyricsFile_next.getText();
        				temp += lyricslist.get(i).toString();
        				LyricsFile_next.setText(temp);
        	        }
            		Playinglyrics_index+=1;
            	}
            }
        },0,1000);
    }
	void init()
	{
		songs =  new ArrayList<>();
		path = new ArrayList<>();
		music_player = new audioplay();
		list = new ListJFrame(this);
		//实现窗体拖动
		addMouseListener(new MouseAdapter() {
			
	        public void mousePressed(MouseEvent e) {
	        	list.setAlwaysOnTop(true);
	            x = e.getX();
	            y = e.getY();
	        }
		});
		addMouseMotionListener(new MouseMotionAdapter() {
	        public void mouseDragged(MouseEvent e) {
	                int fx=getLocation().x + e.getX() - x;
	                int fy=getLocation().y + e.getY() - y;
	                int px = list.getLocation().x + e.getX() - x;
	                int py = list.getLocation().y + e.getY() - y;
	                
	                list.setLocation(px, py);
	                list.setAlwaysOnTop(false);
	                setLocation(fx, fy);
	        }
		});
		song_list = new JButton(new ImageIcon("./locals/song_list.png"));
		song_list.setBounds(940, 685, 50, 50);
		song_list.setOpaque(false);
		song_list.setMargin(new Insets(0,0,0,0));
		song_list.setBorderPainted(false);//不打印边框  
		song_list.setBorder(null);//除去边框  
		song_list.setText(null);//除去按钮的默认名称  
		song_list.setFocusPainted(false);//除去焦点的框  
		song_list.setContentAreaFilled(false);//除去默认的背景填充
		song_list.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if(open == false) {
					list.setVisible(true);
					list.setExtendedState(NORMAL);
					//	getContentPane().add(list);
					open = true;
				}else {
					list.setVisible(false);
					open = false;
				}
	        }
	        public void mouseEntered(MouseEvent e) {
	        	song_list.setIcon(new ImageIcon("./locals/song_list2.png"));
	        }
	        public void mouseExited(MouseEvent e) {
	        	song_list.setIcon(new ImageIcon("./locals/song_list.png"));
            }
		});
		add(song_list);
		bg = new JLabel(new ImageIcon("./locals/background2.png"));
		bg.setBounds(0,0,1026,768);//设置背景控件大小
	     getLayeredPane().add(bg, new Integer(Integer.MIN_VALUE));//背景图片控件置于最底层
		((JPanel)getContentPane()).setOpaque(false); //控件透明
		
		title = new JLabel("SCaryon's music player");
		title.setFont(new Font("微软雅黑",Font.BOLD,25));
		title.setBounds(30, 15, 500,60);
		title.setForeground(Color.WHITE);
		add(title);
		
		cover  = new JLabel(new ImageIcon("./locals/default.jpg"));
		cover.setBounds(182, 282, 200, 200);
		add(cover);
		
		plate = new JLabel(new ImageIcon("./locals/plate.png"));
		plate.setBounds(100, 175, 400, 400);
		getLayeredPane().add(plate,new Integer(Integer.MAX_VALUE));
		
		name = new JLabel("右下角添加歌曲");
		name.setFont(new Font("微软雅黑",Font.BOLD,34));
		name.setForeground(Color.black);
		name.setBounds(640, 100,500, 100);
		add(name);

	    
	    LyricsFile_pre = new JTextPane();
	    LyricsFile_pre.setBounds(640, 230,400, 50);
	    LyricsFile_pre.setOpaque(false);
	    LyricsFile_pre.setEditable(false);
	    LyricsFile_pre.setFont(new Font("楷体",Font.PLAIN,24));
	    
	    LyricsFile_now = new JTextPane();
	    LyricsFile_now.setBounds(640, 280,400, 50);
	    LyricsFile_now.setOpaque(false);
	    LyricsFile_now.setFont(new Font("楷体",Font.BOLD,26));	
	    LyricsFile_now.setForeground(new Color(238,99,99));
	    LyricsFile_now.setEditable(false);
	    
	    LyricsFile_next = new JTextPane();
	    LyricsFile_next.setBounds(640, 330,400, 300);
	    LyricsFile_next.setOpaque(false);
	    LyricsFile_next.setFont(new Font("楷体",Font.PLAIN,24));
	    LyricsFile_next.setEditable(false);
	    
	    add(LyricsFile_pre);
	    add(LyricsFile_now);
	    add(LyricsFile_next);
	    
	    init_basicmd();
	    init_progress();
	    init_play_button();
	    init_change_sound();
	}
	void init_basicmd()
	{
		end = new JButton(new ImageIcon("./locals/end.png"));
		end.setOpaque(false);
		end.setMargin(new Insets(0,0,0,0));
		end.setBorderPainted(false);//不打印边框  
		end.setBorder(null);//除去边框  
		end.setText(null);//除去按钮的默认名称  
		end.setFocusPainted(false);//除去焦点的框  
		end.setContentAreaFilled(false);//除去默认的背景填充
		end.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				list.dispose();
				dispose();
				System.exit(0);
	        }
	        public void mouseEntered(MouseEvent e) {
        		end.setIcon(new ImageIcon("./locals/end2.png"));
      
	        }
	        public void mouseExited(MouseEvent e) {
	        	end.setIcon(new ImageIcon("./locals/end.png"));
            }
		});
		minial = new JButton(new ImageIcon("./locals/mininal.png"));
		minial.setOpaque(false);
		minial.setMargin(new Insets(0,0,0,0));
		minial.setBorderPainted(false);//不打印边框  
		minial.setBorder(null);//除去边框  
		minial.setText(null);//除去按钮的默认名称  
		minial.setFocusPainted(false);//除去焦点的框  
		minial.setContentAreaFilled(false);//除去默认的背景填充
		minial.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				setExtendedState(ICONIFIED);
				list.setExtendedState(ICONIFIED);
	        }
	        public void mouseEntered(MouseEvent e) {
        		minial.setIcon(new ImageIcon("./locals/mininal2.png"));
      
	        }
	        public void mouseExited(MouseEvent e) {
	        	minial.setIcon(new ImageIcon("./locals/mininal.png"));
            }
		});
		cmd = Box.createHorizontalBox();
		cmd.add(minial);
		cmd.add(Box.createHorizontalStrut(20));
		cmd.add(end);
		cmd.setBounds(860, 20, 120, 55);
		add(cmd);
	}
	void init_progress()
	{
	    time = new JLabel("inf");
	    time.setFont(new Font("微软雅黑",Font.PLAIN,15));
	    time.setForeground(Color.black);    
	    now = new JLabel("0:0:00");
	    now.setFont(new Font("微软雅黑",Font.PLAIN,15));
	    now.setForeground(Color.black);
	   
	    progress_button = new JSlider(0,100);
	    progress_button.setOpaque(false);
	    progress_button.setValue(0);
	    progress_button.setForeground(Color.red);
	    progress_button.setUI(new MySliderUI2(progress_button));
	    progress_button.addChangeListener(new ChangeListener() { 
            @Override
            public void stateChanged(ChangeEvent e) {
            	int it = progress_button.getValue();
            	int min = it/60;
            	int sec = it % 60;
            	String res = String.valueOf(min)+":"+String.valueOf(sec); 
            	now.setText(res);
            }
        });
	    
	    // 拖动进度条的实现
	    progress = Box.createHorizontalBox();
	    progress.setBounds(250, 700,500, 20);
	    progress.add(now);
	    progress.add(Box.createHorizontalStrut(10));
	    progress.add(progress_button);
	    progress.add(Box.createHorizontalStrut(10));
	    progress.add(time);

	    add(progress);
	}
	void init_change_sound()
	{
	    speaker = new JLabel(new ImageIcon("./locals/speaker.png"));
	    
	    sound = new JSlider(0,100);
	    sound.setUI(new MySliderUI(sound));
	    sound.setOpaque(false);
	    sound.setValue(20);
	    sound.setForeground(Color.red);
	    sound.addChangeListener(new ChangeListener() { 
            @Override
            public void stateChanged(ChangeEvent e) {
            	//实现系统音量的加减
            }
        });
	    change_sound = Box.createHorizontalBox();
	    change_sound.setBounds(770,700, 160, 18);
	    change_sound.add(speaker);
	    change_sound.add(Box.createHorizontalStrut(5));
	    change_sound.add(sound);
	    add(change_sound);
	    
	}
	void init_play_button()
	{
		b_play = new JButton();
		b_play.setMargin(new Insets(0,0,0,0));
        b_play.setBorderPainted(false);//不打印边框  
        b_play.setBorder(null);//除去边框  
        b_play.setText(null);//除去按钮的默认名称  
        b_play.setFocusPainted(false);//除去焦点的框  
        b_play.setContentAreaFilled(false);//除去默认的背景填充
		b_play.setIcon(new ImageIcon("./locals/play.png"));
		b_play.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				String now = b_play.getIcon().toString();
        		if(now == play_hash || now == play2_hash) {
        			b_play.setIcon(new ImageIcon("./locals/stop.png"));
        			if(music_player.Get_Path() != "null") {
        				music_player.continues();
        			}else {
        				if(path.size()!=0) {
        					//int maxx = path.size();
        					Playinglyrics_index=-1;
        					Playingmusic_index = 0;
        					MusicTime=getMusicTime(path.get(Playingmusic_index));
        					music_player.start(path.get(0));
        			        int tmp = MusicTime/1000;
        			        time.setText(tmp/60+":"+tmp%60);
        			        time.setText(tmp/60+":"+tmp%60);
        			        
        			        progress_button.setMaximum(tmp);
        			        
        			        LyricsFile_pre.setText("");
        			        LyricsFile_now.setText("");
        			        LyricsFile_next.setText("");
        			        LoadLyrics(path.get(Playingmusic_index).replaceAll(".wav", ".txt"));
        			        name.setText(songs.get(Playingmusic_index).replaceAll(".wav", ""));
        			        cover.setIcon(new ImageIcon(path.get(Playingmusic_index).replaceAll(".wav", ".jpg")));
        					timerFun();
        					//System.out.println(Playingmusic_index);
        				}
        			}
        		}else if(now == stop_hash || now == stop2_hash) {
        			b_play.setIcon(new ImageIcon("./locals/play.png"));
        			music_player.stop();
        		}
	        }
	        public void mouseEntered(MouseEvent e) {
				String now = b_play.getIcon().toString();
        		if(now == play_hash)
        			b_play.setIcon(new ImageIcon("./locals/play2.png"));
        		else if(now == stop_hash)
        			b_play.setIcon(new ImageIcon("./locals/stop2.png"));
	        }
	        public void mouseExited(MouseEvent e) {
	        	String now = b_play.getIcon().toString();
	        	if(now == play2_hash)
        			b_play.setIcon(new ImageIcon("./locals/play.png"));
        		else if(now == stop2_hash)
        			b_play.setIcon(new ImageIcon("./locals/stop.png"));
            }
		});
		b_next = new JButton();
		b_next.setIcon(new ImageIcon("./locals/next.png"));
		b_next.setMargin(new Insets(0,0,0,0));
        b_next.setBorderPainted(false);//不打印边框  
        b_next.setBorder(null);//除去边框  
        b_next.setText(null);//除去按钮的默认名称  
        b_next.setFocusPainted(false);//除去焦点的框  
        b_next.setContentAreaFilled(false);//除去默认的背景填充
        b_next.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int maxx = path.size();
				if(maxx>0) {
					Playingmusic_index = (Playingmusic_index + 1)%maxx;
					
					MusicTime=getMusicTime(path.get(Playingmusic_index));
			        int tmp = MusicTime/1000;
			        time.setText(tmp/60+":"+tmp%60);
			        time.setText(tmp/60+":"+tmp%60);
			        
			        progress_button.setMaximum(tmp);
			        
			        LyricsFile_pre.setText("");
			        LyricsFile_now.setText("");
			        LyricsFile_next.setText("");
			        
			        LoadLyrics(path.get(Playingmusic_index).replaceAll(".wav", ".txt"));
			        name.setText(songs.get(Playingmusic_index).replaceAll(".wav", ""));
			        cover.setIcon(new ImageIcon(path.get(Playingmusic_index).replaceAll(".wav", ".jpg")));
			        music_player.start(path.get(Playingmusic_index));
					timerFun();
			        String now = b_play.getIcon().toString();
			        if(now == play_hash || now == play2_hash) {
			        	b_play.setIcon(new ImageIcon("./locals/stop.png"));
					}
					//System.out.println(Playingmusic_index);
				}
	        }
	        public void mouseEntered(MouseEvent e) {
        		b_next.setIcon(new ImageIcon("./locals/next2.png"));
      
	        }
	        public void mouseExited(MouseEvent e) {
	        	b_next.setIcon(new ImageIcon("./locals/next.png"));
            }
		});
		b_pre = new JButton();
		b_pre.setIcon(new ImageIcon("./locals/pre.png"));
		b_pre.setMargin(new Insets(0,0,0,0));
        b_pre.setBorderPainted(false);//不打印边框  
        b_pre.setBorder(null);//除去边框  
        b_pre.setText(null);//除去按钮的默认名称  
        b_pre.setFocusPainted(false);//除去焦点的框  
        b_pre.setContentAreaFilled(false);//除去默认的背景填充
        b_pre.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int maxx = path.size();
				if(maxx > 0) {
					if(Playingmusic_index == 0)
						Playingmusic_index = maxx -1;
					else
						Playingmusic_index = Playingmusic_index - 1;
					
					MusicTime=getMusicTime(path.get(Playingmusic_index));
			        int tmp = MusicTime/1000;
			        time.setText(tmp/60+":"+tmp%60);
			        time.setText(tmp/60+":"+tmp%60);
			        
			        progress_button.setMaximum(tmp);
			        
			        LyricsFile_pre.setText("");
			        LyricsFile_now.setText("");
			        LyricsFile_next.setText("");
			        LoadLyrics(path.get(Playingmusic_index).replaceAll(".wav", ".txt"));
			        name.setText(songs.get(Playingmusic_index).replaceAll(".wav", ""));
			        cover.setIcon(new ImageIcon(path.get(Playingmusic_index).replaceAll(".wav", ".jpg")));
			        music_player.start(path.get(Playingmusic_index));
				        timerFun();
			        String now = b_play.getIcon().toString();
			        if(now == play_hash || now == play2_hash) {
			        	b_play.setIcon(new ImageIcon("./locals/stop.png"));
					}
					//System.out.println(Playingmusic_index);
				}
	        }
	        public void mouseEntered(MouseEvent e) {
        		b_pre.setIcon(new ImageIcon("./locals/pre2.png"));
	        }
	        public void mouseExited(MouseEvent e) {
	        	b_pre.setIcon(new ImageIcon("./locals/pre.png"));
            }
		});
		play = Box.createHorizontalBox();
		play.setBounds(40, 610, 500, 200);
		add(play);
		play.add(b_pre);
		play.add(b_play);
		play.add(b_next);
	}
}
public class player {
	public static void main(String args[])
	{
		MyExtendsJFrame f= new MyExtendsJFrame();
	}
}