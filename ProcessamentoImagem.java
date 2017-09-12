import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ProcessamentoImagem extends JFrame implements ActionListener {
	BufferedImage imgInput, Img1, Img2;
	int chosenFilter, pix, dg, dt;
	JLabel lblImg1, lblImg2;
	JComboBox filtersList;
	JPanel pnlImg = new JPanel();
	JPanel pnlBtn = new JPanel();
	JPanel pnlFiltros = new JPanel();
	JLabel lblInfo = new JLabel("<html><p style='font-size: 8px'>As imagens foram redimensionadas para 500x500<br>para preencher melhor a janela</p></html>");
	JButton btnFoto = new JButton("Escolher foto");
	JButton btnSalvar = new JButton("Salvar foto");
	JFileChooser chooser = new JFileChooser();
	JFileChooser saver = new JFileChooser();
	FileNameExtensionFilter filt1 = new FileNameExtensionFilter("JPG & PNG", "jpg", "png");
	FileNameExtensionFilter filt2 = new FileNameExtensionFilter("PNG", "png");
	
	JPanel pnlWave = new JPanel();
	JLabel lblDeg = new JLabel("Frequência");
	JSpinner spnDeg = new JSpinner(new SpinnerNumberModel(10, 1, 20, 1));
	JLabel lblDis = new JLabel("Amplitude");
	JSpinner spnDis = new JSpinner(new SpinnerNumberModel(10, 1, 20, 1));
	
	JPanel pnlPixel = new JPanel();
	JLabel lblPix = new JLabel("Tamanho do pixel");
	JSpinner spnPix = new JSpinner(new SpinnerNumberModel(10, 2, 30, 1));
	
	ProcessamentoImagem() {
		super("Processamento de Imagens");
		
		pix = 10;
		dt = 10;
		dg = 10;
		
		chooser.setFileFilter(filt1);
		saver.setFileFilter(filt2);
		
		pnlBtn.setLayout(new GridLayout(2, 1, 10, 10));
		pnlFiltros.setLayout(new GridLayout(2, 1, 10, 10));
		pnlWave.setLayout(new GridLayout(2, 2, 10, 10));
		pnlPixel.setLayout(new GridLayout(1, 2, 10, 10));
		
		String[] filters = {"Grayscale", "Negativo", "Sepia", "Pixelate", "Circle Pixels", "Wave", "Focus", "Sobel Negativo"};
		filtersList = new JComboBox<String>(filters);
		filtersList.setSelectedItem("Grayscale");
		try {
	        imgInput = ImageIO.read(new File("images/lena.png"));
	        Img1 = new BufferedImage(500, 500, imgInput.getType());
	        Graphics2D g2d = Img1.createGraphics();
	        g2d.drawImage(imgInput, 0, 0, 500, 500, null);
	        g2d.dispose();
	        applyFilter();
	    } catch(Exception e) {
	    	e.printStackTrace();
	    }
		lblImg1 = new JLabel(new ImageIcon(Img1));
		lblImg2 = new JLabel(new ImageIcon(Img2));
		
		pnlImg.setSize(new Dimension(1000, 500));
		pnlImg.add(lblImg1, BorderLayout.WEST);
		pnlImg.add(lblImg2, BorderLayout.EAST);
		
		pnlWave.add(lblDeg);
		pnlWave.add(spnDeg);
		pnlWave.add(lblDis);
		pnlWave.add(spnDis);
		
		pnlPixel.setOpaque(true);
		pnlPixel.add(lblPix);
		pnlPixel.add(spnPix);

		pnlFiltros.setOpaque(true);
		pnlFiltros.add(filtersList);
		pnlFiltros.add(lblInfo);
		
		pnlBtn.add(btnFoto);
		pnlBtn.add(btnSalvar);
		
		pnlFiltros.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		pnlBtn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		pnlWave.setBorder(BorderFactory.createTitledBorder("Wave"));
		pnlPixel.setBorder(BorderFactory.createTitledBorder("Pixel"));

		add(pnlImg, BorderLayout.NORTH);
		add(pnlFiltros, BorderLayout.WEST);
		add(pnlBtn, BorderLayout.EAST);

		btnFoto.addActionListener(this);
		btnSalvar.addActionListener(this);
		filtersList.addActionListener(this);
		spnDeg.addChangeListener(new ChangeListener() {
	        public void stateChanged(ChangeEvent e) {
	        	dg = (int)spnDeg.getValue();
	        	Img2 = wave(Img1);
				lblImg2.setIcon(new ImageIcon(Img2));
	        }
	    });
		spnDis.addChangeListener(new ChangeListener() {
	        public void stateChanged(ChangeEvent e) {
				dt = (int)spnDis.getValue();
	        	Img2 = wave(Img1);
				lblImg2.setIcon(new ImageIcon(Img2));
	        }
	    });
		spnPix.addChangeListener(new ChangeListener() {
	        public void stateChanged(ChangeEvent e) {
				pix = (int)spnPix.getValue();
				if(chosenFilter == 3) {
					Img2 = pixelate(Img1);
				} else if(chosenFilter == 4) {
					Img2 = circlepixels(Img1);
				}
				lblImg2.setIcon(new ImageIcon(Img2));
	        }
	    });
		 
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();

		setVisible(true);
		
	}
	
	public void applyFilter() {
		pix = 10;
		dg = 10;
		dt = 10;
		spnDeg.setValue(10);
		spnDis.setValue(10);
		spnPix.setValue(10);
		remove(pnlPixel);
		remove(pnlWave);
		repaint();
		
		chosenFilter = filtersList.getSelectedIndex();
		switch(chosenFilter) {
			case 0:
				Img2 = grayscale(Img1);
				break;
			case 1:
				Img2 = negativo(Img1);
				break;
			case 2:
				Img2 = sepia(Img1);
				break;
			case 3:
				Img2 = pixelate(Img1);
				add(pnlPixel, BorderLayout.CENTER);
				revalidate();
				break;
			case 4:
				Img2 = circlepixels(Img1);
				add(pnlPixel, BorderLayout.CENTER);
				revalidate();
				break;
			case 5:
				Img2 = wave(Img1);
				add(pnlWave, BorderLayout.CENTER);
				revalidate();
				break;
			case 6:
				Img2 = focus(Img1);
				break;
			case 7:
				Img2 = sobelneg(Img1);
				break;
		}
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == filtersList) {
			applyFilter();
			lblImg2.setIcon(new ImageIcon(Img2));
		} else if(e.getSource() == btnFoto) {
			int returnVal = chooser.showOpenDialog(this);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		       try {
			        imgInput = ImageIO.read(new File(chooser.getSelectedFile().getPath()));
			        Img1 = new BufferedImage(500, 500, imgInput.getType());
			        Graphics2D g2d = Img1.createGraphics();
			        g2d.drawImage(imgInput, 0, 0, 500, 500, null);
			        g2d.dispose();
			        applyFilter();

					lblImg1.setIcon(new ImageIcon(Img1));
					lblImg2.setIcon(new ImageIcon(Img2));
			    } catch(Exception ex) {
			    	ex.printStackTrace();
			    }
		    }	
		} else if(e.getSource() == btnSalvar) {
			int returnVal = saver.showSaveDialog(null);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					File f = saver.getSelectedFile();
					String path = f.getAbsolutePath();
					f = new File(f.toString() + ".png");
					ImageIO.write(Img2, "png", f);
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	int dist(int x1, int y1, int x2, int y2) {
		return (int)Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
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
	
//	outputRed = (inputRed * .393) + (inputGreen *.769) + (inputBlue * .189)
//	outputGreen = (inputRed * .349) + (inputGreen *.686) + (inputBlue * .168)
//	outputBlue = (inputRed * .272) + (inputGreen *.534) + (inputBlue * .131)
	
	BufferedImage sepia(BufferedImage img) {
		int col, row, rgb, red, green, blue, ored, ogreen, oblue;
		row = img.getHeight();
		col = img.getWidth();
		BufferedImage ret = new BufferedImage(col, row, BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < row - 1; i++) {
			for(int j = 0; j < col - 1; j++) {
				rgb = img.getRGB(i, j);
			    red = (rgb >> 16) & 0xff;
			    green = (rgb >> 8) & 0xff;
			    blue = (rgb) & 0xff;
			    ored = (int)(red * 0.393 + green * 0.769 + blue * 0.189);
			    ogreen = (int)(red * 0.349 + green * 0.686 + blue * 0.168);
			    oblue = (int)(red * 0.272 + green * 0.534 + blue * 0.131);
			    if(ored > 255) ored = 255;
			    if(ogreen > 255) ogreen = 255;
			    if(oblue > 255) oblue = 255;
				ret.setRGB(i, j, ored * 65536 + ogreen * 256 + oblue);			
			}
		}
		return ret;
	}
	
	BufferedImage pixelate(BufferedImage img) {
		int col, row, rgb, red, green, blue, somar, somag, somab;
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
	
	BufferedImage circlepixels(BufferedImage img) {
		int col, row, rgb, red, green, blue, somar, somag, somab;
		int d1, d2, d;
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
				d1 = i * pix + pix / 2;
				d2 = j * pix + pix / 2;
				
				for(int c1 = 0; c1 < pix; c1++) {
					for(int c2 = 0; c2 < pix; c2++) {
						if(dist(i * pix + c1, j * pix + c2, d1, d2) < pix / 2)
							ret.setRGB(i * pix + c1, j * pix + c2, somar * 65536 + somag * 256 + somab);
//						else if(dist(i * pix + c1, j * pix + c2, d1, d2) == pix / 2)
//							ret.setRGB(i * pix + c1, j * pix + c2, 0);
						else 
							ret.setRGB(i * pix + c1, j * pix + c2, 0xffffff);
							
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
	
	BufferedImage sobelneg(BufferedImage img) {
		int col, row, rgb, red, green, blue, med;
		BufferedImage ret;
		ret = grayscale(img);
		row = ret.getHeight();
		col = ret.getWidth();
		int[][] rgbm = new int[row][col];
		int[][] sobelX = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
		int[][] sobelY = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};
		double px, py;
		int val;
	
		for(int i = 0; i < row; i++) {
			for(int j = 0; j < col; j++) {
				rgbm[i][j] = ret.getRGB(i, j) & 0xff;
			}
		}
		
		for(int i = 1; i < row - 1; i++) {
			for(int j = 1; j < col - 1; j++) {
				px = sobelX[0][0] * rgbm[i - 1][j - 1] + sobelX[0][1] * rgbm[i][j - 1] + sobelX[0][2] * rgbm[i + 1][j - 1] +
					 sobelX[1][0] * rgbm[i - 1][j    ] + sobelX[1][1] * rgbm[i][j    ] + sobelX[1][2] * rgbm[i + 1][j    ] +
					 sobelX[2][0] * rgbm[i - 1][j + 1] + sobelX[2][1] * rgbm[i][j + 1] + sobelX[2][2] * rgbm[i + 1][j + 1];
				
				py = sobelY[0][0] * rgbm[i - 1][j - 1] + sobelY[0][1] * rgbm[i][j - 1] + sobelY[0][2] * rgbm[i + 1][j - 1] +
					 sobelY[1][0] * rgbm[i - 1][j    ] + sobelY[1][1] * rgbm[i][j    ] + sobelY[1][2] * rgbm[i + 1][j    ] +
					 sobelY[2][0] * rgbm[i - 1][j + 1] + sobelY[2][1] * rgbm[i][j + 1] + sobelY[2][2] * rgbm[i + 1][j + 1];
				val = 255 - (int)Math.sqrt(px * px + py * py);
				ret.setRGB(i, j, val * 65536 + val * 256 + val);
			}
		}
		return ret;
	}

	public static void main(String[] args) {
		new ProcessamentoImagem();
	}
}
