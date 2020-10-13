package application;

import java.util.Date;

import dao.HistoryDAO;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.History;
import utils.Utils;

public class HistoryDetailController {
	@FXML
	private TextField maKH, bienSo, maNV, maCN, thoiGianVao, thoiGianRa;

	@FXML
	private ImageView xeVao, nguoiVao, xeRa, nguoiRa;

	public void init(History his) {
		if (his == null) {
			return;
		}
		History history = HistoryDAO.getHistoryByInDateAndPlate(his);

		if (history.getCustomerId() != 0)
			maKH.setText(Long.toString(history.getCustomerId()));
		bienSo.setText(history.getPlateNo());
		if (history.getUserId() != 0)
			maNV.setText(Long.toString(history.getUserId()));
		if (history.getParkingLotId() != 0)
			maCN.setText(Long.toString(history.getParkingLotId()));
		thoiGianVao.setText(history.getInDate().toString());
		if (history.getOutDate() != null)
			thoiGianRa.setText(history.getOutDate().toString());
		updateImageView(xeVao, history.getPlateInImage());
		updateImageView(xeRa, history.getPlateOutImage());
		updateImageView(nguoiVao, history.getFaceInImage());
		updateImageView(nguoiRa, history.getFaceOutImage());

	}

	private void updateImageView(ImageView view, Image image) {
		Utils.onFXThread(view.imageProperty(), image);
	}

}
