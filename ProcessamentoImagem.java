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
	
	ProcessamentoImagem() {
		super("Processamento de Imagens");
		setLayout(new GridLayout(2, 2));
		
		String[] filters = {"Grayscale", "Negativo", "Mosaico", "Contorno"};
		filtersList = new JComboBox<String>(filters);
		filtersList.setSelectedItem("Grayscale");
		try {
	        imgInput = ImageIO.read(new File("images/vitor.jpg"));
	        Img1 = new BufferedImage(500, 500, imgInput.getType());
	        Graphics2D g2d = Img1.createGraphics();
	        g2d.drawImage(imgInput, 0, 0, 500, 500, null);
	        g2d.dispose();
		    Img2 = grayscale(Img1);
		    Img3 = contour(Img1);
		    Img4 = mosaic(Img1);
	    } catch(Exception e) {
	    	e.printStackTrace();
	    }
		lblImg1 = new JLabel(new ImageIcon(Img1));
		lblImg2 = new JLabel(new ImageIcon(Img2));
		
		add(lblImg1);
		add(lblImg2);
		add(filtersList);
		
		add(btnConfirmar);
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
				Img2 = mosaic(Img1);
				break;
			case 3:
				Img2 = contour(Img1);
				break;
		}
		lblImg2.setIcon(new ImageIcon(Img2));
	}
	
	public int sat(int pixel) {
	    int red = (pixel >> 16) & 0xff;
	    int green = (pixel >> 8) & 0xff;
	    int blue = (pixel) & 0xff;
	    int med = (red + green + blue) / 3;
	    if(med < 126) {
	    	red = 0;
	    	green = 0;
	    	blue = 0;
	    } else {
	    	red = 255;
	    	green = 255;
	    	blue = 255;
	    }
	    return red * 65536 + green * 256 + blue;
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
	
	BufferedImage contour(BufferedImage img) {
		int col, row, rgb, red, green, blue, g;
		row = img.getHeight();
		col = img.getWidth();
		BufferedImage ret = new BufferedImage(col, row, BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < row; i++) {
			for(int j = 0; j < col; j++) {
				rgb = img.getRGB(i, j);
			    
			    rgb = sat(rgb);
		    	ret.setRGB(i, j, rgb);
			    if(rgb == 0 && j >= 3) {
			    	ret.setRGB(i, j - 3, 0xffffff);
			    }
			}
		}
		for(int j = 2; j < col; j++) {
			for(int i = 2; i < row; i++) {
				rgb = ret.getRGB(i, j);
				g = (rgb >> 16) & 0xff;
				if(g == 0) {
					ret.setRGB(i - 1, j, 0);
				}
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
	
	BufferedImage mosaic(BufferedImage img) {
		int col, row, rgb, red, green, blue, pix, somar, somag, somab;
		pix = 10;
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

	public static void main(String[] args) {
		new ProcessamentoImagem();
	}
}
