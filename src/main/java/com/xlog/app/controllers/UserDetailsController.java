
package com.xlog.app.controllers;

import com.xlog.app.data.AvatarRepository;
import com.xlog.app.data.Database;
import com.xlog.app.data.PurchaseRepository;
import com.xlog.app.models.AvatarSelection;
import com.xlog.app.models.AvatarSelection.BodyColor;
import com.xlog.app.models.AvatarSelection.Displayable;
import com.xlog.app.models.AvatarSelection.EyeType;
import com.xlog.app.models.Session;
import com.xlog.app.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class UserDetailsController {

    @FXML private Label lblName;
    @FXML private Label lblLevel;
    @FXML private ProgressBar xpBar;
    @FXML private Label coinsBadge;

    @FXML private StackPane avatarStack;
    @FXML private ImageView imgBody;
    @FXML private ImageView imgEyes;
    @FXML private ImageView imgDisplay;

    @FXML private Label statInt, statStr, statChi, statCha;

    // Modal + combos
    @FXML private AnchorPane customizeModal;
    @FXML private ComboBox<BodyColor>   cmbBody;
    @FXML private ComboBox<EyeType>     cmbEyes;
    @FXML private ComboBox<Displayable> cmbDisplay;

    private final User user = Session.getUser();
    private final AvatarRepository repo = new AvatarRepository();
    private final PurchaseRepository purchases = new PurchaseRepository();
    private AvatarSelection current;

    @FXML public void initialize(){
        try { Database.init(); } catch (SQLException e) { e.printStackTrace(); }
        // Header
        lblName.setText(user.getUsername());
        lblLevel.setText("Level " + user.getLevel());
        coinsBadge.setText(user.getCoins() + " Coins");
        xpBar.setProgress(Math.max(0, Math.min(1, user.getXp())));
        // Stats
        statInt.setText(String.valueOf(user.getIntelligence()));
        statStr.setText(String.valueOf(user.getStrength()));
        statChi.setText(String.valueOf(user.getChi()));
        statCha.setText(String.valueOf(user.getCharisma()));
        // Selection + preview
        current = repo.load();
        updatePreview();
    }

    private void updatePreview() {
        BodyColor body = current.getBody();
        EyeType eyes = current.getEyes();
        Displayable disp = current.getDisplay();
        imgBody.setImage(new Image("/com/xlog/app/images/avatar/body/" + body.name().toLowerCase() + ".png", 220, 220, true, true));
        imgEyes.setImage(new Image("/com/xlog/app/images/avatar/eyes/" + eyes.name().toLowerCase() + ".png", 220, 220, true, true));
        imgDisplay.setImage(disp == Displayable.NONE ? null :
                new Image("/com/xlog/app/images/avatar/display/" + disp.name().toLowerCase() + ".png", 220, 220, true, true));
    }

    /* Modal */
    @FXML private void openCustomize() {
        cmbBody.setItems(FXCollections.observableArrayList(BodyColor.values()));
        cmbBody.setValue(current.getBody());

        cmbEyes.setItems(FXCollections.observableArrayList(EyeType.values()));
        cmbEyes.setValue(current.getEyes());

        ObservableList<Displayable> dispOptions = FXCollections.observableArrayList();
        dispOptions.add(Displayable.NONE);
        if (purchases.isOwned("football"))   dispOptions.add(Displayable.FOOTBALL);
        if (purchases.isOwned("sunglasses")) dispOptions.add(Displayable.SUNGLASSES);
        if (purchases.isOwned("staff"))      dispOptions.add(Displayable.STAFF);
        cmbDisplay.setItems(dispOptions);
        cmbDisplay.setValue(dispOptions.contains(current.getDisplay()) ? current.getDisplay() : Displayable.NONE);

        customizeModal.setVisible(true);
    }
    @FXML private void closeCustomize(){ customizeModal.setVisible(false); }

    @FXML private void saveAvatar(){
        BodyColor b = cmbBody.getValue() == null ? BodyColor.BLUE : cmbBody.getValue();
        EyeType e = cmbEyes.getValue() == null ? EyeType.NORMAL : cmbEyes.getValue();
        Displayable d = cmbDisplay.getValue() == null ? Displayable.NONE : cmbDisplay.getValue();
        current.setBody(b); current.setEyes(e); current.setDisplay(d);
        repo.save(current); updatePreview(); closeCustomize();
    }

    /* Navigation */
    @FXML private void gotoDashboard(javafx.event.ActionEvent evt){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/xlog/app/dashboard.fxml"));
            Scene scene = new Scene(loader.load(), 1280, 800);
            scene.getStylesheets().add(getClass().getResource("/com/xlog/app/style.css").toExternalForm());
            Stage st = (Stage) ((Node) evt.getSource()).getScene().getWindow();
            st.setScene(scene); st.setTitle("Zenith — Home"); st.show();
        } catch (IOException e) { e.printStackTrace(); }
    }
    @FXML private void gotoStore(javafx.event.ActionEvent evt){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/xlog/app/shop.fxml"));
            Scene scene = new Scene(loader.load(), 1280, 800);
            scene.getStylesheets().add(getClass().getResource("/com/xlog/app/style.css").toExternalForm());
            Stage st = (Stage) ((Node) evt.getSource()).getScene().getWindow();
            st.setScene(scene); st.setTitle("Zenith — Store"); st.show();
        } catch (IOException e) { e.printStackTrace(); }
    }
}
