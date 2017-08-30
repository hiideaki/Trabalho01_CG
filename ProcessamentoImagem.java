import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;

public class ProcessamentoImagem extends JFrame implements ActionListener {
	BufferedImage imgInput, Img1, Img2;
	JLabel lblImg1, lblImg2;
	JComboBox filtersList;
	JButton btnConfirmar = new JButton("Confirmar");
	JPanel pnlImg = new JPanel();
	JPanel pnlLista = new JPanel();
	JPanel pnlConfig = new JPanel();
	
	
	ProcessamentoImagem() {
		super("Processamento de Imagens");
		
		String[] filters = {"Grayscale", "Negativo", "Pixelate", "Wave", "Focus", "Contour"};
		filtersList = new JComboBox<String>(filters);
		filtersList.setSelectedItem("Grayscale");
		try {
	        imgInput = ImageIO.read(new File("images/vitor.jpg"));
	        Img1 = new BufferedImage(500, 500, imgInput.getType());
	        Graphics2D g2d = Img1.createGraphics();
	        g2d.drawImage(imgInput, 0, 0, 500, 500, null);
	        g2d.dispose();
		    Img2 = grayscale(Img1);
	    } catch(Exception e) {
	    	e.printStackTrace();
	    }
		lblImg1 = new JLabel(new ImageIcon(Img1));
		lblImg2 = new JLabel(new ImageIcon(Img2));
		
		pnlImg.add(lblImg1, BorderLayout.WEST);
		pnlImg.add(lblImg2, BorderLayout.EAST);
		pnlLista.setLayout(new GridLayout(2, 0, 10, 10));
		pnlLista.add(filtersList);
		pnlLista.add(btnConfirmar);
		pnlLista.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		add(pnlImg, BorderLayout.NORTH);
		add(pnlLista, BorderLayout.WEST);
	
		btnConfirmar.addActionListener(this);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		int chosenFilter = filtersList.getSelectedIndex();
		switch(chosenFilter) {
			case 0:
				Img2 = grayscale(Img1);
				break;
			case 1:
				Img2 = negativo(Img1);
				break;
			case 2:
				Img2 = pixelate(Img1);
				break;
			case 3:
				Img2 = wave(Img1);
				break;
			case 4:
				Img2 = focus(Img1);
				break;
			case 5:
				Img2 = contour(Img1);
				break;
		}
		lblImg2.setIcon(new ImageIcon(Img2));
	}
	
	BufferedImage grayscale(BufferedImage img) {
		int col, row, rgb, red, green, blue, med;
		row = img.getHeight();
		col = img.getWidth();
		BufferedImage ret = new BufferedImage(col, row, BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < row - 1; i++) {
			for(int j = 0; j < col - 1; j++) {
				rgb = img.getRGB(i, j);
			    red = (rgb >> 16) & 0xff;
			    green = (rgb >> 8) & 0xff;
			    blue = (rgb) & 0xff;
			    med = (red + green + blue) / 3;
				ret.setRGB(i, j, med * 65536 + med * 256 + med);			
			}
		}
		return ret;
	}
	
	BufferedImage negativo(BufferedImage img) {
		int col, row, rgb, red, green, blue, med;
		row = img.getHeight();
		col = img.getWidth();
		BufferedImage ret = new BufferedImage(col, row, BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < row; i++) {
			for(int j = 0; j < col; j++) {
				rgb = img.getRGB(i, j);
			    red = 255 - (rgb >> 16) & 0xff;
			    green = 255 - (rgb >> 8) & 0xff;
			    blue = 255 - (rgb) & 0xff;
			    med = (red + green + blue) / 3;
				ret.setRGB(i, j, med * 65536 + med * 256 + med);			
			}
		}
		return ret;
	}
	
	BufferedImage pixelate(BufferedImage img) {
		int col, row, rgb, red, green, blue, somar, somag, somab;
		int pix = 5; //tamanho dos blocos
		col = img.getWidth() / pix;
		row = img.getHeight() / pix;
		BufferedImage ret = new BufferedImage(col * pix, row * pix, BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < row; i ++) {
			for(int j = 0; j < col; j ++) {
				somar = somag = somab = 0;
				for(int c1 = 0; c1 < pix; c1++) {
					for(int c2 = 0; c2 < pix; c2++) {
						rgb = img.getRGB(i * pix + c1, j * pix + c2);
					    red = (rgb >> 16) & 0xff;
					    green = (rgb >> 8) & 0xff;
					    blue = (rgb) & 0xff;
					    somar += red;
					    somag += green;
					    somab += blue;
					}
				}
				somar /= pix * pix;
				somag /= pix * pix;
				somab /= pix * pix;
				for(int c1 = 0; c1 < pix; c1++) {
					for(int c2 = 0; c2 < pix; c2++) {
						ret.setRGB(i * pix + c1, j * pix + c2, somar * 65536 + somag * 256 + somab);		
					}
				}
			}
		}
		return ret;
	}
	
	BufferedImage wave(BufferedImage img) {
		int col, row, rgb, red, green, blue, med, ctrl;
		row = img.getHeight();
		col = img.getWidth();
		BufferedImage ret = new BufferedImage(col, row, BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < row; i++) {
			for(int j = 0; j < col; j++) {
				rgb = img.getRGB(i, j);
				blue = (rgb) & 0xff;
				rgb -= blue;
				blue *= 1.5;
				if(blue > 255) blue = 255;
				rgb += blue;
				ret.setRGB(i, j, rgb);
			}
		}
		ctrl = 1;
		double offset = 0;
		double aux;
		int dg = 10; //quantos graus "anda" por "i"
		int dt = 10; //distorção
		for(int i = 0; i < row; i++) {
			aux = Math.sin(dg * i * Math.PI / 180);
			if(aux >= 0) {
				for(int j = col - 1; j > (int)(aux * dt); j--) {
					rgb = ret.getRGB(j - (int)(aux * dt), i);
					ret.setRGB(j, i, rgb);
				}
			} else {
				for(int j = 0; j < col + (int)(aux * dt); j++) {
					rgb = ret.getRGB(j - (int)(aux * dt), i);
					ret.setRGB(j, i, rgb);
				}
			}
		}
		return ret;
	}
	
	int dist(int x1, int y1, int x2, int y2) {
		return (int)Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}
	
	BufferedImage focus(BufferedImage img) {
		int col, row, rgb, red, green, blue, med;
		double t, tmax;
		row = img.getHeight();
		col = img.getWidth();
		tmax = dist(row / 2, col / 2, 0, 0);
		BufferedImage ret = new BufferedImage(col, row, BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < row - 1; i++) {
			for(int j = 0; j < col - 1; j++) {
				t = Math.abs((tmax - dist(i, j, row / 2, col / 2)) / tmax);
				rgb = img.getRGB(i, j);
			    red = (int)Math.floor(t * ((rgb >> 16) & 0xff));
			    green = (int)Math.floor(t * ((rgb >> 8) & 0xff));
			    blue = (int)Math.floor(t * ((rgb) & 0xff));
			    
			    if(red > 255) red = 255;
			    if(green > 255) green = 255;
			    if(blue > 255) blue = 255;
			    
			    med = (red + green + blue) / 3;
				ret.setRGB(i, j, red * 65536 + green * 256 + blue);			
			}
		}
		return ret;
	}
	
	BufferedImage contour(BufferedImage img) {
		int col, row, rgb, red, green, blue, med;
		row = img.getHeight();
		col = img.getWidth();
		BufferedImage ret = new BufferedImage(col, row, BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < row; i++) {
			for(int j = 0; j < col; j++) {
				rgb = img.getRGB(i, j);

				red = (rgb >> 16) & 0xff;
			    green = (rgb >> 8) & 0xff;
			    blue = (rgb) & 0xff;
			    med = (red + green + blue) / 3;
			    if(med < 126) {
			    	red = 0;
			    	green = 0;
			    	blue = 0;
			    } else {
			    	red = 255;
			    	green = 255;
			    	blue = 255;
			    }
			    rgb = red * 65536 + green * 256 + blue;
		    	ret.setRGB(i, j, rgb);
			    if(rgb == 0 && j >= 3) {
			    	ret.setRGB(i, j - 3, 0xffffff);
			    }
			}
		}
		
		for(int j = 2; j < col; j++) {
			for(int i = 2; i < row; i++) {
				rgb = ret.getRGB(i, j);
				green = (rgb >> 16) & 0xff;
				if(green == 0) {
					ret.setRGB(i - 1, j, 0);
				}
			}
		}
		return ret;
	}
	


	public static void main(String[] args) {
		new ProcessamentoImagem();
	}
}
