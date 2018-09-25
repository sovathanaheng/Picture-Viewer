import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;




public class Viewer extends JPanel {
	
	private final int LABEL_WIDTH = 1200;
	private final int LABEL_HEIGHT = 800;

	private final int MIN_DELAY_TIME = 0;
	private final int MAX_DELAY_TIME = 10;
	private final int DEFAULT_DELAY_TIME = 1;
	
	private final String[] EXTENSIONS = {".jpg", ".JPG"};
	
	private JPanel center;	
	private JPanel east;
	private JPanel west;
	private JPanel south;
	
	private JLabel picLabel;
	private JSlider delaySlider;
	private JCheckBox autoplayCheckBox;
	private JButton previousButton;
	private JButton nextButton;
	private JButton browse;
	private JScrollPane scroll;
	private Timer timer;
	
	private int count;
	private List<File> fileList; 

	
	
	
	public Viewer(){
		setLayout(new BorderLayout());
		createCheckBox();
		createSlider();
		createButtons();
		createPicLabel();
		createScrollPane();
		createPanel();
		createTimer();	
		getFileList();
		printImage();
	}
	
	
	private void createPanel(){
		createCenterPanel();
		createEastPanel();
		createWestPanel();
		createSouthPanel();
		
		add(center, BorderLayout.CENTER);
		add(east, BorderLayout.EAST);
		add(west, BorderLayout.WEST);
		add(south, BorderLayout.SOUTH);
	}
	
	private void createCenterPanel(){
		center = new JPanel();
		center.setLayout(new BorderLayout());
		center.add(scroll, BorderLayout.CENTER);
	}
	
	private void createSouthPanel(){
		south = new JPanel();	
		south.add(browse);
		south.add(autoplayCheckBox);
		south.add(delaySlider);
		south.add(new JLabel("sec"));		
	}
	
	private void createEastPanel(){
		east = new JPanel();
		east.setLayout(new BorderLayout());
		east.setPreferredSize(new Dimension(80,50));
		east.add(nextButton, BorderLayout.CENTER);
	}
	
	private void createWestPanel(){
		west = new JPanel();
		west.setLayout(new BorderLayout());
		west.setPreferredSize(new Dimension(80,50));
		west.add(previousButton, BorderLayout.CENTER);
	}
	
	public void createButtons(){	
		nextButton = new JButton(">>");
		nextButton.addActionListener(buttonListener);
		previousButton = new JButton("<<");
		previousButton.addActionListener(buttonListener);
		browse = new JButton("Browse");
		browse.addActionListener(browseListener);	
	}
	
	private void createPicLabel(){
		picLabel = new JLabel();
		picLabel.setPreferredSize(new Dimension(1200,800));
		picLabel.setHorizontalAlignment(JLabel.CENTER);
	}
	
	private void createScrollPane(){
		scroll = new JScrollPane(picLabel);
	}
	
	private void createSlider(){
		delaySlider = new JSlider(MIN_DELAY_TIME, MAX_DELAY_TIME,
				DEFAULT_DELAY_TIME);
		delaySlider.setMajorTickSpacing(MAX_DELAY_TIME/2);
		delaySlider.setMinorTickSpacing(1);
		delaySlider.setPaintTicks(true);
		delaySlider.createStandardLabels(5);
		delaySlider.setPaintLabels(true);
		delaySlider.addChangeListener(sliderListener);
	}
	
	private void createCheckBox(){
		//initiate checkbox for start/stop slide show
		autoplayCheckBox = new JCheckBox("Autoplay");
		autoplayCheckBox.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				if(autoplayCheckBox.isSelected()){
					timer.start(); //start slide show
				}
				else{
					timer.stop(); //stop slide show
				}
			}
		});
	}
	
	private void createTimer(){
		//create timer for slider show
		timer = new Timer(DEFAULT_DELAY_TIME * 1000, new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				//show next image every delay time interval
				count++;	
				printImage();
			}
		});
	}
	
	private void getFileList(){
		fileList = new ArrayList<>();
		
		//set default image folder directory to read
		String path = System.getProperty("user.home") + "/Pictures";
		path = "/Users/sovathana/Desktop/Picture";
		System.out.println(path);
		File directory = new File(path);
		
		//set filter for image file extension
		FilenameFilter filter = new FilenameFilter(){
			@Override
	        public boolean accept(File directory, String name) {
				for(String ext : EXTENSIONS){
					if (name.endsWith(ext)) {
		                return true;
		            }
				}
	            return false;
	        }
	    };
	    
	    //get path of all images
		for(File img : directory.listFiles(filter)){
			fileList.add(img);
		}	
	}
	
	private void printImage(){
		try{				
			setLabelImage(LABEL_WIDTH, LABEL_HEIGHT);
		}
		catch (IOException e1){
			e1.printStackTrace();
		}	
	}	

	private void setLabelImage(int width, int height)throws IOException{ 
		
		//keep track of the position of image being showed
		if(count >= fileList.size()){
			count = 0;
		}
		if(count < 0){
			count = fileList.size() - 1;
		}
		
		//read image
		Image img = ImageIO.read(fileList.get(0));
		
		
		//get ratio of original image's dimension
		double imageWidth = img.getWidth(this);
		double imageHeight = img.getHeight(this);
		imageWidth = height * (imageWidth/imageHeight);
		
		//print image with the height of label
		//and width that is proportional to the ratio of image
		picLabel.setIcon(new ImageIcon(img.getScaledInstance((int)imageWidth,
				height, Image.SCALE_DEFAULT)));
	}

	
	ActionListener buttonListener = new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e){
			if (e.getSource() == nextButton){
				count++; //move to next image
			}
			else if (e.getSource() == previousButton){
				count--; //move to previous image
			}
			printImage();
		}
	};
	
	ChangeListener sliderListener = new ChangeListener(){
		@Override
		public void stateChanged(ChangeEvent e){
			//update delay time every time slider is changed
			//delay time unit is in millisecond
			timer.setDelay(delaySlider.getValue() * 1000);	
		}
	};
		
	
	
	ActionListener browseListener = new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e){
							
			JFileChooser file = new JFileChooser();
            //set default browse directory to the same default folder
			file.setCurrentDirectory(new File(System.getProperty("user.home") + "/Pictures"));
            
            //filter the files
            FileNameExtensionFilter filter = new FileNameExtensionFilter("jpg file", "jpg");
            file.addChoosableFileFilter(filter);
            
            //show browse dialog 
            int result = file.showOpenDialog(null);
            
             //if the user click on open in Jfilechooser
            if(result == JFileChooser.APPROVE_OPTION){
                File selectedFile = file.getSelectedFile();
                String path = selectedFile.getAbsolutePath();
                //show selected image in the position after the current image
                count++;
                fileList.add(count, new File(path));
                printImage();
            }
		}
	};
	
	
	
	
	
	public static void main(String[] args){
		JFrame frame = new JFrame ("Picture Viewer");
		frame.add(new Viewer());
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);	
	}
}
