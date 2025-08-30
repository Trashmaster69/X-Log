
package com.xlog.app.controllers;

import com.xlog.app.data.Database;
import com.xlog.app.data.PurchaseRepository;
import com.xlog.app.models.Session;
import com.xlog.app.models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ShopController {

    @FXML private Button btnDashboard;
    @FXML private Button btnStore;
    @FXML private Label coinsHeader;

    @FXML private ImageView imgFootball;
    @FXML private ImageView imgSunglasses;
    @FXML private ImageView imgStaff;

    @FXML private Label lblFootballPrice;
    @FXML private Label lblSunglassesPrice;
    @FXML private Label lblStaffPrice;

    @FXML private Label lblFootballName;
    @FXML private Label lblSunglassesName;
    @FXML private Label lblStaffName;

    @FXML private AnchorPane buyModal;
    @FXML private Label modalTitle;
    @FXML private ImageView modalImage;
    @FXML private Label modalDesc;
    @FXML private Label modalPrice;
    @FXML private Button modalBuyBtn;

    private final User user = Session.getUser();
    private final PurchaseRepository repo = new PurchaseRepository();

    private static class Item {
        final String key, name, desc, imgPath; final int price;
        Item(String key, String name, int price, String desc, String imgPath){
            this.key=key; this.name=name; this.price=price; this.desc=desc; this.imgPath=imgPath;
        }
    }
    private final Map<String, Item> items = new HashMap<>();
    private Item currentItem;

    @FXML public void initialize(){
        try { Database.init(); } catch (SQLException e) { e.printStackTrace(); }

        items.put("football",   new Item("football","Football",150,"A standard-sized football, perfect for a pickup game or practicing your throws.","/com/xlog/app/images/avatar/display/football.png"));
        items.put("sunglasses", new Item("sunglasses","Sunglasses",200,"Stylish shades. Look cool while grinding tasks.","/com/xlog/app/images/avatar/display/sunglasses.png"));
        items.put("staff",      new Item("staff","Wizard Staff",500,"A mystical staff. +10 style points and questionable arcane power.","/com/xlog/app/images/avatar/display/staff.png"));

        setCard(imgFootball, lblFootballName, lblFootballPrice, items.get("football"));
        setCard(imgSunglasses, lblSunglassesName, lblSunglassesPrice, items.get("sunglasses"));
        setCard(imgStaff, lblStaffName, lblStaffPrice, items.get("staff"));

        updateCoinsHeader();
    }
    private void setCard(ImageView img, Label name, Label price, Item item){
        name.setText(item.name); price.setText(String.valueOf(item.price)); try{ img.setImage(new Image(item.imgPath,300,160,true,true)); }catch(Exception ignore){}
    }
    private void updateCoinsHeader(){ coinsHeader.setText(String.valueOf(user.getCoins())); }

    @FXML private void openFootball(){ openModal(items.get("football")); }
    @FXML private void openSunglasses(){ openModal(items.get("sunglasses")); }
    @FXML private void openStaff(){ openModal(items.get("staff")); }

    private void openModal(Item item){
        currentItem=item; if(item==null) return;
        modalTitle.setText(item.name); modalDesc.setText(item.desc); modalPrice.setText(String.valueOf(item.price));
        try{ modalImage.setImage(new Image(item.imgPath,380,220,true,true)); }catch(Exception ignore){}
        boolean owned=repo.isOwned(item.key); boolean enough=user.getCoins()>=item.price;
        modalBuyBtn.setDisable(owned||!enough);
        modalBuyBtn.setText(owned? "Owned" : enough? "Buy" : "Not enough coins");
        buyModal.setVisible(true);
    }
    @FXML private void closeModal(){ buyModal.setVisible(false); }
    @FXML private void confirmBuy(){ if(currentItem==null) return; if(repo.isOwned(currentItem.key)) return; if(user.getCoins()<currentItem.price) return;
        user.addCoins(-currentItem.price); repo.buy(currentItem.key); updateCoinsHeader(); closeModal(); }

    @FXML private void gotoDashboard(javafx.event.ActionEvent evt){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/xlog/app/dashboard.fxml"));
            Scene scene = new Scene(loader.load(), 1280, 800);
            scene.getStylesheets().add(getClass().getResource("/com/xlog/app/style.css").toExternalForm());
            Stage stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
            stage.setScene(scene); stage.setTitle("Zenith — Home"); stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }
}
