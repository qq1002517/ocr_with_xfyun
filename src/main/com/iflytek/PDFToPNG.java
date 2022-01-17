package main.com.iflytek;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.icepdf.core.pobjects.Document;
import org.icepdf.core.util.GraphicsRenderingHints;

public class PDFToPNG extends JFrame {
	private static final long serialVersionUID = 1L;
	/** 页面控件 */
	// PDF地址输入域
	private static JTextField textFieldPdf;
	// 图片输出地址输入域
	private static JTextField textFieldOut;
	// 进度条
	private static JProgressBar progressBar;
	// 图片输出目录显示
	private static JLabel labelOutShowPath;

	/** 全局变量 */
	// PDF文件绝对路径
	private static String inPath;
	// 图片输出目录
	private static String outPath;

	static {
		// 初始化变量
		outPath = "/Users/admin/Pictures/ncfs1";
	}

	/**
	 * 无参构造方法
	 */
	public PDFToPNG() {
		// 页面控件相关尺寸
		int frameWidth = 550;// 窗口宽度
		int frameHeight = 200;// 窗口高度

		this.setTitle("PDF转图片工具");// 窗口标题
		this.setSize(frameWidth, frameHeight);
		this.setResizable(false);// 设置固定窗口大小
		this.setLocationRelativeTo(null);// 窗口显示到屏幕中央
		this.setLayout(null);// 设置布局为空，即可使用绝对位置

		/** PDF地址 */
		// PDF地址标签
		JLabel labelPdf = new JLabel(" PDF地址：");
		labelPdf.setBounds(10, 10, 70, 25);
		this.add(labelPdf);
		// PDF地址文本输入框
		textFieldPdf = new JTextField();
		textFieldPdf.setBounds(75, 10, 400, 25);
		textFieldPdf.setText(outPath + "\\csmlshy.pdf");// 设置默认目录
		this.add(textFieldPdf);
		// PDF地址浏览按钮
		JButton btnPdf = new JButton("浏览");
		btnPdf.setBounds(475, 10, 60, 25);
		this.add(btnPdf);

		/** 图片输出地址 */
		// 输出地址
		JLabel labelOut = new JLabel("输出地址：");
		labelOut.setBounds(10, 40, 70, 25);
		this.add(labelOut);
		// 输出文本选择框
		textFieldOut = new JTextField();
		textFieldOut.setBounds(75, 40, 400, 25);
		textFieldOut.setText(outPath);
		this.add(textFieldOut);
		// 输出地址浏览按钮
		JButton btnOut = new JButton("浏览");
		btnOut.setBounds(475, 40, 60, 25);
		this.add(btnOut);

		/** 开始转换按钮 */
		JButton btnToPng = new JButton("开始转换");
		btnToPng.setBounds(200, 70, 100, 30);
		this.add(btnToPng);

		/** 进度条 */
		progressBar = new JProgressBar();
		progressBar.setBounds(10, 110, 520, 18);
		progressBar.setIndeterminate(false);// 设置进度条的样式为不确定的进度条样式（进度条来回滚动），false为确定的进度条样式（即进度条从头到尾显示）
		progressBar.setStringPainted(true);// 设置进度条显示提示信息
		progressBar.setString("0/0");// 设置提示信息
		this.add(progressBar);

		/** 输出目录显示 */
		// 输出目录
		JLabel labelOutShow = new JLabel("输出目录：");
		labelOutShow.setBounds(10, 140, 70, 25);
		this.add(labelOutShow);
		// 输出目录标签显示
		labelOutShowPath = new JLabel(outPath);
		labelOutShowPath.setBounds(80, 140, 320, 25);
		this.add(labelOutShowPath);
		// 打开输出目录按钮
		JButton btnOpenDir = new JButton("打开输出目录");
		btnOpenDir.setBounds(410, 140, 120, 22);
		this.add(btnOpenDir);

		/** 按钮点击事件 */
		// PDF地址浏览按钮点击事件
		btnPdf.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openFileWindow(e, textFieldPdf,
						JFileChooser.FILES_AND_DIRECTORIES);
			}
		});
		// 图片地址浏览按钮点击事件
		btnOut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 弹出文件选择框，只能选择目录
				openFileWindow(e, textFieldOut, JFileChooser.DIRECTORIES_ONLY);
			}
		});
		// 开始转换按钮点击事件
		btnToPng.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnToPng_onclick(btnToPng);
			}
		});
		// 打开文件所在位置按钮点击事件
		btnOpenDir.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnOpenDir_onclick();
			}
		});
	}

	/**
	 * 开始转换按钮点击事件
	 */
	protected void btnToPng_onclick(JButton btnToPng) {
		// 校验
		if (!checkAll()) {
			return;
		}
		// 生成转换后的图片目录
		String pdfName = new File(inPath).getName();
		pdfName = pdfName.substring(0, pdfName.length() - 4);// 去掉后缀.pdf
		labelOutShowPath.setText(outPath + "\\" + pdfName);
		// 开始转换PDF
		Thread thread = new Thread(new Progress(progressBar, inPath, outPath,
				btnToPng));
		thread.start();
	}

	/**
	 * 打开文件所在位置按钮点击事件
	 */
	protected void btnOpenDir_onclick() {
		try {
			String dirpath = labelOutShowPath.getText();
			if (new File(dirpath).exists()) {
				final Runtime runtime = Runtime.getRuntime();
				final String cmd = "rundll32 url.dll FileProtocolHandler file://"
						+ dirpath;
				runtime.exec(cmd);
				return;
			}
			JOptionPane.showMessageDialog(null, "目录不存在，请检查！" + dirpath, "警告",
					JOptionPane.ERROR_MESSAGE);

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * 浏览按钮点击事件
	 */
	public void openFileWindow(ActionEvent e, JTextField textField, int mode) {
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF",
				"pdf");// 过滤pdf外的文件
		chooser.setFileFilter(filter);
		chooser.setFileSelectionMode(mode);
		chooser.showDialog(new JLabel(), "选择");
		File file = chooser.getSelectedFile();
		if (file != null) {
			textField.setText(file.getAbsoluteFile().toString());
		}
	}

	/**
	 * 内部类
	 */
	public class Progress extends Thread {

		private JProgressBar bar;
		private JButton btnToPng;
		private String inPath;
		private String outPath;

		public Progress(JProgressBar progressBar, String inPath,
				String outPath, JButton btnToPng) {
			this.bar = progressBar;
			this.btnToPng = btnToPng;
			this.inPath = inPath;
			this.outPath = outPath;
		}

		public void run() {
			btnToPng.setEnabled(false);
			btnToPng.setText("正在转换");
			try {
				Document document = new Document();
				document.setFile(inPath);
				float scale = 2.5f;// 缩放比例
				float rotation = 0f;// 旋转角度
				File pdfFile = new File(inPath);
				String pdfName = pdfFile.getName().substring(0,
						pdfFile.getName().length() - 4);
				File dirFile = new File(outPath + "/" + pdfName);
				if (!dirFile.exists()) {
					dirFile.mkdir();
				} else {
					JOptionPane.showMessageDialog(null, "己存在同名目录，请检查！"
							+ dirFile.getAbsolutePath(), "警告",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				System.out.println("正在转换...");
				int totalPages = document.getNumberOfPages();
				bar.setMaximum(totalPages);
				for (int i = 0; i < totalPages; i++) {
					bar.setValue(i + 1);
					bar.setString((i + 1) + "/" + totalPages);
					BufferedImage image = (BufferedImage) document
							.getPageImage(
									i,
									GraphicsRenderingHints.SCREEN,
									org.icepdf.core.pobjects.Page.BOUNDARY_CROPBOX,
									rotation, scale);
					RenderedImage rendImage = image;
					try {
						File file = new File(dirFile.getAbsolutePath() + "/"
								+ pdfName + "_" + (i + 1) + ".png");
						ImageIO.write(rendImage, "png", file);
					} catch (IOException e) {
						System.out.println(e.getMessage());
					}
					image.flush();
				}
				document.dispose();
				bar.setString("转换完成！");
			} catch (HeadlessException e) {
				System.out.println(e.getMessage());
			} finally {
				btnToPng.setEnabled(true);
				btnToPng.setText("开始转换");
			}
		}

	}

	/**
	 * 校验
	 */
	protected boolean checkAll() {
		// 校验是否选择
		inPath = textFieldPdf.getText().trim();
		outPath = textFieldOut.getText().trim();
		if (inPath.length() == 0) {
			JOptionPane.showMessageDialog(null, "请选择PDF地址！", "警告",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if (outPath.length() == 0) {
			JOptionPane.showMessageDialog(null, "请选择输出地址！", "警告",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		// 校验PDF地址是否正确
		if (!new File(inPath).exists()) {
			JOptionPane.showMessageDialog(null, "选定PDF文件不存在，请重新选择！", "警告",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if (new File(inPath).isDirectory()) {
			JOptionPane.showMessageDialog(null, "PDF地址不是PDF文件，请重新选择！", "警告",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	/**
	 * main方法
	 */
	public static void main(String[] args) {
		PDFToPNG toPNG = new PDFToPNG();
		toPNG.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		toPNG.setVisible(true);

	}

}

