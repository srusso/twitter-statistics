package com.ssof.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;

import ts.datatypes.MoodData;
import ts.datatypes.TimePeriod;
import ts.datatypes.WordTimelineTweets;
import ts.dbm.DBManager;
import ts.emotions.Dictionary;
import ts.emotions.ExpansionParameters;
import ts.emotions.LoadDictionary;
import ts.emotions.MultipleZScore;
import ts.emotions.SingleZScore;
import ts.emotions.WordUsage;
import ts.emotions.ZScore;
import ts.emotions.ZScoreForGraph;
import ts.exceptions.DateFormatException;
import ts.exceptions.DictionaryException;
import ts.exceptions.DictionaryFileFormatException;
import ts.exceptions.WrongAttributeValueException;
import ts.exceptions.WrongNumberOfValuesException;
import ts.exceptions.ZScoreException;
import ts.tweetsearch.SearchParameters;
import ts.tweetsearch.TweetSearch;
import ts.twitter.SingleTweet;
import ts.twitter.TweetManager;
import ts.utils.ArrayMath;
import ts.utils.DateUtils;
import ts.utils.TextAnalizer;
import twitter4j.TwitterException;

public class MainWindow extends JFrame implements MouseListener, ActionListener, WindowListener{
	private static final long serialVersionUID = -6210415924364979887L;
	
	private final String APP_NAME="Twitter Statistics";
	private final String VERSION="0.1";
	private final String COMPILATION_TIME;
	
	private final JFileChooser fileOpenChooser;
	private final JFileChooser wordlistFileChooser;
	private final JFileChooser currentDirectoryChooser;
	private final JFileChooser createNewFileChooser;
	private final JFileChooser createNewTwsChooser;
	private final JFileChooser cmpDicFileChooser;
	
	private final JTextArea outputPanel = new JTextArea();
	private final Container center;
	private final Container east;
	
	private final JButton startListening = new JButton("Ricevi tweet");
	private final JButton stopListening = new JButton("Stop ricezione");
	private final JButton loadTweetsButton = new JButton("Carica tweet da file TWS");
	private final JButton loadWordlistButton = new JButton("Carica wordlist");
	private final JButton percItaButton = new JButton("Percentuale Tweet Italiani");
	private final JButton loadDictionaryButton = new JButton("Carica Lessico");
	private final JButton printDictionaryButton = new JButton("Mostra Lessico Caricato");
	private final JButton expandDictionaryButton = new JButton("Espandi Lessico Caricato");
	private final JButton pertDictionaryButton = new JButton("Perturbazione Lessico Caricato");
	private final JButton zscoregraph = new JButton("Mostra Grafico Z-scores");
	
	private TweetManager tweetManager;
	
	
	private List <SingleTweet> tweets = null;
	
	private Dictionary dictionary = null;
	
	public MainWindow(){
		DateFormat format=DateFormat.getDateInstance();
		COMPILATION_TIME = format.format(new Date());
		
		tweetManager = new TweetManager();

		
		setSize(600, 600);
		setTitle(APP_NAME);
		addWindowListener(this);
		
		
		fileOpenChooser = new JFileChooser();
		fileOpenChooser.setFileFilter(new TwsFileFilter());
		fileOpenChooser.setMultiSelectionEnabled(true);
		
		currentDirectoryChooser = new JFileChooser();
		currentDirectoryChooser.setFileFilter(new DirFileFilter());
		currentDirectoryChooser.setMultiSelectionEnabled(false);
		currentDirectoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		wordlistFileChooser = new JFileChooser();
		wordlistFileChooser.setFileFilter(new ExtFileFilter(".txt"));
		wordlistFileChooser.setMultiSelectionEnabled(false);
		
		createNewFileChooser = new JFileChooser();
		createNewFileChooser.setFileFilter(new ExtFileFilter(".txt"));
		createNewFileChooser.setMultiSelectionEnabled(false);
		
		createNewTwsChooser = new JFileChooser();
		createNewTwsChooser.setFileFilter(new ExtFileFilter(".tws"));
		createNewTwsChooser.setMultiSelectionEnabled(false);
		
		cmpDicFileChooser = new JFileChooser();
		cmpDicFileChooser.setFileFilter(new ExtFileFilter(".txt"));
		cmpDicFileChooser.setMultiSelectionEnabled(true);
		
		
		outputPanel.setEditable(false);
		
		outputPanel.setMargin(new Insets(20, 20, 20, 20));
		outputPanel.setBackground(new Color(255,255,255,255));
		outputPanel.setForeground(new Color(0,0,0,255));
		
		JScrollPane sp = new JScrollPane(outputPanel);
		
		center = new Container();
		center.setLayout(new BorderLayout());
		center.add(sp, BorderLayout.CENTER);
		
		east = new Container();
		
		startListening.setAlignmentX(Component.CENTER_ALIGNMENT);
		startListening.setName("StartListening");
		startListening.addMouseListener(this);
		
		stopListening.setAlignmentX(Component.CENTER_ALIGNMENT);
		stopListening.setName("StopListening");
		stopListening.addMouseListener(this);
		
		loadTweetsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		loadTweetsButton.setName("LoadTWSButton");
		loadTweetsButton.addMouseListener(this);
		
		loadWordlistButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		loadWordlistButton.setName("loadWordlistButton");
		loadWordlistButton.addMouseListener(this);
		
		percItaButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		percItaButton.setName("percItaButton");
		percItaButton.addMouseListener(this);
		
		loadDictionaryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		loadDictionaryButton.setName("loadDicButton");
		loadDictionaryButton.addMouseListener(this);
		
		pertDictionaryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		pertDictionaryButton.setName("pertDicButton");
		pertDictionaryButton.addMouseListener(this);
		
		zscoregraph.setAlignmentX(Component.CENTER_ALIGNMENT);
		zscoregraph.setName("zscoregraphbutton");
		zscoregraph.addMouseListener(this);
		
		printDictionaryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		printDictionaryButton.setName("printDicButton");
		printDictionaryButton.addMouseListener(this);
		
		expandDictionaryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		expandDictionaryButton.setName("expDicButton");
		expandDictionaryButton.addMouseListener(this);
		
		
		
		BoxLayout bl2 = new BoxLayout(east, BoxLayout.Y_AXIS);
		east.setLayout(bl2);
		
		
		JPanel jp = new JPanel();
		
		BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
		jp.setLayout(bl);
		TitledBorder bor = BorderFactory.createTitledBorder("Server Twitter");
		bor.setTitlePosition(TitledBorder.TOP);
		jp.setBorder(bor);
		jp.add(startListening);
		jp.add(stopListening);
		east.add(jp);
		
		
		JPanel jp2 = new JPanel();
		BoxLayout bl1 = new BoxLayout(jp2, BoxLayout.Y_AXIS);
		jp2.setLayout(bl1);
		TitledBorder bor2 = BorderFactory.createTitledBorder("Database Tweet");
		bor2.setTitlePosition(TitledBorder.TOP);
		jp2.setBorder(bor2);
		jp2.add(loadTweetsButton);
		east.add(jp2);
		
		JPanel jp3 = new JPanel();
		BoxLayout bl3 = new BoxLayout(jp3, BoxLayout.Y_AXIS);
		jp3.setLayout(bl3);
		TitledBorder bor3 = BorderFactory.createTitledBorder("Wordlist");
		bor3.setTitlePosition(TitledBorder.TOP);
		jp3.setBorder(bor3);
		jp3.add(loadWordlistButton);
		jp3.add(percItaButton);
		east.add(jp3);
		
		
		JPanel jp4 = new JPanel();
		BoxLayout bl4 = new BoxLayout(jp4, BoxLayout.Y_AXIS);
		jp4.setLayout(bl4);
		TitledBorder bor4 = BorderFactory.createTitledBorder("Umore tweet");
		bor4.setTitlePosition(TitledBorder.TOP);
		jp4.setBorder(bor4);
		jp4.add(zscoregraph);
		jp4.add(loadDictionaryButton);
		jp4.add(expandDictionaryButton);
		jp4.add(printDictionaryButton);
		jp4.add(pertDictionaryButton);
		east.add(jp4);
		
		
		add(center, BorderLayout.CENTER);
		add(east, BorderLayout.EAST);
		
		setJMenuBar(buildMenuBar());
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(getRootPane());
		setVisible(true);
		
	}
	
	public JMenuBar buildMenuBar() {
        JMenuBar menuBar;
        JMenu menu;
        JMenuItem menuItem;

        menuBar = new JMenuBar();

        //Menu file
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menu.getAccessibleContext().setAccessibleDescription("File menu");
        menuBar.add(menu);
        
        menuItem = new JMenuItem("Apri File", KeyEvent.VK_C);
        menuItem.getAccessibleContext().setAccessibleDescription("Carica File Specifici");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(this);
        menuItem.setName("openfile");
        menu.add(menuItem);
        
        menu.addSeparator();
        
        menuItem = new JMenuItem("Directory di Lavoro", KeyEvent.VK_D);
        menuItem.getAccessibleContext().setAccessibleDescription("Set Working Directory");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(this);
        menuItem.setName("set wd");
        menu.add(menuItem);
        
        menu.addSeparator();
        
        menuItem = new JMenuItem("Esci", KeyEvent.VK_E);
        menuItem.getAccessibleContext().setAccessibleDescription("Chiudi il programma");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(this);
        menuItem.setName("escidalprog");
        menu.add(menuItem);
        
        //Menu lessico
        menu = new JMenu("Tweets");
        menu.setMnemonic(KeyEvent.VK_A);
        menu.getAccessibleContext().setAccessibleDescription("Menu Tweets");
        menuBar.add(menu);
        
        menuItem = new JMenuItem("Ricerca nei tweet", KeyEvent.VK_T);
        menuItem.getAccessibleContext().setAccessibleDescription("Ricerca");
        menuItem.addActionListener(this);
        menuItem.setName("ricerca");
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Preprocess tweet per analisi rapida", KeyEvent.VK_T);
        menuItem.getAccessibleContext().setAccessibleDescription("");
        menuItem.addActionListener(this);
        menuItem.setName("preprocess");
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Svuota tweet caricati", KeyEvent.VK_T);
        menuItem.getAccessibleContext().setAccessibleDescription("Svuota buffer");
        menuItem.addActionListener(this);
        menuItem.setName("emptybuf");
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Espansione lessico con tweet contemporanei", KeyEvent.VK_T);
        menuItem.getAccessibleContext().setAccessibleDescription("Test programma");
        menuItem.addActionListener(this);
        menuItem.setName("ctc");
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Grafico andamento umore", KeyEvent.VK_T);
        menuItem.getAccessibleContext().setAccessibleDescription("Grafico Umore");
        menuItem.addActionListener(this);
        menuItem.setName("moodgraph");
        menu.add(menuItem);
        
      //Menu analisi
        menu = new JMenu("Analisi");
        menu.setMnemonic(KeyEvent.VK_H);
        menu.getAccessibleContext().setAccessibleDescription("Menu Analisi");
        menuBar.add(menu);
        
        menuItem = new JMenuItem("Utilizzo Parole Nel Tempo", KeyEvent.VK_T);
        menuItem.getAccessibleContext().setAccessibleDescription("Analizza l'utilizzo di una o piu parole nel tempo");
        menuItem.addActionListener(this);
        menuItem.setName("upnt");
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Z-Score", KeyEvent.VK_T);
        menuItem.getAccessibleContext().setAccessibleDescription("");
        menuItem.addActionListener(this);
        menuItem.setName("zscore");
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Z-Score medio per i giorni della settimana", KeyEvent.VK_T);
        menuItem.getAccessibleContext().setAccessibleDescription("");
        menuItem.addActionListener(this);
        menuItem.setName("mediazscore");
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Confronto Lessici", KeyEvent.VK_T);
        menuItem.getAccessibleContext().setAccessibleDescription("Esegue il confronto tra due lessici");
        menuItem.addActionListener(this);
        menuItem.setName("cmpdics");
        menu.add(menuItem);

        //menu aiuto
        menu = new JMenu("Aiuto");
        menu.setMnemonic(KeyEvent.VK_H);
        menu.getAccessibleContext().setAccessibleDescription("Menu Aiuto");
        menuBar.add(menu);
        
        menuItem = new JMenuItem("Info", KeyEvent.VK_T);
        menuItem.getAccessibleContext().setAccessibleDescription("Informazioni");
        menuItem.addActionListener(this);
        menuItem.setName("info");
        menu.add(menuItem);

        return menuBar;
    }
	
	/**
	 * Mostra una schermata che permette all'utente di riempire i parametri di ricerca sui tweet.
	 * Poi effettua la ricerca.
	 */
	private void showTweetSearchDialog() {
		if(tweets == null || tweets.size() == 0){
			JOptionPane.showMessageDialog(this, "Nessun tweet su cui effettuare la ricerca. Caricare almeno un file .tws", "Nessun tweet caricato!", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		SearchDialog frame = new SearchDialog(this, tweets);
		frame.setVisible(true);
	}
	
	/**
	 * Mostra una schermata di selezione permettendo all'utente di selezionare uno o piu file con estensione .tws.
	 * Poi carica tali file in memoria.
	 */
	private void loadTWSFiles(){
		fileOpenChooser.setCurrentDirectory(new File(DBManager.getWorkingDirectory()));
		int returnVal = fileOpenChooser.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			DBManager dbm = DBManager.getInstance();
			File [] files = fileOpenChooser.getSelectedFiles();
			
			long t1 = System.currentTimeMillis();
			tweets = dbm.loadTweets(files);
			long diff = System.currentTimeMillis() - t1;
			outputPanel.setText("Numero tweet nei file specificati: " + tweets.size() + " [caricati in " + (diff) + " ms]");
			
		}
	}
	
	/**
	 * Mostra una schermata di selezione permettendo all'utente di selezionare un file con estensione .txt,
	 * contenente una wordlist con le parole italiane.
	 * Poi chiama il metodo per caricare tale wordlist in memoria.
	 */
	private void loadWordlist(){
		wordlistFileChooser.setCurrentDirectory(new File(TextAnalizer.resourceDirectory));
		int returnVal = wordlistFileChooser.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				//creo una nuova istanza del TextAnalizer, con la wordlist indicata dall'utente
				TextAnalizer.getInstance(wordlistFileChooser.getSelectedFile());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void compareDictionaries(){
		cmpDicFileChooser.setCurrentDirectory(new File(TextAnalizer.resourceDirectory));
		int returnVal = cmpDicFileChooser.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				File [] files = cmpDicFileChooser.getSelectedFiles();
				if(files.length != 2){
					JOptionPane.showMessageDialog(this, "Selezionare due file!");
				} else {
					Dictionary d1, d2;
					d1 = LoadDictionary.loadDictionary(files[0]);
					d2 = LoadDictionary.loadDictionary(files[1]);
					outputPanel.setText(d1.diff(d2).toString());
				}
			} catch (WrongNumberOfValuesException e) {
				outputPanel.setText(e.toString());
			} catch (WrongAttributeValueException e) {
				outputPanel.setText(e.toString());
			} catch (IOException e) {
				outputPanel.setText(e.toString());
			} catch (DictionaryFileFormatException e) {
				outputPanel.setText(e.toString());
			} catch (DictionaryException e) {
				outputPanel.setText(e.toString());
			}
			
		}
	}
	
	private void loadDictionary(){
		wordlistFileChooser.setCurrentDirectory(new File(TextAnalizer.resourceDirectory));
		int returnVal = wordlistFileChooser.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			
			try {
				dictionary = LoadDictionary.loadDictionary((wordlistFileChooser.getSelectedFile()));
			} catch (WrongNumberOfValuesException e) {
				outputPanel.setText(e.toString());
			} catch (WrongAttributeValueException e) {
				outputPanel.setText(e.toString());
			} catch (IOException e) {
				outputPanel.setText(e.toString());
			} catch (DictionaryFileFormatException e) {
				outputPanel.setText(e.toString());
			} catch (DictionaryException e) {
				outputPanel.setText(e.toString());
			}
			
		}
	}
	
	/**
	 * Svuota la lista dei tweet precedentemente caricati in memoria leggendo uno o piu file .tws dal disco.
	 */
	private void unloadTWSFiles() {
		if(tweets == null || tweets.size() == 0)
			return;
		
		tweets.clear();
		System.gc(); //garbage collection
		JOptionPane.showMessageDialog(this, "File TWS dimenticati. Caricarne altri se si desidere effettuare delle operazioni", "Memoria liberata", JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Mostra il grafico dell'uso di certe parole nel tempo, utilizzando i tweet attualmente caricati in memoria.
	 */
	private void showTimelineWindow(){
		if(tweets == null){
			JOptionPane.showMessageDialog(this, "Caricare dei tweet prima!", "Nessun file *.tws caricato", JOptionPane.INFORMATION_MESSAGE);
			loadTWSFiles();
		}
		
		if(tweets == null){
			JOptionPane.showMessageDialog(this, "File *.tws non caricato", "Errore", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		String string = JOptionPane.showInputDialog(this,
				"Inserire parole per le quali mostrare le timeline, separate da uno spazio [max 4 parole]",
				"Parole da analizzare",
				JOptionPane.QUESTION_MESSAGE);
		
		if(string == null || string.length() == 0)
			return;
		
		String [] words = string.split(" ");
		
		if(words.length > 4){
			JOptionPane.showMessageDialog(this, "Numero massimo di parole pari a 4! Parole inserite: " + words.length, "Errore!", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		//ora faccio delle ricerche sui tweet attualmente caricati in memoria
		//in modo da passare i risultati al grafico
		long time1 = System.currentTimeMillis();
		SearchParameters spms;
		TweetSearch ts = new TweetSearch(tweets);
		List <WordTimelineTweets> timelines = new ArrayList <WordTimelineTweets>();
		
		for(int i = 0 ; i < words.length ; i++){ //per ogni parola inserita dall'utente
			try {
				spms = new SearchParameters(words[i], null, null, null, null, null, null, null); //creo dei parametri di ricerca che restituiscono i tweet contenenti tale parola
			} catch (DateFormatException e) {
				JOptionPane.showMessageDialog(this, "Dovrebbe essere impossibile vedere questo messaggio", "Errore impossibile!", JOptionPane.ERROR_MESSAGE);
				return;
			}
			//aggiungo la timeline con i tweet che contengono tale parola
			timelines.add(new WordTimelineTweets(words[i], ts.getSearchResults(spms)));
		}
		
		//infine mostro il grafico
		new WordTimelineGraph(this, timelines);
		long time2 = System.currentTimeMillis();
		
		System.out.println("Tempo per grafico utilizzo parole con "  + tweets.size() + " tweet e " + words.length + " parole richieste: " + (time2-time1) + " ms.");
	}
	
	private void expandDictionary(){
		File file;
		final String workingDirectory = System.getProperty("user.dir") + File.separator + "resources"+ File.separator +"lessico"+ File.separator +"espansi";
		
		if(tweets == null || dictionary == null || TextAnalizer.getInstance() == null){
			JOptionPane.showMessageDialog(this, "Nessun lessico e/o tweet e/o wordlist caricato!", "Errore", JOptionPane.INFORMATION_MESSAGE);
		}
		
		createNewFileChooser.setCurrentDirectory(new File(workingDirectory));
		int returnVal = createNewFileChooser.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = createNewFileChooser.getSelectedFile();
			if(!file.getAbsolutePath().endsWith(".txt")){
				file = new File(file.getAbsolutePath() + ".txt");
			}
			if(file.exists()){
				int x = JOptionPane.showConfirmDialog(this, "Il file e' esistente. Si e' sicuri di volerlo sovrascrivere?", "Attenzione", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(x != JOptionPane.YES_OPTION)
					return;
			}
		} else {
			return;
		}

		
		long t1 = System.currentTimeMillis();
		WordUsage wu = new WordUsage(this.dictionary, tweets);
		long t2 = System.currentTimeMillis();
		ExpansionParameters ep = new ExpansionParameters(wu, 0.005);
		
		long t3 = System.currentTimeMillis();
		try {
			dictionary.expand(ep).saveToFile(file.getAbsolutePath());
		} catch (DictionaryException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		long t4 = System.currentTimeMillis();
		
		outputPanel.setText("Tempo new WordUsage() con " + tweets.size() + " tweets: " + (t2-t1) + " millisecondi\n" + 
							"Tempo expand: " + (t4-t3) + " millisecondi\n");
		
	}	
	
	private void zscore() {
		if(tweets == null || dictionary == null){
			JOptionPane.showMessageDialog(this, "Nessun lessico e/o tweet caricato!", "Errore", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		String inizio = JOptionPane.showInputDialog(this, "Inserire data inizio", "Input richiesto", JOptionPane.PLAIN_MESSAGE);
		String fine = JOptionPane.showInputDialog(this, "Inserire data fine", "Input richiesto", JOptionPane.PLAIN_MESSAGE);
		
		try {
			GregorianCalendar dataInizio = DateUtils.translateStringDate(inizio);
			GregorianCalendar dataFine = DateUtils.translateStringDate(fine);
			
			dataInizio.set(GregorianCalendar.HOUR_OF_DAY, 0);
			dataInizio.set(GregorianCalendar.MINUTE, 0);
			dataInizio.set(GregorianCalendar.SECOND, 0);
			dataInizio.set(GregorianCalendar.MILLISECOND, 0);
			
			dataFine.set(GregorianCalendar.HOUR_OF_DAY, 23);
			dataFine.set(GregorianCalendar.MINUTE, 59);
			dataFine.set(GregorianCalendar.SECOND, 59);
			dataFine.set(GregorianCalendar.MILLISECOND, 999);
			
			TimePeriod tp = new TimePeriod(dataInizio, dataFine);
			
			long t1 = System.currentTimeMillis();
			ZScore zscore = null;
			try {
				zscore = new ZScore(dictionary, tweets, tp);
			} catch (ZScoreException e) {
				System.out.println(e);
			}
			long t2 = System.currentTimeMillis();
			
			System.out.println("Tempo impiegato per new ZScore(): " + (t2-t1) + " millisecondi");
			
			if(zscore != null)
				outputPanel.setText(zscore.toString());
		} catch (DateFormatException e) {
			JOptionPane.showMessageDialog(this, e.toString(), "Eccezione! Data errata", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
	}
	
	private void moodGraph(){
		if(tweets == null || dictionary == null){
			JOptionPane.showMessageDialog(this, "Nessun tweet e/o lessico caricato!", "Errore", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		Thread graphThread = new Thread(new Runnable(){

			public void run() {
				try {
					new MoodGraphFrame(new MoodData(tweets, dictionary));
				} catch (Exception e) {
					System.out.println(e);
				}			
			}
			
		});
		
		graphThread.start();
	}
	
	/**
	 * Calcola la media dello z-score per ogni giorno della settimana, per ogni attributo del lessico
	 */
	private void mediazscore() {
		if(tweets == null || dictionary == null){
			JOptionPane.showMessageDialog(this, "Nessun tweet e/o lessico caricato!", "Errore", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		double [][] media;
		double [] tweetPerGiorno;
		
		media = new double[7][dictionary.attributes.length];
		tweetPerGiorno = new double[7];
		
		for(int i = 0 ; i < 7 ; i++){
			tweetPerGiorno[i] = 0;
			for(int j = 0 ; j < media[0].length ; j++)
				media[i][j] = 0;
		}
		
		MultipleZScore mz = new MultipleZScore(dictionary, tweets);
		
		for(SingleZScore zscore : mz.zscores){
			int giorno_settimana = zscore.dayOfWeek;
			tweetPerGiorno[giorno_settimana]++;
			media[giorno_settimana] = ArrayMath.arraySum(media[giorno_settimana], zscore.Z);
		}

		for(int i = 0 ; i < 7 ; i++){
			if(tweetPerGiorno[i] != 0){
				media[i] = ArrayMath.arrayDivide(media[i], tweetPerGiorno[i]);
			} else {
				System.out.println("Strano errore in MainWindow.mediazscore()");
			}
		}
		
		
		DecimalFormat df = new DecimalFormat("#.####");
		for(int j = 0 ; j < media[0].length ; j++){
			System.out.print(dictionary.attributes[j] + ": ");
			for(int i = 1 ; i < 7 ; i++){
				System.out.print(df.format(media[i][j]) + " "); //lunedi..sabato
			}
			System.out.print(df.format(media[0][j]) + " | "); //domenica
			System.out.println();
		}
		
	}

	private void preprocessTweets(){
		if(TextAnalizer.getInstance() == null){
			JOptionPane.showMessageDialog(this, "Nessuna wordlist caricata!", "Errore", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		File destFile;
		File [] sourceFiles;
		
		fileOpenChooser.setCurrentDirectory(new File(DBManager.getWorkingDirectory()));
		int returnVal = fileOpenChooser.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			sourceFiles = fileOpenChooser.getSelectedFiles();			
		} else {
			JOptionPane.showMessageDialog(this, "Nessun file di origine selezionato!", "Errore", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		createNewTwsChooser.setCurrentDirectory(new File(DBManager.getWorkingDirectory()));
		returnVal = createNewTwsChooser.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			destFile = createNewTwsChooser.getSelectedFile();
			if(!destFile.getAbsolutePath().endsWith(".tws")){
				destFile = new File(destFile.getAbsolutePath() + ".tws");
			}
			if(destFile.exists()){
				int x = JOptionPane.showConfirmDialog(this, "Il file " + destFile.getName() + " esistente. Si e' sicuri di volerlo sovrascrivere?",
													  "Attenzione", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(x != JOptionPane.YES_OPTION)
					return;
			}
		} else {
			JOptionPane.showMessageDialog(this, "Nessun file di destinazione selezionato!", "Errore", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		DBManager dbm = DBManager.getInstance();
		
		if(dbm==null){
			JOptionPane.showMessageDialog(this, "Impossibile caricare il database manager [DBManager.java]!", "Errore interno", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		dbm.preprocessTWSFile(TextAnalizer.getInstance(), sourceFiles, destFile);
	}

	public void actionPerformed(ActionEvent e) {
		JMenuItem source = (JMenuItem)(e.getSource());
		String name=source.getName();
		
		if(name.equals("info")){
			JOptionPane.showMessageDialog(null,
					"Autore: Simone Russo\nEmail: simone.russo89@gmail.com\nVersion: "+ VERSION +"\nCompiled: " + COMPILATION_TIME,
					"Informazioni",
					JOptionPane.INFORMATION_MESSAGE);
		} else if(name.equals("openfile")){
			loadTWSFiles();        	
		} else if(name.equals("set wd")){
			int returnVal = currentDirectoryChooser.showOpenDialog(this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File dir = currentDirectoryChooser.getSelectedFile();
				if(dir != null){
					DBManager.setWorkingDirectory(dir.getAbsolutePath());
				}
			}
		} else if(name.equals("escidalprog")){
			if(tweetManager.isListening())
				tweetManager.stopListening();
			
			dispose();
			System.exit(0);
		} else if(name.equals("upnt")){
			showTimelineWindow();			
		} else if(name.equals("cmpdics")){
			compareDictionaries();
		}  else if(name.equals("preprocess")){
			preprocessTweets();
		} else if(name.equals("ricerca")){
			showTweetSearchDialog();
		} else if(name.equals("emptybuf")){
			unloadTWSFiles();
		} else if(name.equals("ctc")){
			if(dictionary == null || tweets == null){
				JOptionPane.showMessageDialog(this, "Nessuna lessico e/o tweet caricato!", "Errore", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			dictionary.runExpansionTests(tweets);
		} else if(name.equals("zscore")){
			zscore();
		} else if(name.equals("mediazscore")){
			long time1 = System.currentTimeMillis();
			mediazscore();
			long time2 = System.currentTimeMillis();
			System.out.println("Zscore multiplo con " + tweets.size() + " tweet: " + (time2-time1) + "ms.");
		} else if(name.equals("moodgraph")){
			moodGraph();
		}
		
	}

	public void mouseClicked(MouseEvent e) {
		Component comp = e.getComponent();
		String cname = comp.getName();
		
		if(cname==null)
			return;
		
		if(cname.equals(startListening.getName())){
			tweetManager.startListening();
		} else if(cname.equals(stopListening.getName())){
			tweetManager.stopListening();
		} else if(cname.equals(loadTweetsButton.getName())){
			loadTWSFiles();
		} else if(cname.equals(loadWordlistButton.getName())){
			loadWordlist();
		} else if(cname.equals(percItaButton.getName())){
			if(tweets == null){
				JOptionPane.showMessageDialog(this, "Caricare dei tweet prima!", "Nessun file *.tws caricato", JOptionPane.INFORMATION_MESSAGE);
				loadTWSFiles();
			}
			
			if(tweets == null){
				JOptionPane.showMessageDialog(this, "File *.tws non caricato", "Errore", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			TextAnalizer ta = TextAnalizer.getInstance();
			if(ta == null){
				JOptionPane.showMessageDialog(this, "Caricare una wordlist prima!", "Wordlist non caricata", JOptionPane.INFORMATION_MESSAGE);
				loadWordlist();
			}
			ta = TextAnalizer.getInstance();
			if(ta == null){
				JOptionPane.showMessageDialog(this, "Wordlist non caricata", "Errore", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			JOptionPane.showMessageDialog(this, "Percentuale di tweet riconosciuti essere in italiano: " + ta.percItalian(tweets) + "%", "Tweet Italiani", JOptionPane.INFORMATION_MESSAGE);
		} else if(cname.equals(loadDictionaryButton.getName())){
			loadDictionary();
		} else if(cname.equals(printDictionaryButton.getName())){
			if(dictionary != null){
				outputPanel.setText(dictionary.toString());
			} else {
				JOptionPane.showMessageDialog(this, "Caricare un lessico prima!", "Lessico non caricato", JOptionPane.INFORMATION_MESSAGE);
			}
		} else if(cname.equals(expandDictionaryButton.getName())){
			expandDictionary();
		}  else if(cname.equals(zscoregraph.getName())){
			zscoregraph();
		} else if(cname.equals(pertDictionaryButton.getName())){
			perturbazione();
		}
	}

	private void perturbazione() {
		if(dictionary == null){
			JOptionPane.showMessageDialog(this, "Nessun lessico caricato!", "Errore", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		String x;
		double perturb = -1;
		
		do{
			x = JOptionPane.showInputDialog(null, "Valore perturbazione [0, 1]: ");
			
			try{
				perturb = Double.parseDouble(x);
			} catch(NumberFormatException e) {
				e.printStackTrace();
				return;
			}
		} while(x != null && perturb < 0);
		
		
		Dictionary newDic = dictionary.addPerturbation(perturb);
		DecimalFormat df = new DecimalFormat("#.##");
		
		try {
			newDic.saveToFile("resources/newDic - "+ df.format(perturb) +".txt");
		} catch (IOException e) {
			
		}
	}

	private void zscoregraph() {
		//ZScoreGraphFrame frame =
				new ZScoreGraphFrame(new ZScoreForGraph(dictionary, tweets));
	}

	public void mouseEntered(MouseEvent arg0) {
		
	}

	public void mouseExited(MouseEvent arg0) {
		
	}

	public void mousePressed(MouseEvent arg0) {
		
	}
	
	public void mouseReleased(MouseEvent arg0) {
		
	}

	public void windowActivated(WindowEvent arg0) {
		
	}

	public void windowClosed(WindowEvent arg0) {
		
	}

	public void windowClosing(WindowEvent arg0) {
		if(tweetManager.isListening())
			tweetManager.stopListening();
		dispose();
	}

	public void windowDeactivated(WindowEvent arg0) {
		
	}

	public void windowDeiconified(WindowEvent arg0) {
		
	}

	public void windowIconified(WindowEvent arg0) {
		
	}

	public void windowOpened(WindowEvent arg0) {
		
	}
	
	public void setOutput(String output){
		outputPanel.setText(output);
	}
	
	public List<SingleTweet> getTweets(){
		return this.tweets;
	}
	
	public static void main(String[] args) throws TwitterException, IOException{
		
		new MainWindow();
		
	}
	
	/**
	 * Eseguita [chiamata da SearchDialog] dopo che sono stati ricevuti i risultati di una ricerca.
	 * @param results
	 */
	public void onSearchResultsReceived(List <SingleTweet> results){		
		TextAnalizer ta = TextAnalizer.getInstance();
		
		outputPanel.setText("");
		outputPanel.append("Risultati ricerca:\n");
		Iterator <SingleTweet> i = results.iterator();
		while(i.hasNext()){
			SingleTweet t = i.next();
			outputPanel.append("Utente: " + t.user + "\n");
			outputPanel.append("Localita: " + t.place + "\n");
			outputPanel.append("Sorgente: " + t.source + "\n");
			outputPanel.append("Tweet: " + t.text + "\n");
			if(ta != null){
				boolean ita = ta.isTextItalian(t.text);
				if(ita){
					outputPanel.append("Tweet italiano.\n");
					if(dictionary != null)
						outputPanel.append("Umore tweet: " + dictionary.getTweetMoodAsString(t.text) +"\n\n");
					else outputPanel.append("\n");
				} else {
					outputPanel.append("Tweet in lingua straniera.\n\n");
				}
			} else {
				outputPanel.append("\n");
			}
		}
	}
}