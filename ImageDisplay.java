import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import java.util.ArrayList;

public class ImageDisplay {

	JFrame frame;
	JLabel lbIm1;
	BufferedImage imgOne;
	BufferedImage imgTwo;
	BufferedImage processImg;
	int width = 640; // default image width and height
	int height = 480;
	ArrayList<BufferedImage> imgs = new ArrayList<BufferedImage>();
	ArrayList<BufferedImage> bgImgs = new ArrayList<BufferedImage>();
	/** Read Image RGB
	 *  Reads the image of given width and height at the given imgPath into the provided BufferedImage.
	 */
	private void readImageRGB(int width, int height, String imgPath, BufferedImage img)
	{
		try
		{
			int frameLength = width*height*3;

			File file = new File(imgPath);
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			raf.seek(0);

			long len = frameLength;
			byte[] bytes = new byte[(int) len];

			raf.read(bytes);

			int ind = 0;
			for(int y = 0; y < height; y++)
			{
				for(int x = 0; x < width; x++)
				{
					byte a = 0;
					byte r = bytes[ind];
					byte g = bytes[ind+height*width];
					byte b = bytes[ind+height*width*2];

					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					//int pix = ((a << 24) + (r << 16) + (g << 8) + b);
					img.setRGB(x,y,pix);
					ind++;
				}
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public BufferedImage transformation(BufferedImage origin, BufferedImage backGround)
	{
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		int width = origin.getWidth(), height = origin.getHeight();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int originRGB = origin.getRGB(i,j);
				if(isGreen(originRGB)){

					int newRGB = backGround.getRGB(i, j);
					img.setRGB(i, j, newRGB);
					//img.setRGB(i, j, new Color(255,255,255,255).getRGB());
				}
				else{
					img.setRGB(i, j, originRGB);
				}
//				Color c = new Color(originRGB);
//				float[] hsv = new float[3];
//				float low = (float)70/360;
//				float high = (float)170/360;
//				Color.RGBtoHSB(c.getRed(),c.getGreen(),c.getBlue(),hsv);//h 0-1 ---> 0-360
//				if((hsv[0] >= low && hsv[0] <= high) && (hsv[1]*100 >= 30 && hsv[1]*100 <255) &&
//						(hsv[2]*100 >= 30 && hsv[2]*100 < 255)){
//					//hsv[1] = 0;
//
//					//originRGB = Color.HSBtoRGB(hsv[0],hsv[1],hsv[2]);
//					//img.setRGB(i, j, originRGB);
//				}else{
//					float[] neighbors = calculateRGB(origin, i, j);
//					float r = neighbors[0], g = neighbors[1], b = neighbors[2], alpha = neighbors[3];
//					int foreColor = (Math.round(alpha) << 24) | (Math.round(r) << 16)
//							| (Math.round(g) << 8) | Math.round(b);
//					int backColor = backGround.getRGB(i, j);
//					int finalColor = Math.round(alpha*foreColor+(1-alpha)*backColor);
//					img.setRGB(i, j, finalColor);
//				}
			}
		}
		return img;
	}



	public boolean[][] detectMotion(BufferedImage img1, BufferedImage img2){
		int width = img1.getWidth(), height = img1.getHeight();
		boolean[][] moving = new boolean[width][height];
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				int preRGB = img1.getRGB(i, j); int currRGB = img2.getRGB(i, j);
				float r1, g1, b1, r2, g2, b2;
				r1 = (preRGB >> 16) & 0xff; g1 = (preRGB >> 8) & 0xff; b1 = (preRGB >> 0) & 0xff;
				r2 = (currRGB >> 16) & 0xff; g2 = (currRGB >> 8) & 0xff; b2 = (currRGB >> 0) & 0xff;
				float[] preHSV = new float[3]; float[] currHSV = new float[3];
				Color c1 = new Color(preRGB), c2 = new Color(currRGB);
				Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), preHSV);
				Color.RGBtoHSB(c2.getRed(), c2.getGreen(), c2.getBlue(), currHSV);
				float[] pre = calculateRGB(img1, i, j);
				float[] curr = calculateRGB(img2, i, j);
				if((Math.abs(pre[0]-curr[0])>4 && Math.abs(pre[1]-curr[1])>4 && Math.abs(pre[2]-curr[2])>4)
						&& Math.abs(preHSV[2]-currHSV[2]) >= 0.009
						&& Math.abs(preHSV[1]-currHSV[1]) >= 0.009){
					moving[i][j] = true;
				}
// 				if((r1!=r2 || g1!=g2 || b1!=b2)
//						&& Math.abs(preHSV[2]-currHSV[2]) >= 0.01
//						&& Math.abs(preHSV[1]-currHSV[1]) >= 0.01){
//					moving[i][j] = true;
//				}
//				float[] preHSV = new float[3]; float[] currHSV = new float[3];
//				Color c1 = new Color(preRGB), c2 = new Color(currRGB);
//				Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), preHSV);
//				Color.RGBtoHSB(c2.getRed(), c2.getGreen(), c2.getBlue(), currHSV);
//				if(Math.abs(preHSV[0]-currHSV[0]) >= 0.01 && Math.abs(preHSV[1]-currHSV[1])>=0.003
//						&& Math.abs(preHSV[2]-currHSV[2])>=0.003){
//					moving[i][j] = true;
//				}
			}
		}
		return moving;
	}

	public boolean isGreen(int rgb){
		Color c = new Color(rgb);
		float[] hsv = new float[3];
		float low = (float)70/360;
		float high = (float)170/360;
		Color.RGBtoHSB(c.getRed(),c.getGreen(),c.getBlue(),hsv);//h 0-1 ---> 0-360
		if((hsv[0] >= low && hsv[0] <= high) && (hsv[1]*100 >= 30 && hsv[1]*100 <255) &&
				(hsv[2]*100 >= 30 && hsv[2]*100 < 255)) return true;
		return false;
	}

	public float[] calculateRGB(BufferedImage img, int x, int y){
		float[] res = new float[3];
		int pix = img.getRGB(x, y);
		float r = (pix>>16)&0xff, g = (pix>>8)&0xff, b = (pix>>0)&0xff;
		//top left
		if(x-1 >= 0 && y-1 >= 0) {pix = img.getRGB(x-1, y-1);}
		else if(x-1 >= 0 && y-1 < 0){pix = img.getRGB(x-1, y+1);}
		else if(x-1 < 0 && y-1 >= 0){pix = img.getRGB(x+1, y-1);}
		else {pix = img.getRGB(x+1, y+1);}
		r += (pix >> 16) & 0xff; g += (pix >> 8) & 0xff; b += (pix >> 0) & 0xff;
		//top
		if(x-1>=0){pix = img.getRGB(x-1, y);}
		else {pix = img.getRGB(x+1, y);}
		r += (pix >> 16) & 0xff; g += (pix >> 8) & 0xff; b += (pix >> 0) & 0xff;
		//top right
		if(x-1 >= 0 && y+1 < height){pix = img.getRGB(x-1, y+1);}
		else if(x-1<0 && y+1 < height){pix = img.getRGB(x+1, y+1);}
		else if(x-1>=0 && y+1 >= height){pix = img.getRGB(x-1, y-1);}
		else{pix = img.getRGB(x+1, y-1);}
		r += (pix >> 16) & 0xff; g += (pix >> 8) & 0xff; b += (pix >> 0) & 0xff;
		//left
		if(y-1>=0) {pix = img.getRGB(x, y-1);}
		else{pix = img.getRGB(x, y+1);}
		r += (pix >> 16) & 0xff; g += (pix >> 8) & 0xff; b += (pix >> 0) & 0xff;
		//right
		if(y+1<height){pix = img.getRGB(x,y+1);}
		else{pix = img.getRGB(x, y-1);}
		r += (pix >> 16) & 0xff; g += (pix >> 8) & 0xff; b += (pix >> 0) & 0xff;
		//down left
		if(x+1 < width && y-1 >= 0) {pix = img.getRGB(x+1, y-1);}
		else if(x+1 >= width && y-1 >= 0){pix = img.getRGB(x-1, y-1);}
		else if(x+1 < width && y-1 <0) {pix = img.getRGB(x+1, y+1);}
		else {pix = img.getRGB(x-1, y+1);}
		r += (pix >> 16) & 0xff; g += (pix >> 8) & 0xff; b += (pix >> 0) & 0xff;
		//down
		if(x+1 < width){pix = img.getRGB(x+1, y);}
		else {pix = img.getRGB(x-1, y);}
		r += (pix >> 16) & 0xff; g += (pix >> 8) & 0xff; b += (pix >> 0) & 0xff;
		//down right
		if(x+1 < width && y+1 < height){pix = img.getRGB(x+1, y+1);}
		else if(x+1 >= width && y+1 < height){pix = img.getRGB(x-1, y+1);}
		else if(x+1 < width && y+1 >= height){pix = img.getRGB(x+1, y-1);}
		else{pix = img.getRGB(x-1, y-1);}
		r += (pix >> 16) & 0xff; g += (pix >> 8) & 0xff; b += (pix >> 0) & 0xff;
		r = r/9; g = g/9; b = b/9;
		res[0] = r;
		res[1] = g;
		res[2] = b;
		return res;
	}

	public void avgBoolean(boolean[][] res){
		int row = res.length, col = res[0].length;
		for(int x = 0; x < row; x++){
			for(int y = 0; y < col; y++){
				boolean mid, topL, topR, L, R, downL, downR, up, down;
				mid = res[x][y];
				//top Left
				if(x-1 >= 0 && y-1 >= 0) {topL = res[x-1][y-1];}
				else if(x-1 >= 0 && y-1 < 0){topL = res[x-1][y+1];}
				else if(x-1 < 0 && y-1 >= 0){topL = res[x+1][y-1];}
				else {topL = res[x+1][y+1];}
				//top
				if(x-1>=0){up = res[x-1][y];}
				else {up = res[x+1][y];}
				//top right
				if(x-1 >= 0 && y+1 < height){topR = res[x-1][y+1];}
				else if(x-1<0 && y+1 < height){topR = res[x+1][y+1];}
				else if(x-1>=0 && y+1 >= height){topR = res[x-1][y-1];}
				else{topR = res[x+1][y-1];}
				//left
				if(y-1>=0) {L = res[x][y-1];}
				else{L = res[x][y+1];}
				//right
				if(y+1<height){R = res[x][y+1];}
				else{R = res[x][y-1];}
				//down left
				if(x+1 < width && y-1 >= 0) {downL = res[x+1][y-1];}
				else if(x+1 >= width && y-1 >= 0){downL = res[x-1][y-1];}
				else if(x+1 < width && y-1 <0) {downL = res[x+1][y+1];}
				else {downL = res[x-1][y+1];}
				//down
				if(x+1 < width){down = res[x+1][y];}
				else {down = res[x-1][y];}
				//down right
				if(x+1 < width && y+1 < height){downR = res[x+1][y+1];}
				else if(x+1 >= width && y+1 < height){downR = res[x-1][y+1];}
				else if(x+1 < width && y+1 >= height){downR = res[x+1][y-1];}
				else{downR = res[x-1][y-1];}
				int cnt = 0;
				if(mid) cnt++;
				if(topL) cnt++; if(up) cnt++; if(topR) cnt++;
				if(L) cnt++; if(R) cnt++; if(downL) cnt++;
				if(downR) cnt++; if(down) cnt++;
				if(cnt >= 4) res[x][y] = true;
				else res[x][y] = false;
			}
		}
	}

	public BufferedImage changeToGreen(BufferedImage img, boolean[][] moving){
		BufferedImage resImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		int width = img.getWidth(), height = img.getHeight();
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				if(moving[i][j] == false){
					int rgb = 0xff000000 | (0 << 16) | (255 << 8) | 0;
					resImg.setRGB(i, j, rgb);
				}else{
					int rgb = img.getRGB(i, j);
					resImg.setRGB(i, j, rgb);
				}
			}
		}
		return resImg;
	}


	public void showIms(String[] args){

		// Read a parameter from command line
		//String param1 = args[1];
		//System.out.println("The second parameter was: " + param1);

		String dirName1 = args[0];
		File dir1 = new File(dirName1);
		String dirName2 = args[1];
		File dir2 = new File(dirName2);

		int mode = Integer.parseInt(args[2]);

		for(File f : dir2.listFiles()){
			imgTwo = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			String fileName = dirName2 + "/" + f.getName();
			readImageRGB(width, height, fileName, imgTwo);
			bgImgs.add(imgTwo);
		}
		for(File f : dir1.listFiles()){
			imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			String fileName = dirName1 + "/" + f.getName();
			readImageRGB(width, height, fileName, imgOne);
			imgs.add(imgOne);
		}
		if(mode == 1){
			for(int i = 0; i < imgs.size(); i++){
				processImg = transformation(imgs.get(i), bgImgs.get(i));
				//res.add(processImg);
				bgImgs.set(i, processImg);
			}
		}
		else if(mode == 0){
			boolean[][] reference = null;
			if(imgs.size()%4 == 0){
				for(int i = 3; i < imgs.size(); i+=4){
					boolean[][] res = detectMotion(imgs.get(i-3), imgs.get(i));
					avgBoolean(res);
					for(int j = i-3; j <= i; j++){
						processImg = changeToGreen(imgs.get(j), res);
						processImg = transformation(processImg, bgImgs.get(j));
						bgImgs.set(j, processImg);
					}
				}
			}else{
				int leftover = imgs.size()%4;
				int j = 0;
				for(int i = 3; i < imgs.size()-leftover; i+=4){
					boolean[][] res = detectMotion(imgs.get(i-3), imgs.get(i));
					avgBoolean(res);
					for(j = i-3; j <= i; j++){
						processImg = changeToGreen(imgs.get(j), res);
						processImg = transformation(processImg, bgImgs.get(j));
						bgImgs.set(j, processImg);
					}
				}
				for(; j < imgs.size(); j++){
					boolean[][] res = detectMotion(imgs.get(j-3), imgs.get(j));
					avgBoolean(res);
					processImg = changeToGreen(imgs.get(j), res);
					processImg = transformation(processImg, bgImgs.get(j));
					bgImgs.set(j, processImg);
				}
			}
		}

		frame = new JFrame();
		lbIm1 = new JLabel(new ImageIcon(imgs.get(0)));
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().add(lbIm1, BorderLayout.CENTER);
		//frame.add(lbIm1, c);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		for(int i = 1; i < bgImgs.size(); i++){
			lbIm1.setIcon(new ImageIcon(bgImgs.get(i)));
			try {
				if (i == bgImgs.size()-1){
					i=0;
				}
				Thread.sleep(1000/24);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

	}

	public static void main(String[] args) {
		ImageDisplay ren = new ImageDisplay();
		ren.showIms(args);
	}

}

