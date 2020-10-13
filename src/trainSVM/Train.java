package trainSVM;

import java.io.File;
import java.lang.reflect.Array;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.Ml;
import org.opencv.ml.SVM;

public class Train {
	
	
	//mat chứa những label và dữ liệu của ký tự số
	private Mat trainingNumberDataMat, numberLabelsMat;
	
	//mat chứ những label và dữ liệu của ký tự chữ
	private Mat trainingWordDataMat, wordLabelsMat;
	
	//SVM để train
	private SVM numberSVM;
	private SVM wordSVM;

	public Train() {
		//load từ svm đã train lên
		numberSVM = SVM.load(System.getProperty("user.dir") + "\\resources\\svm\\number.svm");
		wordSVM = SVM.load(System.getProperty("user.dir") + "\\resources\\svm\\word.svm");
	}

	public void createTrainingData(String path) {
		//chạy từng file trong foder tổng
		File file = new File(path);
		for (int h = 0; h < file.listFiles().length; h++) {
			//load thư viện
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			File fi = file.listFiles()[h];
			
			//hàm tính tổng số hình để train trong từng foder word hoặc number
			int total = getAmountOfFile(fi);
			
			//tạo mảng để chứa label có độ dài bằng số lượng file
			int[] labels = new int[total];
			
			//vị trí hiện tại của mảng label
			int index = 0;
			
			//khởi tạo mảng dữ liệu huân luyện
			float[] trainingData = new float[1];
			
			//độ dài của từng mảng 1 chiều chứa hình
			int length = 0;

			//list foder từng ký tự trong từng foder word hoặc number
			File[] ListCharFoder = fi.listFiles();
			
			
			for (int i = 0; i < ListCharFoder.length; i++) {
				if (ListCharFoder[i].isDirectory()) {
					
					//tên của ký tự
					String charName = ListCharFoder[i].getName();
					
					//danh sách hình hình trong foder của ký tự đó
					File[] listCharImage = ListCharFoder[i].listFiles();
					
					for (int j = 0; j < listCharImage.length; j++) {
						//label vị trí hiện tại bằng tên ký tự
						//chuyển charname từ string về char rồi sang int theo bảng mã ASCII
						labels[index] = charName.toCharArray()[0];

						//image hiện tại
						File image = listCharImage[j];
						
						//mảng f chứa hình image nhị phân đã được chuyển thành mảng 1 chiều
						float[] f = convertImage(image);
						
						if (j == 0 && i == 0) {
							//length là độ dài của từng mảng f chứa hình
							length = f.length;
							
							//mảng training có độ dài bằng tổng độ dài số mảng 1 chiều chứa hình trong foder
							trainingData = new float[f.length * total];
						}
						//copy từ mảng 1 chiều chứa hình hiện tại vào trong trong mảng training
						System.arraycopy(f, 0, trainingData, index * f.length, f.length);
						index++;
					}
				}

			}
			
			//nếu foder là số
			if (h == 0) {
				// mat chứa label để train có số hàng bằng độ dài của mảng label và 1 cột 
				this.numberLabelsMat = new Mat(labels.length, 1, CvType.CV_32SC1);
				//add mảng label vào mat label
				this.numberLabelsMat.put(0, 0, labels);
				
				//mat training có số hàng bằng độ dài của mảng length và số cột bằng length
				this.trainingNumberDataMat = new Mat(labels.length, length, CvType.CV_32FC1);
				//add mảng trainingdata vào mat trainingdata
				this.trainingNumberDataMat.put(0, 0, trainingData);
			} else { //nếu foder là chữ
				
				//trương tự trên
				this.wordLabelsMat = new Mat(labels.length, 1, CvType.CV_32SC1);
				this.wordLabelsMat.put(0, 0, labels);
				
				this.trainingWordDataMat = new Mat(labels.length, length, CvType.CV_32FC1);
				this.trainingWordDataMat.put(0, 0, trainingData);
			}
		}
	}
	public float[] convertImage(File image) {
		//tạo mat từ hình của đường dẫn
		Mat mat = Imgcodecs.imread(image.getPath());
		
		//chuyển kích cỡ về cho phù hợp
		Imgproc.resize(mat, mat, new Size(12, 28));
		
		//chuyển định dạng màu thành xám
		Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);
		
		//chuyển thành mat nhị phân theo ngưỡng 30
		// nếu pixel x >30 => x= 255 (màu trắng) và ngược lại
		Imgproc.threshold(mat, mat, 30, 255, Imgproc.THRESH_BINARY);
		
		//tạo mảng 1 chiều kết quả chứa hình ảnh đã được nhị phân chuyển từ mat 2 chiều thành mảng 1 chiều 
		float[] res = new float[mat.rows() * mat.cols()];
		
		for (int i = 0; i < mat.rows(); i++) {
			for (int j = 0; j < mat.cols(); j++) {
				res[mat.cols() * i + j] = (int) mat.get(i, j)[0];
			}
		}

		return res;
	}

	//xử lý mat trước khi dự đoán chuyển thành nhị phân và chuyển thành 1 chiều
	public Mat preProcessingAMat(Mat mat) {
		Imgproc.resize(mat, mat, new Size(12, 28));
		Imgproc.threshold(mat, mat, 30, 255, Imgproc.THRESH_BINARY);
		float[] f = new float[mat.cols() * mat.rows()];
		for (int i = 0; i < mat.rows(); i++) {
			for (int j = 0; j < mat.cols(); j++) {
				f[mat.cols() * i + j] = (int) mat.get(i, j)[0];
			}
		}
		Mat res = new Mat(1, f.length, CvType.CV_32F);
		res.put(0, 0, f);
		return res;
	}

	//lấy tổng số file trong foder cả nhưng file trong foder con
	public int getAmountOfFile(File file) {
		File[] list = file.listFiles();
		int files = 0;
		for (int i = 0; i < list.length; i++) {
			if (list[i].isDirectory()) {
				files += getAmountOfFile(list[i]);
			} else {
				files++;
			}
		}
		return files;
	}

	public Mat getTrainingNumberDataMat() {
		return trainingNumberDataMat;
	}

	public void setTrainingNumberDataMat(Mat trainingNumberDataMat) {
		this.trainingNumberDataMat = trainingNumberDataMat;
	}

	public Mat getNumberLabelsMat() {
		return numberLabelsMat;
	}

	public void setNumberLabelsMat(Mat numberLabelsMat) {
		this.numberLabelsMat = numberLabelsMat;
	}

	public Mat getTrainingWordDataMat() {
		return trainingWordDataMat;
	}

	public void setTrainingWordDataMat(Mat trainingWordDataMat) {
		this.trainingWordDataMat = trainingWordDataMat;
	}

	public Mat getWordLabelsMat() {
		return wordLabelsMat;
	}

	public void setWordLabelsMat(Mat wordLabelsMat) {
		this.wordLabelsMat = wordLabelsMat;
	}

	public SVM getNumberSVM() {
		return numberSVM;
	}

	public void setNumberSVM(SVM numberSVM) {
		this.numberSVM = numberSVM;
	}

	public SVM getWordSVM() {
		return wordSVM;
	}

	public void setWordSVM(SVM wordSVM) {
		this.wordSVM = wordSVM;
	}
	
	
}
