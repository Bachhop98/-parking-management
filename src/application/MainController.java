package application;

import dao.RevenueDAO;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;

import constant.SystemConstant;
import dao.HistoryDAO;
import dao.PriceDAO;
import dao.RentDetailDAO;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Pagination;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.History;
import model.RentDetail;
import trainSVM.Train;
import utils.Utils;

import model.Chart;

public class MainController {

	private Stage primaryStage;

	private final int maxProcessing = 3;

	private int absolutePlateSize, maxPlateSize;

	private CascadeClassifier plateCascade;

	private CascadeClassifier faceCascade;

	private Train trainSVM;

	// a timer for acquiring the video stream
	private ScheduledExecutorService timer;
	// the OpenCV object that realizes the video capture
	private VideoCapture capturePlate = new VideoCapture();
	private VideoCapture captureFace = new VideoCapture();
	// the id of the camera to be used
	private static int cameraPlateId = 0;
	private static int cameraFaceId = 1;

	/* FXML IN */
	@FXML
	private ImageView imgViewPlateInCamera, imgViewFaceInCamera, imgViewPlateIn, imgViewFaceIn, imgViewPlateInHistory,
			imgViewFaceInHistory;

	@FXML
	private TextField txfCardInId, txfCardInType, txfCardInStatus, txfCustomerInName, txfCustomerInId;

	@FXML
	private TextArea txaPlateIn, txaTimeIn;

	/* FXML OUT */
	@FXML
	private ImageView imgViewPlateOut, imgViewFaceOut, imgViewPlateOutCamera, imgViewFaceOutCamera,
			imgViewPlateOutHistory, imgViewFaceOutHistory;

	@FXML
	private TextField txfCardOutId, txfCardOutType, txfCardOutStatus, txfCustomerOutName, txfCustomerOutId, txfTimeOut,
			txfPriceOut;

	@FXML
	private TextArea txaPlateOut;

	/** Khang **/

	private static final int rowsPerPage = 20;

	@FXML
	private Pagination pagination;

	@FXML
	private TableColumn<History, String> idCol, typeCardCol, plateNoCol, customerIdCol, inDateCol, outDateCol, priceCol;

	@FXML
	private TableView<History> historyTable;

	private ObservableList<History> historyData = null;

	private FilteredList<History> filteredData;

	@FXML
	private Tab main, history, statistic;

	@FXML
	private TabPane tabPane;

	@FXML
	private TextField searchTextField;

	/** Khang **/

	/** Tài **/
	XYChart.Series<String, Number> dataSeries;

	RevenueDAO dao = new RevenueDAO();

	String dayfrom, dayto;

	List<Chart> chart = new ArrayList<Chart>();

	@FXML
	DatePicker fx_DayFrom;

	@FXML
	DatePicker fx_DayTo;

	@FXML
	BarChart<String, Number> fx_BarChart;

	@FXML
	Button fx_buttonSearch;

	@FXML
	BorderPane pane_bar;

	@FXML
	CategoryAxis fx_bar_X;

	@FXML
	NumberAxis fx_bar_Y;

	/** Tài **/

	protected void init(Stage primaryStage) {
		this.primaryStage = primaryStage;

		this.absolutePlateSize = 0;
		this.maxPlateSize = 0;

		this.plateCascade = new CascadeClassifier();
		this.plateCascade.load("resources\\lbpcascades\\haarcascade_plate.xml");

		this.faceCascade = new CascadeClassifier();
		this.faceCascade.load("resources\\lbpcascades\\haarcascade_frontalface_alt.xml");

		trainSVM = new Train();

		setNumberTXF();

		this.imgViewPlateInCamera.setPreserveRatio(true);
		this.imgViewFaceInCamera.setPreserveRatio(true);
		this.imgViewPlateOutCamera.setPreserveRatio(true);
		this.imgViewFaceOutCamera.setPreserveRatio(true);

		this.startCamera();

		this.fx_BarChart.setAnimated(false);
		this.dataSeries = new XYChart.Series();
		
		this.historyTable.setId("history-table");

	}

	/** Khang **/
	private void changeTableView(int index, int limit) {

		int fromIndex = index * limit;
		int toIndex = Math.min(fromIndex + limit, historyData.size());

		int minIndex = Math.min(toIndex, filteredData.size());
		SortedList<History> sortedData = new SortedList<>(
				FXCollections.observableArrayList(filteredData.subList(Math.min(fromIndex, minIndex), minIndex)));
		sortedData.comparatorProperty().bind(historyTable.comparatorProperty());

		historyTable.setItems(sortedData);

	}

	@FXML
	private void chooseHistoryTab() {
		if (history.isSelected()) {

			historyData = HistoryDAO.getAllHistory();

			ObservableList<History> oList = FXCollections.observableArrayList();
			for (History history : historyData) {
				oList.add(history);
			}

			filteredData = new FilteredList<>(oList, p -> true);
			searchTextField.textProperty().addListener((observable1, oldValue, newValue) -> {
				filteredData.setPredicate(history -> newValue == null || newValue.isEmpty()
						|| history.getPlateNo().toLowerCase().contains(newValue.toLowerCase())
						|| history.getCustomerId_String().contains(newValue.toLowerCase()));
				changeTableView(pagination.getCurrentPageIndex(), rowsPerPage);
			});

			idCol.setCellValueFactory(cellData -> cellData.getValue().getIdProperty());

			typeCardCol.setCellValueFactory(cellData -> cellData.getValue().getCustomerId() != 0 ?  new SimpleStringProperty("Thẻ tháng"): new SimpleStringProperty("Thẻ ngày"));

			plateNoCol.setCellValueFactory(cellData -> cellData.getValue().getPlateNoProperty());
			
//			plateNoCol.setcell
			
			customerIdCol.setCellValueFactory(cellData -> cellData.getValue().getCustomerIdProperty());
			
			inDateCol.setCellValueFactory(cellData -> cellData.getValue().getInDateProperty());

			outDateCol.setCellValueFactory(cellData -> cellData.getValue().getOutDateProperty());

			priceCol.setCellValueFactory(cellData -> cellData.getValue().getPriceProperty());

			int totalPage = (int) (Math.ceil(oList.size() * 1.0 / rowsPerPage));
			pagination.setPageCount(totalPage);
			pagination.setCurrentPageIndex(0);
			changeTableView(0, rowsPerPage);
			pagination.currentPageIndexProperty().addListener(
					(observable2, oldValue, newValue) -> changeTableView(newValue.intValue(), rowsPerPage));
		}
	}

	/** Khang **/

	/** Tuyễn **/

	@FXML
	public void chooseHistoryDetail(MouseEvent mouseEvent) {
		if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
			if (mouseEvent.getClickCount() == 2) {
				try {
					History his = (History) historyTable.getSelectionModel().getSelectedItem();
					if (his != null) {

						FXMLLoader loader = new FXMLLoader(getClass().getResource("HistoryDetail.fxml"));

						BorderPane secondaryLayout;
						secondaryLayout = (BorderPane) loader.load();

						Scene secondScene = new Scene(secondaryLayout);

						Stage newWindow = new Stage();
						newWindow.setTitle("Chi tiết");
						newWindow.setScene(secondScene);

						newWindow.setResizable(false);

						newWindow.initModality(Modality.WINDOW_MODAL);

						newWindow.initOwner(primaryStage);

						newWindow.show();
						HistoryDetailController historyDetailController = loader.getController();

						historyDetailController.init(his);
					}
				} catch (Exception e) {
					showMes("Không thể hiển thị chi tiết lịch sử", AlertType.ERROR);
					e.printStackTrace();
				}
			}
		}
	}

	/** Tuyễn **/

	/** Tài **/

	@FXML
	public void chooseStatisticTab() {
		if (statistic.isSelected()) {
			this.chart = dao.calMoneyMonth();
			fx_BarChart.getData().clear();
			dataSeries.getData().clear();
			dataSeries.setName("DOANH THU TRONG THÁNG NÀY");
			for (Chart chart : this.chart) {
				dataSeries.getData().add(new XYChart.Data<>(chart.getDate().toString(), chart.getMoney()));
			}
			fx_BarChart.getData().addAll(dataSeries);
		}
	}

	@FXML
	public void showChart(ActionEvent event) {
		if (this.dayto.equals("")) {
			Date date_from = new Date();
			SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd");
			this.dayto = fm.format(date_from);
			getData();
			return;
		}
		Date dayFrom = convertStringToDate(fx_DayFrom.getValue().toString());
		Date dayTo = convertStringToDate(fx_DayTo.getValue().toString());
		if(dayFrom.compareTo(dayTo) > 0) {
			showMes("Ngày bắt đầu phải trước ngày kết thúc", AlertType.ERROR);
			return;
		}
		
		getData();
		

	}
	
	public Date convertStringToDate(String day) {
		String[] s = day.split("-");
		return new Date(Integer.parseInt(s[2]),Integer.parseInt(s[1]),Integer.parseInt(s[0]));
	}

	@FXML
	public void getData() {
		this.chart = dao.calMoney(this.dayfrom, this.dayto);
		fx_BarChart.getData().clear();
		dataSeries = new XYChart.Series();
		dataSeries.setName("DOANH THU TỪ NGÀY " + this.dayfrom + " ĐẾN NGÀY " + this.dayto);

		for (Chart chart : this.chart) {
			dataSeries.getData().add(new XYChart.Data<>(chart.getDate().toString(), chart.getMoney()));
		}
		fx_BarChart.setData(FXCollections.observableArrayList(dataSeries));
	}

	@FXML
	public void setDayfrom(ActionEvent event) {
		this.dayfrom = fx_DayFrom.getValue().toString();
	}

	@FXML
	public void setDayto(ActionEvent event) {
		this.dayto = fx_DayTo.getValue().toString();
	}

	/** Tài **/

	public void setNumberTXF() {
		txfCardInId.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue != null) {
					if (!newValue.matches("\\d*")) {
						txfCardInId.setText(newValue.replaceAll("[^\\d]", ""));
					}
				}
			}
		});
		txfCardOutId.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue != null) {
					if (!newValue.matches("\\d*")) {
						txfCardOutId.setText(newValue.replaceAll("[^\\d]", ""));
					}
				}
			}
		});
	}

	public Image createImageToSave(VideoCapture videoCapture) {
		try {
			Mat plateInMat = this.grabFrame(videoCapture);
			Imgproc.resize(plateInMat, plateInMat, new Size(256, 192));
			return Utils.mat2Image(plateInMat);
		} catch (Exception e) {
			System.out.println("không thể lưu hình ảnh từ camera " + videoCapture.getNativeObjAddr());
			return null;
		}
	}

	@FXML
	public void checkCardIn(ActionEvent event) {
		clearIn();
		Mat matPlate = null;
		RentDetail rentDetailIn = null;
		String temp1 = null;
		String temp2 = null;
		for (int i = 0; i < maxProcessing; i++) {

			matPlate = detect(this.grabFrame(capturePlate), this.imgViewPlateIn, this.plateCascade);
			if (matPlate == null)
				continue;

			String plateIn = recognizePlate(matPlate, this.txaPlateIn);

			if (captureFace.isOpened())
				detect(this.grabFrame(captureFace), this.imgViewFaceIn, this.faceCascade);
			try {
				if (rentDetailIn == null)
					rentDetailIn = RentDetailDAO.getOneByIdAndStatus(Integer.parseInt(txfCardInId.getText()),
							SystemConstant.STATUS_ACTIVE);
				// kiem tra neu la the thang
				if (rentDetailIn.getPlateNo() != null) {
					// kiem tra bien so co trung khop ko
					if (rentDetailIn.getPlateNo().equals(plateIn)) {
						showInforCustomerIn(rentDetailIn);
						showCustomerInHistory(rentDetailIn);
						HistoryDAO.saveMonthHistory(
								new History(rentDetailIn.getCardId(), rentDetailIn.getCustomer().getId(),
										createImageToSave(this.capturePlate), createImageToSave(this.captureFace),
										plateIn, new java.util.Date(System.currentTimeMillis()), 0));
						return;
					} else {
						if (i < maxProcessing - 1)
							continue;
						clearIn();
						showMes("Biển số không trùng khớp với thẻ", AlertType.ERROR);
						return;
					}
					// neu la the ngay
				} else {
					if (temp1 == null) {
						temp1 = recognizePlate(
								detect(this.grabFrame(capturePlate), this.imgViewPlateIn, this.plateCascade),
								this.txaPlateIn);
						if (!temp1.equals(plateIn)) {
							temp2 = recognizePlate(
									detect(this.grabFrame(capturePlate), this.imgViewPlateIn, this.plateCascade),
									this.txaPlateIn);
							if (temp1.equals(temp2))
								plateIn = temp1;
							if (!plateIn.equals(temp2)) {
								showMes("Không thể nhận diện biển số", AlertType.ERROR);
								return;
							}
						}
					}

					showInforCustomerIn(rentDetailIn);
					HistoryDAO.saveDayHistory(new History(rentDetailIn.getCardId(),
							createImageToSave(this.capturePlate), createImageToSave(this.captureFace), plateIn,
							new java.util.Date(System.currentTimeMillis()), 0));
					return;

				}
			} catch (NullPointerException e) {
				e.printStackTrace();
				clearIn();
				showMes("Thẻ không tồn tại", AlertType.INFORMATION);
				return;
			}
		}

		showMes("Không thể nhận diện biển số", AlertType.ERROR);
		return;
	}

	@FXML
	public void checkCardOut(ActionEvent event) {
		clearOut();
		Mat matPlate = null;
		History historyOut = null;
		RentDetail rentDetailOut = null;
		for (int i = 0; i < maxProcessing; i++) {

			matPlate = detect(this.grabFrame(capturePlate), this.imgViewPlateOut, this.plateCascade);
			if (matPlate == null)
				continue;

			String plateOut = recognizePlate(matPlate, this.txaPlateOut);

			if (captureFace.isOpened() || this.imgViewFaceIn.getImage() != null)
				detect(this.grabFrame(captureFace), this.imgViewFaceOut, this.faceCascade);

			try {
				if (historyOut == null)
					historyOut = HistoryDAO.getHistoryOut(Long.parseLong(this.txfCardOutId.getText()));
				// kiem tra bien so co trung khop ko
				if (historyOut.getPlateNo().equals(plateOut)) {
					if (rentDetailOut == null)
						rentDetailOut = RentDetailDAO.getOneByIdAndStatus(historyOut.getCardId(),
								SystemConstant.STATUS_ACTIVE);
					historyOut.setPrice(calculatePriceOut(rentDetailOut, historyOut));
					showInforCustomerOut(rentDetailOut, historyOut);
					historyOut.setPlateOutImage(createImageToSave(this.capturePlate));
					historyOut.setFaceOutImage(createImageToSave(this.captureFace));
					historyOut.setOutDate(new Date(System.currentTimeMillis()));
					HistoryDAO.updateHistoryOut(historyOut);
					return;
				} else {
					if (i < maxProcessing - 1)
						continue;
					clearOut();
					showMes("Biển số không trùng khớp với thẻ", AlertType.ERROR);
					return;

				}
			} catch (Exception e) {
				showMes("Không có lịch sử xe vào", AlertType.INFORMATION);
				return;
			}
		}

		showMes("Không thể nhận diện biển số", AlertType.ERROR);
		return;
	}

	private void showCustomerInHistory(RentDetail rentDetailIn) {
		try {

			History history = HistoryDAO.getLastImageInByCardIdAndCustomerId(rentDetailIn.getCardId(),
					rentDetailIn.getCustomer().getId());
			updateImageView(this.imgViewPlateInHistory, history.getPlateOutImage());
			updateImageView(this.imgViewFaceInHistory, history.getFaceOutImage());
		} catch (NullPointerException e) {
			System.out.println("Thẻ không có lịch sử");
		}
	}

	private Mat addTime(Mat frame) {
		try {
			Imgproc.putText(frame, SystemConstant.dateFormat.format(new Date(System.currentTimeMillis())),
					new Point(frame.cols() - 300, frame.rows() - 20), Imgproc.FONT_HERSHEY_DUPLEX, 0.8,
					new Scalar(0, 255, 255));
			return frame;
		} catch (Exception e) {
			System.out.println("cannot add time:" + e);
			return null;
		}
	}

	private void showInforCustomerIn(RentDetail rentDetailIn) {

		this.txfCardInType.setText(rentDetailIn.getCustomer() != null ? "Thẻ tháng" : "Thẻ ngày");
		if (rentDetailIn.getEndDate() == null
				|| new Date(System.currentTimeMillis()).compareTo(rentDetailIn.getEndDate()) < 0) {
			this.txfCardInStatus.setText("Còn thời hạn");
			this.txfCardInStatus.setStyle("-fx-text-fill: green;");

		} else {
			this.txfCardInStatus.setText("Hết thời hạn");
			this.txfCardInStatus.setStyle("-fx-text-fill: red;");

		}
		this.txaTimeIn.setText(SystemConstant.dateFormat.format(new Date(System.currentTimeMillis())));
		if (rentDetailIn.getCustomer() != null) {
			this.txfCustomerInName.setText(rentDetailIn.getCustomer().getName());
			this.txfCustomerInId.setText(rentDetailIn.getCustomer().getId() + "");
		}
	}

	private void showInforCustomerOut(RentDetail rentDetailOut, History historyOut) {

		this.txfCardOutType.setText(rentDetailOut.getCustomer() != null ? "Thẻ tháng" : "Thẻ ngày");
		if (rentDetailOut.getEndDate() == null
				|| new Date(System.currentTimeMillis()).compareTo(rentDetailOut.getEndDate()) < 0) {
			this.txfCardOutStatus.setText("Còn thời hạn");
			this.txfCardOutStatus.setStyle("-fx-text-fill: green;");

		} else {
			this.txfCardOutStatus.setText("Hết thời hạn");
			this.txfCardOutStatus.setStyle("-fx-text-fill: red;");

		}
		this.txfTimeOut.setText(SystemConstant.dateFormat.format(new Date(System.currentTimeMillis())));
		if (rentDetailOut.getCustomer() != null) {
			this.txfCustomerOutName.setText(rentDetailOut.getCustomer().getName());
			this.txfCustomerOutId.setText(rentDetailOut.getCustomer().getId() + "");
		}
		updateImageView(imgViewPlateOutHistory, historyOut.getPlateInImage());
		updateImageView(imgViewFaceOutHistory, historyOut.getFaceInImage());
		this.txfPriceOut.setText(historyOut.getPrice() + " VNĐ");
	}

	private int calculatePriceOut(RentDetail rentDetailOut, History historyOut) {
		int res = 0;
		if (historyOut.getCustomerId() == 0) {
			res = (int) (PriceDAO.getDayPriceByIdAndStatus(rentDetailOut.getPriceId(), SystemConstant.STATUS_ACTIVE)
					* calculateTheNumberOfDays(new Date(System.currentTimeMillis()), historyOut.getInDate()));
		} else if (rentDetailOut.getEndDate().compareTo(new Date(System.currentTimeMillis())) < 0) {
			res = (int) (PriceDAO.getDayPriceByIdAndStatus(rentDetailOut.getPriceId(), SystemConstant.STATUS_ACTIVE)
					* calculateTheNumberOfDays(new Date(System.currentTimeMillis()), rentDetailOut.getEndDate()));
		}
		return res;
	}

	private long calculateTheNumberOfDays(Date startDate, Date endDate) {
		return (startDate.getTime() - endDate.getTime()) / (1000 * 60 * 60 * 24) + 1;
	}

	public void clearIn() {
		txaPlateIn.setText(null);
		txaTimeIn.setText(null);

		imgViewPlateIn.setImage(null);
		imgViewFaceIn.setImage(null);
		imgViewPlateInHistory.setImage(null);
		imgViewFaceInHistory.setImage(null);

		txfCardInStatus.setText(null);
		txfCardInType.setText(null);
		txfCustomerInId.setText(null);
		txfCustomerInName.setText(null);
	}

	public void clearOut() {
		txaPlateOut.setText(null);

		imgViewPlateOut.setImage(null);
		imgViewFaceOut.setImage(null);
		imgViewPlateOutHistory.setImage(null);
		imgViewFaceOutHistory.setImage(null);

		txfCardOutStatus.setText(null);
		txfCardOutType.setText(null);
		txfCustomerOutId.setText(null);
		txfCustomerOutName.setText(null);
		txfTimeOut.setText(null);
		txfPriceOut.setText(null);
	}

	private Mat detect(Mat frame, ImageView imgView, CascadeClassifier cascade) {

		MatOfRect plates = new MatOfRect();
		Mat grayFrame = new Mat();

		// chuyển ảnh xám
		Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

		if (this.absolutePlateSize == 0 && this.maxPlateSize == 0) {
			int height = grayFrame.rows();
			if (Math.round(height * 0.1f) > 0) {
				this.absolutePlateSize = Math.round(height * 0.1f);
				this.maxPlateSize = Math.round(height * 0.4f);
			}
		}

		cascade.detectMultiScale(grayFrame, plates, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
				new Size(0, this.absolutePlateSize), new Size(0, this.maxPlateSize));

		if (plates.empty()) {

			Mat rot_mat = new Mat();

			Point center = new Point(grayFrame.cols() / 2, grayFrame.rows() / 2);
			double scale = 1;
			Mat temp = grayFrame.clone();
			for (int j = 5; Math.abs(j) < 46; j += 5) {
				if (j < 5)
					j -= 10;
				grayFrame = temp.clone();
				rot_mat = Imgproc.getRotationMatrix2D(center, j, scale);
				Imgproc.warpAffine(grayFrame, grayFrame, rot_mat, grayFrame.size());

				// detect plates
				this.plateCascade.detectMultiScale(grayFrame, plates, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
						new Size(this.absolutePlateSize, this.absolutePlateSize),
						new Size(this.maxPlateSize, this.maxPlateSize));

				if (plates.empty()) {
					if (j == 45)
						j = -1;
					continue;
				}
				Imgproc.warpAffine(frame, frame, rot_mat, frame.size());
				break;
			}
		}

		Rect[] platesArray = plates.toArray();
		try {
			Image img = Utils.mat2Image(frame.submat(platesArray[0]));
			updateImageView(imgView, img);
			return grayFrame.submat(platesArray[0]);
		} catch (ArrayIndexOutOfBoundsException e) {

		}
		return null;

	}

	private String recognizePlate(Mat matPlate, TextArea txaPlate) {
		// lọc nhiễu
		Imgproc.GaussianBlur(matPlate, matPlate, new Size(5, 5), 0, 0);

		Mat temp = matPlate.clone();
		Imgproc.bilateralFilter(temp, matPlate, 9, 9, 75);

		if (matPlate.rows() > 100 || matPlate.cols() > 100)
			Imgproc.adaptiveThreshold(matPlate, matPlate, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C,
					Imgproc.THRESH_BINARY_INV, 13, 9);
		else
			Imgproc.adaptiveThreshold(matPlate, matPlate, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
					Imgproc.THRESH_BINARY_INV, 11, 9);

		List<Mat> mats = takeCharsFromPlate(matPlate);

		String s = "";
		for (int i = 0; i < mats.size(); i++) {
			if (i > 8)
				break;
			if (i != 2) {
				Mat m = trainSVM.preProcessingAMat(mats.get(i));
				s += (char) trainSVM.getNumberSVM().predict(m) + " ";
				if (i == 3)
					s += "\n";
			} else {
				s += "- ";
				Mat m = trainSVM.preProcessingAMat(mats.get(i));
				s += (char) trainSVM.getWordSVM().predict(m) + " ";
			}
		}
		txaPlate.setText(s);

		s = s.replaceAll(" ", "");
		s = s.replaceAll("\n", "");
		s = s.replaceAll("-", "");
		return s;
	}

	private List<Mat> takeCharsFromPlate(Mat recognize) {
		Size minSize = new Size();
		minSize.height = recognize.rows() / 4.5;
		minSize.width = recognize.cols() / 21;
		Size maxSize = new Size();
		maxSize.height = recognize.rows() / 2.2;
		maxSize.width = recognize.cols() / 5;
		List<Mat> mats = new ArrayList<Mat>();
		Mat mat = new Mat();

		Rect rect = new Rect();
		Integer midPoint = recognize.cols() / 2;
		int row = recognize.rows() / 4;

		for (int r = row; r < recognize.rows(); r += row * 2) {
			for (int c = midPoint; c < recognize.cols(); c--) {
				int val = (int) recognize.get(r, c)[0];
				if (val != 255) {
					if (c > midPoint)
						c += 2;
					if (c == 0) {
						c = midPoint + 2;
					}
					continue;
				}
				Imgproc.floodFill(recognize, mat, new Point(c, r), new Scalar(128), rect);
				if (rect.width < minSize.width || rect.height < minSize.height || rect.width > maxSize.width
						|| rect.height > maxSize.height) {
					Imgproc.floodFill(recognize, mat, new Point(c, r), new Scalar(0), rect);
					if (c > midPoint)
						c += 2;
					if (c == 0)
						c = midPoint + 2;
					continue;
				}
				if (c < midPoint && (mats.size() == 1 || mats.size() == 5))
					mats.add(mats.size() - 1, new Mat(recognize, rect));
				else {
					if (c < midPoint && mats.size() == 6) {

						mats.add(mats.size() - 2, new Mat(recognize, rect));
						c = midPoint + 2;
					} else
						mats.add(new Mat(recognize, rect));
				}

				if (c > midPoint)
					c += 2;
				if (c == 0)
					c = midPoint + 2;
				if ((c > midPoint && ((r == row && mats.size() == 4)) || (r == row * 3 && mats.size() == 9)))
					break;
			}
		}
		return mats;
	}

	public void showMes(String mes, AlertType alertType) {
		Alert alert = new Alert(alertType);
		alert.setTitle("Thông báo");
		alert.setHeaderText(null);
		alert.setContentText(mes);
		alert.showAndWait();
	}

	private Mat grabFrame(VideoCapture capture) {
		// init everything
		Mat frame = new Mat();

		// check if the capture is open
		if (capture.isOpened()) {
			try {
				// read the current frame
				capture.read(frame);
				addTime(frame);

			} catch (Exception e) {
				// log the error
				System.err.println("Exception during the image elaboration: " + e);
			}
		}
		return frame;
	}

	private void startCamera() {
		// start the video capture
		capturePlate.open(cameraPlateId);
		captureFace.open(cameraFaceId);

		// is the video stream available?
		if (capturePlate.isOpened()) {
			// grab a frame every 33 ms (30 frames/sec)
			Runnable frameGrabber = new Runnable() {

				@Override
				public void run() {
					// effectively grab and process a single frame
					Mat framePlate = grabFrame(capturePlate);
					if (captureFace.isOpened()) {
						Mat frameFace = grabFrame(captureFace);
						Image imageFaceToShow = Utils.mat2Image(frameFace);
						updateImageView(imgViewFaceOutCamera, imageFaceToShow);
						updateImageView(imgViewFaceInCamera, imageFaceToShow);
					}

					// convert and show the frame
					Image imagePlateToShow = Utils.mat2Image(framePlate);
					updateImageView(imgViewPlateInCamera, imagePlateToShow);
					updateImageView(imgViewPlateOutCamera, imagePlateToShow);

				}
			};

			this.timer = Executors.newSingleThreadScheduledExecutor();
			this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

		} else {
			// log the error
			System.err.println("Impossible to open the camera connection...");
		}

	}

	private void stopAcquisition() {
		if (this.timer != null && !this.timer.isShutdown()) {
			try {
				// stop the timer
				this.timer.shutdown();
				this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// log any exception
				System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
			}
		}

		if (this.capturePlate.isOpened() || this.captureFace.isOpened()) {
			// release the camera
			this.capturePlate.release();
			this.captureFace.release();
		}
	}

	private void updateImageView(ImageView view, Image image) throws NullPointerException {
		Utils.onFXThread(view.imageProperty(), image);
	}

	protected void setClosed() {
		this.stopAcquisition();
	}

}