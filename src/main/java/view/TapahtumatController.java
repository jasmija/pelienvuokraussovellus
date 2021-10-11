package view;

import java.io.IOException;

import controller.ProfiiliController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
//import javafx.scene.control.ListView;
import javafx.stage.Stage;
import model.Kayttaja;
import model.Peli;
import model.PeliSovellusDAO;
import model.TiedostoKasittely;

public class TapahtumatController {

	@FXML
	private ListView<Peli> omatPelit;

	@FXML
	private RadioButton myynti;

	@FXML
	private ToggleGroup tyyppi;

	@FXML
	private RadioButton vuokraus;

	@FXML
	private RadioButton lahjoitus;

	@FXML
	private TextField hinta;

	@FXML
	private TextField pelinnimi;

	@FXML
	private ToggleGroup pelintyyppi;

	@FXML
	private RadioButton video;

	@FXML
	private RadioButton lauta;

	@FXML
	private TextField kaupunki;

	@FXML
	private TextField ikaraja;

	@FXML
	private TextField pelaajamaara;

	@FXML
	private TextField kuvaus;
	
	@FXML 
	private TextField tekstikenttä;
	
	@FXML
	private ChoiceBox<String> genre;
	
	@FXML
	private ChoiceBox<String> kunto;
	
	@FXML
	private ChoiceBox<String> konsoli;
	private Kayttaja käyttäjä;
	@FXML
	private Pane konsoliPane;
	@FXML
	private Label kirjaimet;

	
	


	private Stage dialogStage;
	private MainApp main;
	PeliSovellusDAO pelitdao = new PeliSovellusDAO();
	private Peli[] pelit;

	public TapahtumatController() {}

	@FXML
	public void initialize() {
		//kirjautunut käyttäjä haku
		this.käyttäjä=TiedostoKasittely.lueKäyttäjä();
		ObservableList<String> options = FXCollections.observableArrayList("Urheilu", "Räiskintä","Toiminta"
				,"Ajopeli", "Jännitys", "Seikkailu", "Strategia", "Roolipeli", "Pulma",
				"Lautapeli", "Juomapeli", "Tappelupeli", "Tasohyppeli");
		genre.setItems(options);
		
		ObservableList<String> kuntoOptions = FXCollections.observableArrayList("Erinomainen", "Kiitettävä","Hyvä"
				,"Kohtalainen", "Välttävä");
		kunto.setItems(kuntoOptions);
		
		ObservableList<String> konsoliOptions = FXCollections.observableArrayList("Xbox", "Playstation","Wii");
		konsoli.setItems(konsoliOptions);

		listaaOmatPelit();
		omatPelit.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) ->  {
					
				pelinTiedot(newValue);
				});	
	}
	
	@FXML
	public String tyyppiAction(ActionEvent Action) {
		String text = ((RadioButton)tyyppi.getSelectedToggle()).getText();
		System.out.println(text);
		if(text.equals("Lahjoitus")) {
			hinta.setText(Integer.toString(0));
			hinta.setEditable(false);
		}else {
			hinta.setText("");
			hinta.setEditable(true);
		}
		return text;
	}
	
	@FXML
	public String tyyppi(ActionEvent Action) {
		
		String text = ((RadioButton)pelintyyppi.getSelectedToggle()).getText();
		System.out.println(text);
		
		if(text.equals("lauta")) {
			konsoliPane.setVisible(false);
		}else {
			konsoliPane.setVisible(true);
		}
		
		return text;
	}

	public void setMainApp(MainApp main) {
		this.main = main;
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;	
	}

	public void listaaOmatPelit() {
		System.out.println("listaa omat pelit");
		pelit = pelitdao.haeOmatPelit();
		System.out.println(pelit.toString());
		System.out.println(pelit.length);
		for (int i = 0; i < pelit.length; i++) {
			System.out.println(pelit[i]);
			omatPelit.getItems().add(pelit[i]);
		}
	}

	@FXML
	public void poistaPeli() { 
		Peli peli = omatPelit.getSelectionModel().getSelectedItem();
		poistaPeliTietokannasta(peli);	 
	}

	public void poistaPeliTietokannasta(Peli peli) {
		int id = peli.getPeliId();
		System.out.println("poistettava id " + id);
		pelitdao.poistaPeli(id);

		//Peli poistuu listasta heti
		omatPelit.getItems().clear();
		listaaOmatPelit();
	}

	private void pelinTiedot(Peli peli) {
		if(peli != null) {
			pelinnimi.setText(peli.getPelinNimi());
			hinta.setText(Integer.toString(peli.getHinta()));

			String pelintalletusstring = peli.getTalletusTyyppi();
			System.out.println("Pelintalletustyyppi string: " + peli.getTalletusTyyppi());
			if(pelintalletusstring.equals("Myynti")) {
				tyyppi.selectToggle(myynti);
			}else if(pelintalletusstring.equals("Lahjoitus")){
				tyyppi.selectToggle(lahjoitus);
			}else {
				tyyppi.selectToggle(vuokraus);
			}

			String pelintyyppistring = peli.getPelintyyppi();
			System.out.println("Pelintyyppi string: " + peli.getPelintyyppi());
			if(pelintyyppistring.equals("lauta")) {
				pelintyyppi.selectToggle(lauta);
				konsoliPane.setVisible(false);
			}else {
				pelintyyppi.selectToggle(video);
				konsoliPane.setVisible(true);
			}

			kaupunki.setText(peli.getKaupunki());
			genre.setValue(peli.getGenre());
			kunto.setValue(peli.getKunto());
			ikaraja.setText(Integer.toString(peli.getIkaraja()));
			pelaajamaara.setText(Integer.toString(peli.getPelmaara()));
			kuvaus.setText(peli.getKuvaus());
			tekstikenttä.setText(peli.getTekstikenttä());
			konsoli.setValue(peli.getKonsoli());
		} else {
			pelinnimi.setText("");
			hinta.setText("");
			kaupunki.setText("");
			genre.setValue("");
			ikaraja.setText("");
			pelaajamaara.setText("");
			kuvaus.setText("");
			tekstikenttä.setText("");
			konsoli.setValue("");
		}
	}

	@FXML
	public void tallennaMuutokset() {
		Peli peli = new Peli();
		Peli peliId = omatPelit.getSelectionModel().getSelectedItem();
		peli.setPeliId(peliId.getPeliId());
		peli.setPelinNimi(pelinnimi.getText());
		int price = Integer.parseInt(hinta.getText());
		peli.setHinta(price);
		int age = Integer.parseInt(ikaraja.getText());
		peli.setIkaraja(age);
		peli.setKaupunki(kaupunki.getText());
		String tyyppiText = ((RadioButton)tyyppi.getSelectedToggle()).getText();
		System.out.println("TALLETUKSEN TYYPPI:" + tyyppiText);
		peli.setTalletusTyyppi(tyyppiText);
		peli.setKuvaus(kuvaus.getText());
		int players = Integer.parseInt(pelaajamaara.getText());
		peli.setPelmaara(players);
		String pelintyyppiText = ((RadioButton)pelintyyppi.getSelectedToggle()).getText();
		peli.setPelinTyyppi(pelintyyppiText);
		peli.setGenre(genre.getValue().toString());
		System.out.println(genre.getValue().toString());
		peli.setKunto(kunto.getValue().toString());
		System.out.println(peli.getPelinNimi()+" "+ peli.getHinta()+" "+ peli.getIkaraja());
		peli.setTekstikenttä(tekstikenttä.getText());
		
		if(peli.getKonsoli() != null) {
			peli.setKonsoli(konsoli.getValue().toString());
		}else {
			peli.setKonsoli("");
		}
		pelitdao.paivitaPeli(peli);	
		
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
	    alert.setTitle("Alert");
	    alert.setContentText("Muutokset tallennettu onnistuneesti!");
	    alert.showAndWait();
		
		omatPelit.getItems().clear();
		listaaOmatPelit();
	}

	
	@FXML
	public void kirjaimet(KeyEvent key) {
		String kirjaimetstring = tekstikenttä.getText();
		int maxpituus = 200;
		int pituus = 1;
		pituus = kirjaimetstring.length();
		int jaljella = maxpituus - pituus;
		
		if(jaljella <= 0) {
			jaljella = 0;
		}
		
		if(jaljella == 0) {
			kirjaimet.setText("Kirjaimia jäljellä: " + jaljella);
			tekstikenttä.setEditable(false);
			
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
		    alert.setTitle("Alert");
		    alert.setContentText("Tekstikenttä täynnä!");
		    alert.showAndWait();
		    
			//Tekstikenttään voi taas kirjoittaa
		    tekstikenttä.setEditable(true);
		}else {
			System.out.println("Jaljella: " + jaljella);
			kirjaimet.setText("Kirjaimia jäljellä: " + jaljella);
		}
	}
	

    @FXML
    void vieEtusivulle(ActionEvent event) throws IOException {
    	//vaihdetaan näkymää samalla viedään käyttäjän tiedot
		 FXMLLoader loader = new FXMLLoader();
       loader.setLocation(MainApp.class.getResource("Etusivu.fxml"));
       BorderPane personOverview = (BorderPane) loader.load();
       Scene etusivulle = new Scene(personOverview);
       //stage
       Stage window=(Stage)((Node)event.getSource()).getScene().getWindow();
	    	window.setScene(etusivulle);
	    	window.show();
    }
    
    @FXML
    void lisaaUusiPeliNäkymä(ActionEvent event) throws IOException {
    	//vaihdetaan näkymää samalla viedään käyttäjän tiedot
		 FXMLLoader loader = new FXMLLoader();
       loader.setLocation(MainApp.class.getResource("Uusipeli.fxml"));
       BorderPane personOverview = (BorderPane) loader.load();
       Scene etusivulle = new Scene(personOverview);
       //stage
       Stage window=(Stage)((Node)event.getSource()).getScene().getWindow();
	    	window.setScene(etusivulle);
	    	window.show();
    }

    @FXML
    void vieProfiiliNäkymään(ActionEvent event) throws IOException {
    	//vaihdetaan näkymää samalla viedään käyttäjän tiedot
		 FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("Profiili.fxml"));
        BorderPane personOverview = (BorderPane) loader.load();
        Scene etusivulle = new Scene(personOverview);
        //stage
        Stage window=(Stage) ((Node)event.getSource()).getScene().getWindow();
	    	window.setScene(etusivulle);
	    	window.show();
    }
    @FXML
    void LogOut(ActionEvent event) throws IOException {
    	boolean test=TiedostoKasittely.poistaTiedosto();
    	if(test==true) {
    		//ajetaan kirjautumis sivulle
    		FXMLLoader loader = new FXMLLoader();
	        
	        loader.setLocation(MainApp.class.getResource("Kirjautuminen.fxml"));
	       
	        BorderPane etusivu = (BorderPane) loader.load();
	    	Scene kirjautumisNäkymä = new Scene(etusivu);
	    	//get stage
	    	Stage window=(Stage) ((Node)event.getSource()).getScene().getWindow();
	    	window.setScene(kirjautumisNäkymä);
	    	window.show();
    		
    	}
    	
    }

}